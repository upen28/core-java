

package com.payment.netty.handlers;

import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Stream;
import org.jpos.iso.ISOUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;


public class Decoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext paramChannelHandlerContext, ByteBuf byteBuf, List<Object> paramList) throws Exception {
		System.out.println(this.getClass().getName());

		if (byteBuf.readableBytes() < 14) {
			return;
		}
		if (byteBuf.readableBytes() == 14) {
			ByteBuf frame = Unpooled.directBuffer(byteBuf.readableBytes());
			byteBuf.readBytes(frame);
			paramList.add(frame);
		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("exception 	caught " + this.getClass().getName() + "  " + cause.getMessage());
		super.exceptionCaught(ctx, cause);

	}

	public static void main(String... args) throws InterruptedException {
		ByteBuf byteBuf = Unpooled.buffer(100);

		System.out.println(byteBuf.hasArray());

		Stream.iterate(0, f -> f + 1).limit(100).forEach(no -> {
			byteBuf.writeByte((byte) no.intValue());
		});

		ByteBuf readHalf = byteBuf.readBytes(50);

		System.out.println(byteBuf.toString(Charset.defaultCharset()));

		byte[] dst = new byte[100];

		System.out.println(ISOUtil.hexdump(dst));

		System.out.println(byteBuf.refCnt());

		System.out.println(readHalf.refCnt());

		byteBuf.release();

		readHalf.release();

		byteBuf.writeByte(12);

		System.out.println(byteBuf.refCnt());

		System.out.println(readHalf.refCnt());

		Thread.sleep(60000);

	}

}
