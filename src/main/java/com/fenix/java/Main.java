package com.fenix.java;

import cn.hutool.core.io.FileUtil;

import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) {


        new Thread(() -> {
            for (int i = 0; i < 500000; i++) {
                FileUtil.writeString("1", "D:\\testfile\\copy\\2019\\ztest" + i + ".txt", StandardCharsets.UTF_8);
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 500000; i++) {
                FileUtil.writeString("1", "D:\\testfile\\copy\\2019\\03\\ztest" + i + ".txt", StandardCharsets.UTF_8);
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 500000; i++) {
                FileUtil.writeString("1", "D:\\testfile\\copy\\2019\\02\\ztest" + i + ".txt", StandardCharsets.UTF_8);
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 500000; i++) {
                FileUtil.writeString("1", "D:\\testfile\\copy\\2019\\01\\ztest" + i + ".txt", StandardCharsets.UTF_8);
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 500000; i++) {
                FileUtil.writeString("1", "D:\\testfile\\copy\\2020\\01\\ztest" + i + ".txt", StandardCharsets.UTF_8);
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 500000; i++) {
                FileUtil.writeString("1", "D:\\testfile\\copy\\2020\\02\\ztest" + i + ".txt", StandardCharsets.UTF_8);
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 500000; i++) {
                FileUtil.writeString("1", "D:\\testfile\\copy\\2020\\03\\ztest" + i + ".txt", StandardCharsets.UTF_8);
            }
        }).start();

        for (int i = 0; i < 500000; i++) {
            FileUtil.writeString("1", "D:\\testfile\\copy\\2020\\ztest" + i + ".txt", StandardCharsets.UTF_8);
        }

        System.out.println("hello world");
    }

}
