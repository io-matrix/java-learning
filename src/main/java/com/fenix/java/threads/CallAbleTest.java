package com.fenix.java.threads;

import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
public class CallAbleTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        String s1 = SecureUtil.sha1("/test/test/1");
        String s2 = SecureUtil.sha1("/test/test/2");
        String s3 = SecureUtil.sha1("/test/test/3");

        log.info("s1:{}", s1);
        log.info("s2:{}", s2);
        log.info("s3:{}", s3);


//        FutureTask<String> futureTask = new FutureTask<>(() -> "hello");
//
//        ExecutorService executorService = Executors.newFixedThreadPool(2);
//
//        Future<String> submit = executorService.submit(() -> "");
//
//        String s1 = submit.get();
//        executorService.execute(futureTask);
////        futureTask.run();
//        String s = futureTask.get();
//        System.out.println(s);

    }

}
