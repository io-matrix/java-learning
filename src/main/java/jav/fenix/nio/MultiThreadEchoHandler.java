package jav.fenix.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author LiuFeng
 * @desc 描述
 * @date 2023/7/4 14:05
 * @since v1
 */
@Slf4j
public class MultiThreadEchoHandler implements Runnable {

    final SocketChannel channel;
    final SelectionKey sk;
    final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    static final int RECEIVING = 0, SENDING = 1;

    int state = RECEIVING;

    static ExecutorService pool = Executors.newFixedThreadPool(4);

    public MultiThreadEchoHandler(Selector selector, SocketChannel socketChannel) throws IOException {
        this.channel = socketChannel;
        channel.configureBlocking(false);
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        sk = channel.register(selector, 0);
        sk.attach(this);
        sk.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
        log.info("新的连接 注册完成");
    }

    @Override
    public void run() {
        pool.execute(new AsyncTask());

    }

    //异步任务，不在 Reactor 线程中执行
//数据传输与业务处理任务，不在 IO 事件轮询线程中执行，在独立的线程池中执行
    public synchronized void asyncRun() {
        try {
            if (state == SENDING) {
//写入通道
                channel.write(byteBuffer);
//写完后,准备开始从通道读,byteBuffer 切换成写模式
                byteBuffer.clear();
//写完后,注册 read 就绪事件
                sk.interestOps(SelectionKey.OP_READ); //写完后,进入接收的状态
                state = RECEIVING;
            } else if (state == RECEIVING) {
//从通道读
                int length = 0;
                while ((length = channel.read(byteBuffer)) > 0) {
                    log.info(new String(byteBuffer.array(), 0, length));
                }
//读完后，准备开始写入通道,byteBuffer 切换成读模式
                byteBuffer.flip();
//读完后，注册 write 就绪事件
                sk.interestOps(SelectionKey.OP_WRITE); //读完后,进入发送的状态
                state = SENDING;
            }
//处理结束了, 这里不能关闭 select key，需要重复使用
//            sk.cancel();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    class AsyncTask implements Runnable {
        public void run() {
            MultiThreadEchoHandler.this.asyncRun();
        }
    }
}
