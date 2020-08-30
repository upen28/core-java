package com.payment.netty.client;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.payment.jpos.PostPackagerRND;
import com.payment.netty.client.handlers.PostilionClientResHandler;
import com.payment.netty.client.handlers.PostilionHalfDupexDecoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TCPClientMultiplexer {

	public void processTransactionAysc() {
		Bootstrap bootStrap = new Bootstrap();
		bootStrap.remoteAddress("127.0.0.1", 9999);

		EventLoopGroup workerGroup = new NioEventLoopGroup(2);
		bootStrap.group(workerGroup);
		bootStrap.channel(NioSocketChannel.class);
		bootStrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootStrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000);

		ChannelPoolHandler handler = new ChannelPoolHandler() {
			@Override
			public void channelReleased(Channel ch) throws Exception {
				//System.out.println(ch.localAddress() + " is released");
			}

			@Override
			public void channelAcquired(Channel ch) throws Exception {
				//System.out.println(ch.localAddress() + " is acquired");
			}

			@Override
			public void channelCreated(Channel ch) throws Exception {
				System.out.println("handlers is created");
				ch.pipeline().addLast(new PostilionHalfDupexDecoder());
				ch.pipeline().addLast(new PostilionClientResHandler());
			}
		};
		ChannelPool pool = new SimpleChannelPool(bootStrap, handler, ChannelHealthChecker.ACTIVE, false);

		TCPClient[] array = new TCPClient[8];
		for (int i = 0; i < array.length; i++) {
			array[i] = new TCPClient(pool, String.valueOf(i));
		}
		ExecutorService executor = Executors.newFixedThreadPool(24);

		Runnable task = () -> {
			Random ran = new Random();
			TCPClient client = array[ran.nextInt(8)];

			byte[] dst = null;
			try {
				dst = PostPackagerRND.test0100Req().pack();
			} catch (Exception e) {

			}
			ByteBuf isoByteBuf = Unpooled.buffer();
			isoByteBuf.writeBytes(dst);
			int len = isoByteBuf.readableBytes();

			ByteBuf reqByteBuf = Unpooled.buffer();
			reqByteBuf.writeByte(len >> 8);
			reqByteBuf.writeByte(len);
			reqByteBuf.writeBytes(isoByteBuf);

			client.processTransaction(reqByteBuf);
		};

		for (int i = 0; i < 50; i++) {
			executor.execute(task);
		}

	}

	public static void main(String... args) {
		TCPClientMultiplexer ml = new TCPClientMultiplexer();
		ml.processTransactionAysc();

	}

}