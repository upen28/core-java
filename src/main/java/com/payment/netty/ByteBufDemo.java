

package com.payment.netty;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;


public class ByteBufDemo {
	public static void testByteBuf() {
		String name = "upendra verma";
		byte[] bts = name.getBytes();
		ByteBuf src = Unpooled.buffer(bts.length);
		System.out.println(src);
		src.writeBytes(bts);
		System.out.println(src);
		System.out.println(src.toString(Charset.defaultCharset()));
	}

	public static void testEmptyByteBuf() {
		String name = "upendra verma";
		byte[] bts = name.getBytes();
		ByteBuf src = Unpooled.buffer();
		src.writeBytes(bts);
		src.skipBytes(2);
		System.out.println(src.toString(Charset.defaultCharset()));
	}

	public static void testByteProcessor() {
		byte[] number = new byte[] { 1, 2, 3, 4, 5, 6, 0, 4 };
		ByteBuf buf = Unpooled.buffer(number.length);
		buf.writeBytes(number);

		class DCProcessor implements ByteProcessor {
			@Override
			public boolean process(byte value) {
				return value != 4;
			}
		}
		while (buf.isReadable()) {
			int to = buf.readerIndex();
			int from = buf.readableBytes();
			int index = buf.forEachByte(to, from, new DCProcessor());
			if (index == -1) {
				break;
			}
			buf.readerIndex(index + 1);
			System.out.println(index);
		}
	}

	public static void main(String... args) {
		testEmptyByteBuf();
	}
}
