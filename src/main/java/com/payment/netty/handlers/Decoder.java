
package com.payment.netty.handlers;

import java.util.List;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.util.LogSource;
import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class Decoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext paramChannelHandlerContext, ByteBuf byteBuf, List<Object> paramList)
			throws Exception {

		Logger logger = new Logger();
		logger.addListener(new SimpleLogListener(System.out));

		int tLen = byteBuf.readableBytes();
		byteBuf.skipBytes(26);
		GenericPackager packager = new GenericPackager("jar:sms-packager.xml");
		((LogSource) packager).setLogger(logger, "debug");

		ISOMsg isoMessage = new ISOMsg();
		isoMessage.setDirection(1);
		isoMessage.setPackager(packager);

		byte[] messageBytes = new byte[tLen - 26];
		byteBuf.readBytes(messageBytes);
		isoMessage.unpack(messageBytes);
		System.out.println(hexDump(messageBytes));

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
