
package com.payment.netty.testing.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class ResponseHandler2 extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        promise.addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("write successfully by response 2");
            } else {
                System.out.println("write un-successfully by response 2" + future.cause().getLocalizedMessage());
            }
        });
        super.write(ctx, msg, promise);
    }

}
