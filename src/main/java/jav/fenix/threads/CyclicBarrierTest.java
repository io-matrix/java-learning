package jav.fenix.threads;

import java.io.IOException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(4, () -> {
            System.out.println("栅栏已达到开放条件");
        });

        ExecutorService executor = Executors.newFixedThreadPool(4);
        executor.submit(new Thread(new Runner(barrier, "1号选手")));
        executor.submit(new Thread(new Runner(barrier, "2号选手")));
        executor.submit(new Thread(new Runner(barrier, "3号选手")));
        executor.submit(new Thread(new Runner(barrier, "4号选手")));
        executor.submit(new Thread(new Runner(barrier, "5号选手")));
        executor.submit(new Thread(new Runner(barrier, "6号选手")));
        executor.submit(new Thread(new Runner(barrier, "7号选手")));
        System.out.println("主线程完毕");
        executor.shutdown();
    }
}

class Runner implements Runnable {
    private CyclicBarrier barrier;
    private String name;

    public Runner(CyclicBarrier barrier, String name) {
        super();
        this.barrier = barrier;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            System.out.println(name + " 准备好了...");
            barrier.await(4, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(name + " 起跑！");
    }
}