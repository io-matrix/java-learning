package jav.fenix.statistic;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

public class SingleThreadDemo {

    static AtomicLong size = new AtomicLong(0);
    static AtomicLong count = new AtomicLong(0);

    public static void getDirSize(String dir) {

        File folder = new File(dir);
        if (!folder.isDirectory()) {
            return;
        }

        File[] files = folder.listFiles();
        if (null == files || files.length == 0) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                getDirSize(f.getPath());
            } else {
                size.getAndAdd(f.length());
                count.getAndAdd(1);
            }
        }

    }

    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        getDirSize("C:\\Users\\Fenix");
        System.out.println("大小： " + size + ", 数量：" + count + ", 用时：" + (System.currentTimeMillis() - start));

    }

}