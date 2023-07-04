package com.fenix.java.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public class NettyDiscardHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
        try {
            log.info("收到消息，丢弃如下：");
            while (buffer.isReadable()) {
                System.out.print((char)buffer.readByte() + "");
            }
            System.out.println();
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
