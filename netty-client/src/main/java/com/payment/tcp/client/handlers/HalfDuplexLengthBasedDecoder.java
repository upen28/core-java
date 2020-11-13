
package com.payment.tcp.client.handlers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.jpos.iso.ISOUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class HalfDuplexLengthBasedDecoder extends ByteToMessageDecoder {

	private CompletableFuture<ByteBuf> event;

	@SuppressWarnings("unchecked")
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		super.userEventTriggered(ctx, evt);
		event = (CompletableFuture<ByteBuf>) evt;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
		ByteBuf isoByteBuf = (ByteBuf) decode(ctx, byteBuf);
		if (event != null) {
			event.complete(isoByteBuf);
			event = null;
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
		if (event != null) {
			event.completeExceptionally(cause);
		}
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
