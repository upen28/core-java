
package com.payment.io.tcp.server.pipeline.handlers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jpos.iso.ISOUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class LengthBasedDecoder extends ByteToMessageDecoder {
	private int msgLength = 2;
	private ByteDecoder byteDecoder = new TwoByteDecoder();

	private File logFile;

	public LengthBasedDecoder(File logFile) {
		this.logFile = logFile;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		Object decoded = decode(ctx, in);
		if (decoded != null) {
			out.add(decoded);
		}
	}

	private Object decode(ChannelHandlerContext ctx, ByteBuf byteBuf) {
		if (byteBuf.readableBytes() < msgLength) {
			return null;
		}
		byteBuf.markReaderIndex();
		byte[] array = new byte[msgLength];
		byteBuf.readBytes(array);

		log("Entering decoder from remote " + ctx.channel().remoteAddress());
		int dataLength = byteDecoder.decode(array);
		log("two byte header length " + dataLength);

		if (byteBuf.readableBytes() < dataLength) {
			byteBuf.resetReaderIndex();
			return null;
		}
		ByteBuf frame = Unpooled.buffer(dataLength);
		frame.writeBytes(byteBuf, dataLength);
		return frame;
	}

	public static String hexDump(ByteBuf buffer) {
		byte[] dst = new byte[buffer.readableBytes()];
		buffer.getBytes(0, dst);
		return ISOUtil.hexdump(dst);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log("exception raised on socket " + ctx.channel().remoteAddress() + " " + cause.getLocalizedMessage());
	}

	public int getMsgLength() {
		return msgLength;
	}

	public void setMsgLength(int msgLength) {
		this.msgLength = msgLength;
		if (msgLength == 2) {
			byteDecoder = new TwoByteDecoder();
		} else if (msgLength == 4) {
			byteDecoder = new FourByteDecoder();
		}
	}

	public interface ByteDecoder {
		public int decode(byte[] arr);
	}

	public static class TwoByteDecoder implements ByteDecoder {
		@Override
		public int decode(byte[] arr) {
			return ((int) arr[0] & 0xFF) << 8 | (int) arr[1] & 0xFF;
		}
	}

	public static class FourByteDecoder implements ByteDecoder {
		@Override
		public int decode(byte[] arr) {
			return ((int) arr[0] & 0xFF) << 8 | (int) arr[1] & 0xFF;
		}
	}

	private void log(String log) {
		System.out.println(log);
		try {
			String logNewLine = log + System.lineSeparator();
			FileUtils.writeByteArrayToFile(logFile, logNewLine.getBytes(), true);
		} catch (IOException e) {
		}
	}

}
