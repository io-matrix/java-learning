package com.java.java17.ctfile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class GetBook {
    public static volatile AtomicInteger count = new AtomicInteger(0);
    static String jsonFilePath = "D:\\book\\books.json";
    static String downloadFilePath = "D:\\book\\";
    static String unzipBooksPath = "D:\\books\\";
    static String bookPath = "X:\\books\\";

    public static void main(String[] args) {

//        本机


//        测试服务器
//        String jsonFilePath = "/root/fenix/books.json";
//        String downloadFilePath = "/root/fenix/download/";
//        String booksPath = "/root/fenix/books/";
        ExecutorService executorService = Executors.newFixedThreadPool(12);

        try {
            // 读取json文件
            String bookJson = readJsonFile(jsonFilePath);
            List<Book> bookList = JSONArray.parseArray(bookJson, Book.class);
            for (int i = 0; i < bookList.size(); i++) {
                Book book = bookList.get(i);
                run(book);
//                executorService.execute(() -> {
//                    run(book);
//                });
            }


            while (count.get() < 130) {
                log.info("wait");
                Thread.sleep(10000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void run(Book book) {
        log.info("book name: {}", book.getBookName());
        String[] split = book.getBookShareUrl().split("/");
        String fileId = split[split.length - 1];
        String filePwd = book.getBookSharePwd();
        String sourceUrl = "https://webapi.ctfile.com/getfile.php?path=f&f=" + fileId + "&passcode=" + filePwd + "&token=false&r=0.0001";

        // 获取 ctfile file_chk
        String fileInfoJson = getReq(sourceUrl);
        JSONObject jsonObject = JSON.parseObject(fileInfoJson);
        JSONObject file = jsonObject.getJSONObject("file");
        String fileChk = file.getString("file_chk");
        String[] fu = fileId.split("-");
        String uid = fu[0];
        String fid = fu[1];
        String getDownloadUrl = "https://webapi.ctfile.com/get_file_url.php?uid=" + uid + "&fid=" + fid + "&folder_id=0&file_chk=" + fileChk + "&mb=0&app=0&acheck=1&verifycode=" + filePwd + "&rd=0.11781640049442932";

        // 获取下载链接
        String downloadUrl = "";

        while (downloadUrl.equals("")) {
            String downloadUrlJson = getReq(getDownloadUrl);
            log.info(downloadUrlJson);

            JSONObject downloadUrlJsonObj = JSON.parseObject(downloadUrlJson);

            downloadUrl = downloadUrlJsonObj.getString("downurl");
            if (downloadUrl == null) {
                downloadUrl = "";
            }
            log.info(downloadUrl);
        }

        String bookName = book.getBookName();
        if (bookName.contains(" ")) {
            book.setBookName(bookName.replace(" ", ""));
        }

        // 下载文件
        downloadFile(downloadUrl, downloadFilePath, book.getBookName() + ".zip");

        // 解压缩文件
        unZip(downloadFilePath + book.getBookName() + ".zip", unzipBooksPath + book.getBookName(), book.getBookZipPwd());

        delZip(downloadFilePath + book.getBookName() + ".zip");

        fileMove(unzipBooksPath + book.getBookName(), bookPath + book.getBookName());

        int c = count.incrementAndGet();
        log.info(c + ": success");
    }

    public static void delZip(String path) {
        File file = new File(path);
        file.delete();
    }


    public static String readJsonFile(String path) throws Exception {
        StringBuilder jsonBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String json = "";
        while ((json = reader.readLine()) != null) {
            jsonBuilder.append(json);
        }
        return jsonBuilder.toString();
    }

    public static String getReq(String sourceUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(sourceUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static InputStream getFileStream(String sourceUrl) {
        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url(sourceUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().byteStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void downLoadFromUrl(String urlStr, String fileName, String savePath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);
        //文件保存位置
        File saveDir = new File(savePath);

        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static boolean downloadFile(String downloadUrl, String path, String fileName) {
        boolean result;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            InputStream inputStream = response.body().byteStream();

            OutputStream os = new FileOutputStream(path + File.separator + fileName);
            log.info("开始下载");
            byte[] bytes = new byte[1024];
            int length;
            while ((length = inputStream.read(bytes)) != -1) {
                os.write(bytes, 0, length);
            }
            log.info("下载完成");
            os.close();
            inputStream.close();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * @param source   原始文件路径
     * @param dest     解压路径
     * @param password 解压文件密码(可以为空)
     */
    public static void unZip(String source, String dest, String password) {
        try {
            File zipFile = new File(source);
            // 首先创建ZipFile指向磁盘上的.zip文件
            ZipFile zFile = new ZipFile(zipFile);
            zFile.setFileNameCharset("GBK");

            // 解压目录
            File destDir = new File(dest);
            if (!destDir.exists()) {
                // 目标目录不存在时，创建该文件夹
                destDir.mkdirs();
            }
            if (zFile.isEncrypted()) {
                // 设置密码
                zFile.setPassword(password.toCharArray());
            }
            // 将文件抽出到解压目录(解压)
            zFile.extractAll(dest);
            List<FileHeader> headerList = zFile.getFileHeaders();
            List<File> extractedFileList = new ArrayList<File>();
            for (FileHeader fileHeader : headerList) {
                if (!fileHeader.isDirectory()) {
                    extractedFileList.add(new File(destDir, fileHeader.getFileName()));
                }
            }
            File[] extractedFiles = new File[extractedFileList.size()];
            extractedFileList.toArray(extractedFiles);
            for (File f : extractedFileList) {
                log.info(f.getAbsolutePath() + "文件解压成功!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 移动文件
     * from 从哪
     * to   到哪
     */
    public static int fileMove(String from, String to) {
        try {
            File dir = new File(from);
            File[] files = dir.listFiles();
            if (files == null) {
                return -1;
            }
            File moveDir = new File(to);
            if (!moveDir.exists()) {
                moveDir.mkdirs();
                log.info("已新建一个目标移动文件夹");
            }
            for (int i = 0; i < files.length; i++) {
                log.info("files[i].isDirectory()：" + files[i].isDirectory());
                if (files[i].isDirectory()) {
                    fileMove(files[i].getPath(),
                            to + dir.separator + files[i].getName());
                    files[i].delete();
                }
                File moveFile = new File(moveDir.getPath() + dir.separator
                        + files[i].getName());
                if (moveFile.exists()) {
                    moveFile.delete();
                }
                files[i].renameTo(moveFile);
            }
            log.info("文件移动成功！");
        } catch (Exception e) {
            log.info("移动文件出现异常，异常信息为[" + e.getMessage() + "]");
            return -1;
        }
        return 0;
    }


}
