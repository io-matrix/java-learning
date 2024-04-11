package jav.fenix.statistic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicLong;

public class MultiThreadDemo {

    public static long getDirSize(String dir) {

        File folder = new File(dir);
        if (!folder.isDirectory()) {
            return folder.length();
        }

        return IoOperateHolder.forkjoinPool.invoke(new CalDirCommand(folder));

    }

    static class CalDirCommand extends RecursiveTask<Long> {
        private File folder;

        CalDirCommand(File folder) {
            this.folder = folder;
        }

        @Override
        protected Long compute() {
            AtomicLong size = new AtomicLong(0);
            File[] files = folder.listFiles();

            if (null == files || files.length == 0) {
                return 0L;
            }

            List<ForkJoinTask<Long>> jobs = new ArrayList<>();

            for (File f : files) {
                if (!f.isDirectory()) {
                    size.getAndAdd(f.length());
                } else {
                    jobs.add(new CalDirCommand(f));
                }
            }

            for (ForkJoinTask<Long> t : invokeAll(jobs)) {
                size.getAndAdd(t.join());
            }

            return size.get();
        }
    }

    private static final class IoOperateHolder {
        final static ForkJoinPool forkjoinPool = new ForkJoinPool();
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        long result = getDirSize("C:\\Users\\Fenix");
        System.out.println("大小： " + result + ", 用时：" + (System.currentTimeMillis() - start));
    }

}