
package com.payment.tcp.client.handlers;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InReqHandlerForIsoMsg extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;
		unpack(byteBuf);
	}

	private ISOMsg unpack(ByteBuf byteBuf) throws ISOException {
		GenericPackager packager = new GenericPackager("jar:postpack.xml");
		ISOMsg resIsoMsg = new ISOMsg();
		resIsoMsg.setPackager(packager);

		int len = byteBuf.readableBytes();
		byte[] messageBytes = new byte[len];
		byteBuf.readBytes(messageBytes);

		resIsoMsg.unpack(messageBytes);
		resIsoMsg.dump(System.out, "request");

		return resIsoMsg;
	}
}
