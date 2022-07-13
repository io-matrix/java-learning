package com.fenix.java.ctfile;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.fenix.java.utils.DownLoadUtil;
import com.fenix.java.utils.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.exception.ZipException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class GetBook {

    static Logger failLog = LoggerFactory.getLogger("failLog");
    public static volatile AtomicInteger count = new AtomicInteger(0);
    private static final int BUFFER_SIZE = 2 * 1024;
    static String jsonFilePath = "D:\\book\\books.json";
    static String downloadFilePath = "/Users/feng/book/down/";
    static String unzipBooksPath = "/Users/feng/book/unzip/";
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
        String jsonFilePath = "/Users/feng/book/select.json";
        String downloadFilePath = "/root/fenix/book/";
        String booksPath = "/root/fenix/book/unzip";

        ExecutorService executorService = Executors.newFixedThreadPool(6);
//        AmazonS3 s3Client = AmazonS3ClientUtil.getAwsS3Client(AK, SK, ENDPOINT);

        try {
            // 读取json文件
//            String bookJson = readJsonFile(jsonFilePath);
//            List<Book> bookList = JSONArray.parseArray(bookJson, Book.class);

            List<Book> bookList = new ArrayList<>();
            List<String> bookStringList = FileUtil.readLines("/Users/feng/book/select.json", StandardCharsets.UTF_8);

            for (String s : bookStringList) {

                Book book = JSON.parseObject(s, Book.class);
                bookList.add(book);
            }
            int num = 102906;
            for (int i = 0; i < bookList.size(); i++) {
                num++;
                Book book = bookList.get(i);
                String bookName = book.getBookName();


                try {
//                    ObjectMetadata objectMetadata = s3Client.getObjectMetadata(bucket, prefix + book.getBookName() + ".zip");
//                    log.info(objectMetadata.getRawMetadata().toString());
//                    continue;
                } catch (AmazonS3Exception e) {
                    if (e.getMessage().contains("Not Found (Service: Amazon S3; Status Code: 404; Error Code: 404 Not Found")) {
                        log.info("{} 文件不存在，重新上传", bookName);
                    }
                }

                int finalNum = num;
                executorService.execute(() -> {
                    try {
                        run(book, finalNum);
                    } catch (InterruptedException e) {
                        log.info("error:", e);
                    }
                });


            }
            executorService.shutdown();
        } catch (Exception e) {
            log.info("读取json文件异常：", e);
        }
    }


    public static void run(Book book, int num) throws InterruptedException {

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
            boolean success = DownLoadUtil.downloadFile(downloadUrl, downloadFilePath, num + ".zip");
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
            ZipUtil.unZip(downloadFilePath + num + ".zip", unzipBooksPath + num, book.getBookZipPwd(), num);
        } catch (ZipException e) {
            log.info(bookName + "解压缩异常： ", e);
            if (e.getMessage().contains("Wrong Password")) {
                failLog.info(book.get_id());
            }
            return;
        }


        delZip(downloadFilePath + num + ".zip");
//
//        try {
//            FileOutputStream fosZip = new FileOutputStream(new File(bookPath + book.getBookName() + ".zip"));
//            ZipUtil.toZip(unzipBooksPath + book.getBookName(), fosZip, true);
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//
//        // 删除zip
//        delZip(downloadFilePath + book.getBookName() + ".zip");
//        // 删除解压的文件夹
//        delDir(unzipBooksPath + book.getBookName());

//        fileMove(unzipBooksPath + book.getBookName(), bookPath + book.getBookName());
        int c = count.incrementAndGet();

        Thread.sleep(100);
        log.info(num + ": success");
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
