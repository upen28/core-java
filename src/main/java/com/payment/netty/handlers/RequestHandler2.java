

package com.payment.netty.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class RequestHandler2 extends ChannelInboundHandlerAdapter {

	private ChannelHandlerContext ctx;

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
		super.handlerAdded(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;
		System.out.println("Recieved2 ");
		super.channelRead(ctx, msg);

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("exception 	caught " + this.getClass().getName() + "  " + cause.getMessage());
		// super.exceptionCaught(ctx, cause);
	}

}
