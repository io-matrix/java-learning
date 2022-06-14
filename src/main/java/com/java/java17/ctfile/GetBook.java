package com.java.java17.ctfile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.java.java17.awss3.AmazonS3ClientUtil;
import com.java.java17.utils.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.exception.ZipException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class GetBook {
    public static volatile AtomicInteger count = new AtomicInteger(0);
    private static final int BUFFER_SIZE = 2 * 1024;
    static String jsonFilePath = "D:\\book\\books.json";
    static String downloadFilePath = "D:\\book\\";
    static String unzipBooksPath = "D:\\books\\";
    static String bookPath = "D:\\bookzip\\";

    static String ENDPOINT = "";
    static String AK = "";
    static String SK = "";
    static String bucket = "fenix";
    static String prefix = "books/";

    static volatile ConcurrentLinkedQueue<String> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();

    static String[] USER_AGENT = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.%(rnd)d.100 Safari/537.36 OPR/48.0.2685.52",
            "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:56.0) Gecko/20100101 Firefox/56.0",
            "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/533.20.25 (KHTML, like Gecko) Version/5.0.4 Safari/533.20.27",
            "Mozilla/5.0 (X11; U; Linux x86_64; en-us) AppleWebKit/531.2+ (KHTML, like Gecko) Version/5.0 Safari/531.2",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/604.3.5 (KHTML, like Gecko) Version/11.0.1 Safari/604.3.5",
            "Mozilla/5.0 (OS/2; U; Warp 4.5; en-US; rv:1.7.12) Gecko/20050922 Firefox/1.0.7",
            "Mozilla/5.0 (X11; FreeBSD amd64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.%(rnd)d.115 Safari/537.36",
            "Mozilla/5.0 (X11; U; FreeBSD i386; zh-tw; rv:31.0) Gecko/20100101 Opera/13.0",
            "Mozilla/5.0 (X11; FreeBSD amd64; rv:40.0) Gecko/20100101 Firefox/40.0",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.%(rnd)d.62 Safari/537.36"
    };

    static {
        for (String s : USER_AGENT) {
            concurrentLinkedQueue.add(s);
        }
    }

    public static void main(String[] args) {

//        本机


//        测试服务器
//        String jsonFilePath = "/root/fenix/books.json";
//        String downloadFilePath = "/root/fenix/download/";
//        String booksPath = "/root/fenix/books/";

        ExecutorService executorService = Executors.newFixedThreadPool(6);
        AmazonS3 s3Client = AmazonS3ClientUtil.getAwsS3Client(AK, SK, ENDPOINT);

        try {
            // 读取json文件
            String bookJson = readJsonFile(jsonFilePath);
            List<Book> bookList = JSONArray.parseArray(bookJson, Book.class);
            for (int i = 0; i < bookList.size(); i++) {
                Book book = bookList.get(i);
                String bookName = book.getBookName();


                book.setBookName(bookName.replace(" ", "")
                        .replace("：", ":")
                        .replace("？", "?")
                );


                try {
                    ObjectMetadata objectMetadata = s3Client.getObjectMetadata(bucket, prefix + book.getBookName() + ".zip");
//                    log.info(objectMetadata.getRawMetadata().toString());
                    continue;
                } catch (AmazonS3Exception e) {
                    if (e.getMessage().contains("Not Found (Service: Amazon S3; Status Code: 404; Error Code: 404 Not Found")) {
                        log.info("{} 文件不存在，重新上传", bookName);
                    }
                }

                executorService.execute(() -> {
                    try {
                        run(book);
                    } catch (InterruptedException e) {
                        log.info("error:", e);
                    }
                });
            }
        } catch (Exception e) {
            log.info("读取json文件异常：", e);
        }
    }


    public static void run(Book book) throws InterruptedException {

        String bookName = book.getBookName();
        bookName = bookName.replace(":", "_").replace("：", "_");
        if (bookName.contains("(")) {
            String[] split = bookName.split("\\(");
            bookName = split[0];
        }

        if (bookName.contains("（")) {
            String[] split = bookName.split("（");
            bookName = split[0];
        }

        book.setBookName(bookName);
        String userAgent = concurrentLinkedQueue.poll();

        int pollCount = 0;
        while (userAgent == null && pollCount < 3) {
            pollCount++;
            Thread.sleep(500);
            userAgent = concurrentLinkedQueue.poll();
        }

        log.info("book name: {}", book.getBookName());
        String[] split = book.getBookShareUrl().split("/");
        String fileId = split[split.length - 1];
        String filePwd = book.getBookSharePwd();
        String sourceUrl = "https://webapi.ctfile.com/getfile.php?path=f&f=" + fileId + "&passcode=" + filePwd + "&token=false&r=0.0001";

        // 获取 ctfile file_chk
        String fileInfoJson = getReq(sourceUrl, userAgent);
        JSONObject jsonObject = JSON.parseObject(fileInfoJson);
        if (!jsonObject.getString("code").equals("200")) {
            concurrentLinkedQueue.add(userAgent);
            return;
        }
        JSONObject file = jsonObject.getJSONObject("file");
        String fileChk = file.getString("file_chk");
        String[] fu = fileId.split("-");
        String uid = fu[0];
        String fid = fu[1];
        String getDownloadUrl = "https://webapi.ctfile.com/get_file_url.php?uid=" + uid + "&fid=" + fid + "&folder_id=0&file_chk=" + fileChk + "&mb=1&app=0&acheck=1&verifycode=" + filePwd + "&rd=0.11781640049442932";

        // 获取下载链接
        String downloadUrl = "";

        while (downloadUrl.equals("")) {
            String downloadUrlJson = getReq(getDownloadUrl, userAgent);
            log.info(downloadUrlJson);

            JSONObject downloadUrlJsonObj = JSON.parseObject(downloadUrlJson);

            downloadUrl = downloadUrlJsonObj.getString("downurl");
            if (downloadUrl == null) {
                downloadUrl = "";
                Thread.sleep(200);
            }
            log.info(downloadUrl);
        }

        Thread.sleep(200);
        // 下载文件
        try {
            boolean success = downloadFile(downloadUrl, downloadFilePath, book.getBookName() + ".zip");
            concurrentLinkedQueue.add(userAgent);
            if (!success) {
                log.info("下载失败：useragent : {}", userAgent);
                return;
            }
        } catch (IOException e) {
            concurrentLinkedQueue.add(userAgent);
            log.info("下载失败：useragent : {}", userAgent);
            return;
        }
        Thread.sleep(500);
        // 解压缩文件
        try {
            ZipUtil.unZip(downloadFilePath + book.getBookName() + ".zip", unzipBooksPath + book.getBookName(), book.getBookZipPwd());
        } catch (ZipException e) {
            log.info(bookName + "解压缩异常： ", e);
            return;
        }

        delZip(downloadFilePath + book.getBookName() + ".zip");

        try {
            FileOutputStream fosZip = new FileOutputStream(new File(bookPath + book.getBookName() + ".zip"));
            ZipUtil.toZip(unzipBooksPath + book.getBookName(), fosZip, true);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // 删除zip
        delZip(downloadFilePath + book.getBookName() + ".zip");
        // 删除解压的文件夹
        delDir(unzipBooksPath + book.getBookName());

//        fileMove(unzipBooksPath + book.getBookName(), bookPath + book.getBookName());
        int c = count.incrementAndGet();

        Thread.sleep(100);
        log.info(c + ": success");
    }

    public static void delZip(String path) {
        File file = new File(path);
        file.delete();
    }

    public static void delDir(String path) {
        File dir = new File(path);

        if (dir.isDirectory() == false) {
            log.info("Not a directory. Do nothing");
            return;
        }
        File[] listFiles = dir.listFiles();
        for (File file : listFiles) {
            file.delete();
        }
        //now directory is empty, so we can delete it
        log.info("Deleting Directory " + path + ". Success = " + dir.delete());
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

    public static String getReq(String sourceUrl, String userAgent) {
        OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true).connectTimeout(30, TimeUnit.SECONDS).build();
        ;
        Request request = new Request.Builder()
                .url(sourceUrl)
                .addHeader("User-Agent", userAgent)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            log.info("get 请求异常：", e);
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
            log.info("获取文件流异常：", e);
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

    public static boolean download(String downloadUrl, String path, String fileName) throws IOException {
        OutputStream os = null;
        InputStream is = null;
        boolean result = false;
        int count = 0;
        log.info(fileName + " 开始下载");
        while (!result && count < 3) {
            count++;
            try {
                // create a url object
                URL url = new URL(downloadUrl);
                // connection to the file
                URLConnection connection = url.openConnection();
                // get input stream to the file
                is = connection.getInputStream();
                // get output stream to download file
                os = new FileOutputStream(path + File.separator + fileName);
                final byte[] b = new byte[BUFFER_SIZE];
                int length;
                // read from input stream and write to output stream
                while ((length = is.read(b)) != -1) {
                    os.write(b, 0, length);
                }
                result = true;
                log.info(fileName + " 完成下载");
            } catch (IOException e) {
                result = false;
                log.info(fileName + "下载异常：", e);
            }
            os.close();
            is.close();
        }
        return result;

    }


    public static boolean downloadFile(String downloadUrl, String path, String fileName) throws IOException, InterruptedException {
        boolean result = false;
        int count = 0;

        OutputStream os = null;
        InputStream inputStream = null;
        while (!result && count < 3) {
            OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true).connectTimeout(30, TimeUnit.SECONDS).build();
            Request request = new Request.Builder()
                    .url(downloadUrl)
                    .build();
            count++;
            try (Response response = client.newCall(request).execute()) {
                inputStream = response.body().byteStream();

                os = new FileOutputStream(path + File.separator + fileName);
                log.info("开始下载");
                byte[] bytes = new byte[BUFFER_SIZE];
                int length;
                while ((length = inputStream.read(bytes)) != -1) {
                    os.write(bytes, 0, length);
                }
                log.info("下载完成");
                result = true;
            } catch (Exception e) {
                log.info("{} 重试次数 {} 下载异常： ", fileName, count, e);
                result = false;
            }
            os.close();
            inputStream.close();
            Thread.sleep(100);
        }
        return result;
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
