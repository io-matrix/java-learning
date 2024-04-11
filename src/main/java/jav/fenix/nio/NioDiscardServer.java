package jav.fenix.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author LiuFeng
 * @desc 描述
 * @date 2023/7/3 15:37
 * @since v1
 */
@Slf4j
public class NioDiscardServer {

    public static void startServer() throws IOException {

        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(new InetSocketAddress(17070));
        log.info("服务启动成功！！！！");

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (0 < selector.select()) {
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();

                if (selectionKey.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {

                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int length = 0;
                    while ((length = socketChannel.read(buffer)) > 0) {
                        buffer.flip();
                        log.info("接收内容：{}", new String(buffer.array(), 0, length));
                        buffer.clear();
                    }
                    socketChannel.close();

                }
                iterator.remove();
            }
        }
        serverSocketChannel.close();
    }

    public static void main(String[] args) throws IOException {
        startServer();
    }

}
