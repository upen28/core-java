
package com.payment.io.tcp.server.pipeline.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.reactivex.rxjava3.processors.PublishProcessor;

public class TransactionInHandler extends ChannelInboundHandlerAdapter {

	private PublishProcessor<ByteBuf> inTransactionHandler;
	private PublishProcessor<ChannelHandlerContext> channelHandler;

	public TransactionInHandler(PublishProcessor<ByteBuf> inTransactionHandler,
			PublishProcessor<ChannelHandlerContext> channelHandler) {
		this.inTransactionHandler = inTransactionHandler;
		this.channelHandler = channelHandler;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		channelHandler.onNext(ctx);
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		channelHandler.onComplete();
		super.channelInactive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;
		inTransactionHandler.onNext(byteBuf);
	}

}
