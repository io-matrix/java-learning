package jav.fenix.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class WriteReadTest {

    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(9, 100);
        print(buffer);
    }

    public static void print(ByteBuf buffer) {
        System.out.println("1.0 isReadable():" + buffer.isReadable());
        System.out.println("1.1 readerIndex():" + buffer.readerIndex());
        System.out.println("1.2 capacity()" + buffer.capacity());
    }

}