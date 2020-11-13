
package com.payment.tcp.client.handlers;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import com.payment.netty.PostPackagerRND;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class FinancialTransactionHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;
		ByteBuf buff = unpack(byteBuf);
		if (buff != null) {
			ctx.writeAndFlush(buff);
		}
	}

	private ByteBuf unpack(ByteBuf byteBuf) throws ISOException {
		GenericPackager packager = new GenericPackager("jar:postpack.xml");
		ISOMsg reqIsoMsg = new ISOMsg();
		reqIsoMsg.setPackager(packager);

		int len = byteBuf.readableBytes();
		byte[] messageBytes = new byte[len];
		byteBuf.readBytes(messageBytes);
		reqIsoMsg.unpack(messageBytes);
		reqIsoMsg.dump(System.out, "incoming");

		if (reqIsoMsg.getMTI().equals("0100")) {
			ByteBuf isoByteBuf = Unpooled.buffer();
			byte[] dst = null;
			try {
				dst = PostPackagerRND.test0100Res(reqIsoMsg).pack();
			} catch (Exception e) {
				e.printStackTrace();
			}
			isoByteBuf.writeBytes(dst);
			ByteBuf resByteBuf = Unpooled.buffer();
			resByteBuf.writeByte(dst.length >> 8);
			resByteBuf.writeByte(dst.length);
			resByteBuf.writeBytes(isoByteBuf);
			return resByteBuf;
		}
		return null;
	}
}
