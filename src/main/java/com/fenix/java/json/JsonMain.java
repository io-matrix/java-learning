package com.fenix.java.json;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class JsonMain {

    public static void main(String[] args) {
        List<String> jsons = FileUtil.readLines("/Users/feng/Downloads/json.log", StandardCharsets.UTF_8);
        int count = 0;
        int fiveTotal = 0;
        int fiveError = 0;
        int total = 0;
        int error = 0;
        for (String json : jsons) {


            JSONObject jsonObject = JSON.parseObject(json);
            String message = jsonObject.getString("message");
            JSONObject jsonMsg = JSON.parseObject(message);

            Integer totalReq = jsonMsg.getInteger("totalReq");
            Integer errorReq = jsonMsg.getInteger("errorReq");

            fiveTotal += totalReq;
            fiveError += errorReq;

            count++;

            total += totalReq;
            error += errorReq;
            if (count == 5) {
                System.out.println("总数: " + fiveTotal + "; 失败: " + fiveError);
                count = 0;
                fiveTotal = 0;
                fiveError = 0;
            }

        }

        System.out.println(total + ", " + error);

    }

}
