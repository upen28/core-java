
package com.payment.netty.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class ResponseHandler extends ChannelDuplexHandler {

    ChannelHandlerContext ctx;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        super.write(ctx, msg, promise);
        promise.addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("write successfully by response 1");
            } else {
                System.out.println("write un-successfully by response 1" + future.cause().getLocalizedMessage());
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exception 	caught " + this.getClass().getName() + "  " + cause.getMessage());
        super.exceptionCaught(ctx, cause);

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        super.handlerAdded(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byteBuf.release();
        System.out.println("Recieved3 ");

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof String) {
            System.out.println("*******" + evt);
            ByteBuf buf = ctx.alloc().directBuffer(4);
            buf.writeInt(100);
            ctx.pipeline().writeAndFlush(buf);
        }
        super.userEventTriggered(ctx, evt);
    }

}
