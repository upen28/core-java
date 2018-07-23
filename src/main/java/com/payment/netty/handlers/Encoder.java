

package com.payment.netty.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class Encoder extends MessageToByteEncoder<ByteBuf> {

	@Override
	public void encode(ChannelHandlerContext paramChannelHandlerContext, ByteBuf msg, ByteBuf out) throws Exception {
		System.out.println("Encoder===============");
		out.writeBytes(msg);
	}

}
