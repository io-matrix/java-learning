package jav.fenix.alishare;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jav.fenix.ctfile.Book;
import jav.fenix.utils.HttpUtil;
import jav.fenix.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author feng
 * @desc 描述
 * @date 2022/12/20 22:36
 * @since v1
 */
@Slf4j
public class Share {

    public static void main(String[] args) {
        try {
            listShare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void listShare() throws IOException {
        List<Items> result = new ArrayList<>();
        Map<String, String> map = new HashMap<>(8000);

        String url = "https://api.aliyundrive.com/adrive/v3/share_link/list";
        String updateUrl = "https://api.aliyundrive.com/v3/file/update";

        Map<String, Object> req = new HashMap<>();
        req.put("category", "file,album");
        req.put("creator", "f125dd7c182d42e6a46ff465c3f6ee76");
        req.put("include_canceled", false);
        req.put("limit", 1000);
        req.put("order_by", "share_name");
        req.put("order_direction", "DESC");

        String nextMarker = "";

        Headers headers = new Headers.Builder()
                .add("authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJmMTI1ZGQ3YzE4MmQ0MmU2YTQ2ZmY0NjVjM2Y2ZWU3NiIsImN1c3RvbUpzb24iOiJ7XCJjbGllbnRJZFwiOlwiMjVkelgzdmJZcWt0Vnh5WFwiLFwiZG9tYWluSWRcIjpcImJqMjlcIixcInNjb3BlXCI6W1wiRFJJVkUuQUxMXCIsXCJTSEFSRS5BTExcIixcIkZJTEUuQUxMXCIsXCJVU0VSLkFMTFwiLFwiVklFVy5BTExcIixcIlNUT1JBR0UuQUxMXCIsXCJTVE9SQUdFRklMRS5MSVNUXCIsXCJCQVRDSFwiLFwiT0FVVEguQUxMXCIsXCJJTUFHRS5BTExcIixcIklOVklURS5BTExcIixcIkFDQ09VTlQuQUxMXCIsXCJTWU5DTUFQUElORy5MSVNUXCIsXCJTWU5DTUFQUElORy5ERUxFVEVcIl0sXCJyb2xlXCI6XCJ1c2VyXCIsXCJyZWZcIjpcImh0dHBzOi8vd3d3LmFsaXl1bmRyaXZlLmNvbS9cIixcImRldmljZV9pZFwiOlwiZTk4YzhjNjg0NTQxNGE0OGJkNmUxNzkxMDc0NzIyYWZcIn0iLCJleHAiOjE2NzE2MDEyNDUsImlhdCI6MTY3MTU5Mzk4NX0.Qx6uT9oGeaur3w_LRfjtPIap20xwCOBIJplgE_CFZSIATEbh2PsALKQlqPdJmBkqYHoxuhdcVGvkQPYv_IxAGprZmn8xUIVJ0dlDdH7CL9CQbLkQWp-2hC8ihSxM6yktBzUCQ1rgi4Wo5y0-12a7nm7ExPhLRrVq0jXv9672K8Q")
                .add("x-signature", "67c75280b60836392ec914a8682e338b5391f70ee12ed02fe331d62f0b445843698ca3872328f87a193abd69889e84edfa0fe89f73f0fa7bd6424f644ed0fca500")
                .build();

        do {
            if (StrUtil.isNotEmpty(nextMarker)) {
                req.put("marker", nextMarker);
            }
            String post = HttpUtil.post(url, JSONUtil.toJsonStr(req), headers);
            ShareListDto shareListDto = ObjectMapperUtils.fromJSON(post, ShareListDto.class);
            List<Items> items = shareListDto.getItems();
            result.addAll(items);
            nextMarker = shareListDto.getNext_marker();
        } while (StrUtil.isNotEmpty(nextMarker));

        log.info("总分享数：{}", result.size());

        for (Items items : result) {
            map.put(items.getShare_url(), items.getFile_id());
        }

        List<String> bookStringList = FileUtil.readLines("/Users/feng/book/wechart.json", StandardCharsets.UTF_8);

        List<Book> bookList = new ArrayList<>();
        for (String bookString : bookStringList) {
            Book book = com.alibaba.fastjson2.JSON.parseObject(bookString, Book.class);
            String fileId = map.get(book.getBookShareUrl());
            if (StrUtil.isEmpty(fileId)) {
                continue;
            }
            Map<String, String> updateReq = new HashMap<>();
            updateReq.put("check_name_mode", "refuse");
            updateReq.put("drive_id", "2440155");
            updateReq.put("file_id", fileId);
            updateReq.put("name", book.getBookName());

            String post = HttpUtil.post(updateUrl, ObjectMapperUtils.toJSON(updateReq), headers);
            if (post.contains("Too many requests")) {
                log.info("请求过于频繁");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }


//        for (Book book : bookList) {
//            FileUtil.appendString(JSON.toJSONString(book) + "\n", "/Users/feng/book/wechart2.json", StandardCharsets.UTF_8);
//        }

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


}
