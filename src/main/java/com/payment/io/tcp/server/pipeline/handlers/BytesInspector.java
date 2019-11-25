package com.payment.io.tcp.server.pipeline.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class BytesInspector extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(BytesInspector.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (ByteBuf.class.isAssignableFrom(msg.getClass())) {
                publishBytesRead((ByteBuf) msg);
            } else if (ByteBufHolder.class.isAssignableFrom(msg.getClass())) {
                ByteBufHolder holder = (ByteBufHolder) msg;
                publishBytesRead(holder.content());
            }
        } catch (Exception e) {
            logger.warn("Failed to publish bytes read metrics event. This does *not* stop the pipeline processing.", e);
        } finally {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            if (ByteBuf.class.isAssignableFrom(msg.getClass())) {
                publishBytesWritten(((ByteBuf) msg).readableBytes(), promise);
            } else if (ByteBufHolder.class.isAssignableFrom(msg.getClass())) {
                publishBytesWritten(((ByteBufHolder) msg).content().readableBytes(), promise);
            }
        } catch (Exception e) {
            logger.warn("Failed to publish bytes write metrics event. This does *not* stop the pipeline processing.",
                    e);
        } finally {
            super.write(ctx, msg, promise);
        }
    }

    protected void publishBytesWritten(final long bytesToWrite, ChannelPromise promise) {
        if (bytesToWrite <= 0) {
            return;
        }

    }

    protected void publishBytesRead(ByteBuf byteBuf) {
        if (null != byteBuf) {
        }
    }
}