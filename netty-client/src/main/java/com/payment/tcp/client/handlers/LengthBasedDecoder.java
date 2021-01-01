
package com.payment.tcp.client.handlers;

import java.util.List;

import org.jpos.iso.ISOUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;

public class LengthBasedDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
		ByteBuf decodeByteBuf = (ByteBuf) decode(ctx, byteBuf);
		if (decodeByteBuf != null) {
			out.add(decodeByteBuf);
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof SslHandshakeCompletionEvent) {
			SslHandshakeCompletionEvent event = (SslHandshakeCompletionEvent) evt;
			if (event.isSuccess()) {
				System.out.println("ssl success " + ctx.pipeline().channel().localAddress());
			} else {
				System.out.println("ssl failure " + ctx.pipeline().channel().localAddress());
				event.cause().printStackTrace();
			}
			ctx.fireUserEventTriggered(evt);
		}

	}

	private Object decode(ChannelHandlerContext ctx, ByteBuf byteBuf) {
		if (byteBuf.readableBytes() < 2) {
			return null;
		}
		byteBuf.markReaderIndex();

		byte[] array = new byte[2];
		byteBuf.readBytes(array);
		int dataLength = ((int) array[0] & 0xFF) << 8 | (int) array[1] & 0xFF;

		if (byteBuf.readableBytes() < dataLength) {
			byteBuf.resetReaderIndex();
			return null;
		}
		ByteBuf frame = extractFrame(byteBuf, dataLength);
		return frame;
	}

	protected ByteBuf extractFrame(ByteBuf buffer, int length) {
		ByteBuf frame = Unpooled.buffer(length);
		frame.writeBytes(buffer, length);
		return frame;
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
