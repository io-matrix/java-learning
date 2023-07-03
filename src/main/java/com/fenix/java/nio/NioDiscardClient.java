package com.fenix.java.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author LiuFeng
 * @desc 描述
 * @date 2023/7/3 15:37
 * @since v1
 */
@Slf4j
public class NioDiscardClient {

    public static void main(String[] args) throws IOException {

        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(17070));
        socketChannel.configureBlocking(false);
        while (!socketChannel.finishConnect()) {
        }
        log.info("连接成功！");

        String content = "hello world";
        byte[] bytes = content.getBytes();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(bytes);

        buffer.flip();
        //发送到服务器
        socketChannel.write(buffer);
        socketChannel.shutdownOutput();
        socketChannel.close();
    }

}
