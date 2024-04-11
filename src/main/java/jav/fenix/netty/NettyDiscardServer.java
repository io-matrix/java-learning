package jav.fenix.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyDiscardServer {

    private int serverPort;
    ServerBootstrap bootstrap = new ServerBootstrap();

    public NettyDiscardServer(int serverPort) {
        this.serverPort = serverPort;
    }


    public void runServer() {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {

            bootstrap.group(boss, worker);
            bootstrap.localAddress(serverPort);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline().addLast(new NettyDiscardHandler());
                }
            });

            ChannelFuture channelFuture = bootstrap.bind().sync();
            log.info("服务启动成功，监听端口：{}", channelFuture.channel().localAddress());
            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.sync();

        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        NettyDiscardServer nettyDiscardServer = new NettyDiscardServer(17077);
        nettyDiscardServer.runServer();
    }

}
