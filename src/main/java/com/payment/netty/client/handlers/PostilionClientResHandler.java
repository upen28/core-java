
package com.payment.netty.client.handlers;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class PostilionClientResHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg1) throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg1;
		// Logger logger = new Logger();
		// logger.addListener(new SimpleLogListener(System.out));
		int len = byteBuf.readableBytes();
		GenericPackager packager = new GenericPackager("jar:postpack.xml");
		// ((LogSource) packager).setLogger(logger, "debug");

		ISOMsg reqMsg = new ISOMsg();
		reqMsg.setDirection(1);
		reqMsg.setPackager(packager);

		byte[] messageBytes = new byte[len];
		byteBuf.readBytes(messageBytes);
		reqMsg.unpack(messageBytes);
		//reqMsg.dump(System.out, "");
	}

}
