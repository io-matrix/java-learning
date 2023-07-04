package com.fenix.java.nio;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LiuFeng
 * @desc 描述
 * @date 2023/7/4 09:36
 * @since v1
 */
@Slf4j
public class MultiThreadEchoServerReactor {
    ServerSocketChannel serverSocket;
    AtomicInteger next = new AtomicInteger(0);

    Selector bossSelector = null;

    Reactor bossReactor = null;
    Selector[] workSelectors = new Selector[2];

    Reactor[] workReactors = null;

    MultiThreadEchoServerReactor() throws IOException {
        bossSelector = Selector.open();
        workSelectors[0] = Selector.open();
        workSelectors[1] = Selector.open();

        serverSocket = ServerSocketChannel.open();

        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 17070);

        serverSocket.socket().bind(address);
        serverSocket.configureBlocking(false);

        SelectionKey sk = serverSocket.register(bossSelector, SelectionKey.OP_ACCEPT);

        sk.attach(new AcceptorHandler());

        bossReactor = new Reactor(bossSelector);

        Reactor workReactor1 = new Reactor(workSelectors[0]);
        Reactor workReactor2 = new Reactor(workSelectors[1]);

        workReactors = new Reactor[]{workReactor1, workReactor2};

    }

    private void startService() {
        new Thread(bossReactor).start();
        new Thread(workReactors[0]).start();
        new Thread(workReactors[1]).start();

    }


    public static void main(String[] args) throws IOException {
        MultiThreadEchoServerReactor reactor = new MultiThreadEchoServerReactor();
        reactor.startService();
    }

    class Reactor implements Runnable {

        final Selector selector;

        public Reactor(Selector selector) {
            this.selector = selector;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    selector.select(1000);
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    if (CollectionUtil.isEmpty(selectedKeys)) {
                        continue;
                    }
                    Iterator<SelectionKey> iterator = selectedKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey sk = iterator.next();
                        dispatch(sk);
                    }
                    selectedKeys.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void dispatch(SelectionKey sk) {
            Runnable handler = (Runnable) sk.attachment();
            if (handler != null) {
                handler.run();
            }
        }
    }

    class AcceptorHandler implements Runnable {

        @Override
        public void run() {
            try {
                SocketChannel channel = serverSocket.accept();
                log.info("接收到一个新的链接");
                if (channel != null) {
                    int index = next.get();
                    log.info("选择器的编号：{}", index);
                    Selector workSelector = workSelectors[index];
                    new MultiThreadEchoHandler(workSelector, channel);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (next.incrementAndGet() == workSelectors.length) {
                next.set(0);
            }
        }
    }

}
