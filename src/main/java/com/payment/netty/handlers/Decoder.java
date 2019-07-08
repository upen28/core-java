
package com.payment.netty.handlers;

import java.util.List;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class Decoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext paramChannelHandlerContext, ByteBuf byteBuf, List<Object> paramList)
			throws Exception {
		int tLen = byteBuf.readableBytes();
		byteBuf.skipBytes(26);
		System.out.println(hexDump(byteBuf));
		GenericPackager packager = new GenericPackager("jar:sms-packager.xml");

		ISOMsg isoMessage = new ISOMsg();
		isoMessage.setDirection(1);
		isoMessage.setPackager(packager);

		byte[] messageBytes = new byte[tLen - 26];
		byteBuf.readBytes(messageBytes);
		System.out.println(hexDump(messageBytes));

		isoMessage.unpack(messageBytes);
		isoMessage.dump(System.out, "");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("exception 	caught " + this.getClass().getName() + "  " + cause.getMessage());
		super.exceptionCaught(ctx, cause);

	}

	public static String hexDump(ByteBuf buffer) {
		byte[] dst = new byte[buffer.readableBytes()];
		buffer.getBytes(0, dst);
		return ISOUtil.hexdump(dst);
	}

	public static String hexDump(byte[] buffer) {
		return ISOUtil.hexdump(buffer);
	}

}
