package com.fenix.java.nio;

import java.nio.IntBuffer;

/**
 * @author LiuFeng
 * @desc 描述
 * @date 2023/6/30 15:33
 * @since v1
 */
public class BufferUse {

    public static IntBuffer intBuffer = null;

    public static void main(String[] args) {
        intBuffer = IntBuffer.allocate(20);

        System.out.println(intBuffer.capacity());
        System.out.println(intBuffer.position());
        System.out.println(intBuffer.limit());
        System.out.println(intBuffer.mark());


        System.out.println("********写入数据********");
        for (int i = 0; i < 10; i++) {
            intBuffer.put(i);
        }

        System.out.println(intBuffer.capacity());
        System.out.println(intBuffer.position());
        System.out.println(intBuffer.limit());
        System.out.println(intBuffer.mark());

        System.out.println("********after flip（）********");

        intBuffer.flip();
        System.out.println(intBuffer.capacity());
        System.out.println(intBuffer.position());
        System.out.println(intBuffer.limit());
        System.out.println(intBuffer.mark());

        for (int i = 0; i < 3; i++) {
            int j = intBuffer.get();
            System.out.println(j);
        }

        System.out.println("******** get 3 ********");
        System.out.println(intBuffer.capacity());
        System.out.println(intBuffer.position());
        System.out.println(intBuffer.limit());
        System.out.println(intBuffer.mark());

        intBuffer.rewind();

        System.out.println("******** after rewind ********");
        System.out.println(intBuffer.capacity());
        System.out.println(intBuffer.position());
        System.out.println(intBuffer.limit());
        System.out.println(intBuffer.mark());

    }

}
