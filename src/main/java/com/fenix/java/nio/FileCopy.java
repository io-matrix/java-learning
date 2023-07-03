package com.fenix.java.nio;


import org.apache.commons.compress.utils.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author LiuFeng
 * @desc 描述
 * @date 2023/6/30 21:29
 * @since v1
 */
public class FileCopy {

    public static String srcPath = "/Users/feng/logs/test.log";
    public static String destPath = "/Users/feng/logs/test1.log";


    public static void main(String[] args) {
        FileInputStream ifs = null;
        FileOutputStream ofs = null;

        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {

            ifs = new FileInputStream(srcPath);
            ofs = new FileOutputStream(destPath);

            inChannel = ifs.getChannel();
            outChannel = ofs.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            int length = -1;

            while ((length = inChannel.read(buffer)) != -1) {

                buffer.flip();

                int outLength = 0;

                while ((outLength = outChannel.write(buffer)) != 0) {
                    System.out.println("写入字节： " + outLength);
                }

                buffer.clear();
            }

            outChannel.force(true);
        } catch (Exception e) {
            IOUtils.closeQuietly(ifs);
            IOUtils.closeQuietly(ofs);
            IOUtils.closeQuietly(inChannel);
            IOUtils.closeQuietly(outChannel);

        }


    }


}
