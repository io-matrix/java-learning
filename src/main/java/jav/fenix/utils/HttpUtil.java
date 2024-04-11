package jav.fenix.utils;

import okhttp3.*;

import java.io.IOException;

/**
 * @author feng
 * @desc 描述
 * @date 2022/12/21 09:52
 * @since v1
 */
public class HttpUtil {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    final static OkHttpClient client = new OkHttpClient();

    public static String post(String url, String json, Headers headers) throws IOException {

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

}
