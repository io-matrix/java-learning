package jav.fenix.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DownLoadUtil {

    static int BUFFER_SIZE = 2 * 1024;

    /**
     * 使用okhttp 下载
     *
     * @param downloadUrl 下载地址
     * @param path        本地路径
     * @param fileName    本地文件名
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
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
            if (null != os) {
                os.close();
            }
            if (null != inputStream) {
                inputStream.close();
            }
        }
        return result;
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


    /**
     * 使用Urlconnect 下载
     *
     * @param downloadUrl
     * @param path
     * @param fileName
     * @return
     * @throws IOException
     */
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


}
