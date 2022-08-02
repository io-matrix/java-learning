package com.fenix.java.file;

import cn.hutool.core.io.FileUtil;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MakeFile {

    public static String basePath = "/opt/fenix/testfile/";
    public static String[] month = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

    public static void makeFile() {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 22; i > 12; i--) {

            int finalI = i;
            executorService.execute(() -> {
                String year = "20" + finalI;
                for (int j = 0; j < 10000000; j++) {
                    String file = basePath + year + "/" + month[j % 12] + "/" + "test" + j + ".txt";
                    FileUtil.writeString("1", file, StandardCharsets.UTF_8);
                }
            });

        }

    }

    public static void main(String[] args) {
        makeFile();
    }
}
