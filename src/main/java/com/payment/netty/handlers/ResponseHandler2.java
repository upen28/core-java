
package com.payment.netty.handlers;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class ResponseHandler2 extends ChannelDuplexHandler {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        super.write(ctx, msg, promise);

        promise.addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("write successfully by response 2");
            } else {
                System.out.println("write un-successfully by response 2" + future.cause().getLocalizedMessage());
            }
        });
    }

}
