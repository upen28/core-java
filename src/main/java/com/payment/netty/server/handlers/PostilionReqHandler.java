
package com.payment.netty.server.handlers;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import com.payment.jpos.PostPackagerRND;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class PostilionReqHandler extends ChannelInboundHandlerAdapter {

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

		reqMsg.dump(System.out, "");

		byte[] dst = PostPackagerRND.test0100Res().pack();
		ByteBuf resIsoMsg = Unpooled.buffer();
		resIsoMsg.writeBytes(dst);
		int msgLen = resIsoMsg.readableBytes();

		ByteBuf resByteBuf = Unpooled.buffer();
		resByteBuf.writeByte(msgLen >> 8);
		resByteBuf.writeByte(msgLen);
		resByteBuf.writeBytes(resIsoMsg);

		ChannelFuture chFuture = ctx.writeAndFlush(resByteBuf);
		chFuture.addListener(future -> {
			if (future.isSuccess()) {
				System.out.println("write successfully response by PostilionReqHandler");
			} else {
				System.out.println("write un-successfully response by PostilionReqHandler "
						+ future.cause().getLocalizedMessage());
			}
		});

	}

}
