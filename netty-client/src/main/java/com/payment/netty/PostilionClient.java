
package com.payment.netty;

import org.jpos.iso.ISOUtil;

import com.payment.tcp.client.handlers.InReqHandlerForIsoMsg;
import com.payment.tcp.client.handlers.LengthBasedDecoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;

public class PostilionClient {

	private Channel connect(String ip, int port) {
		EventLoopGroup workerGroup = new NioEventLoopGroup(2);
		try {
			Bootstrap bootStrap = new Bootstrap();
			bootStrap.group(workerGroup);
			bootStrap.channel(NioSocketChannel.class);
			bootStrap.option(ChannelOption.SO_KEEPALIVE, true);
			bootStrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000);
			bootStrap.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new LengthBasedDecoder());
					ch.pipeline().addLast(new InReqHandlerForIsoMsg());
				}
			});
			ChannelFuture connectFuture = bootStrap.connect(ip, port);
			connectFuture.awaitUninterruptibly();
			if (connectFuture.isSuccess()) {
				System.out.print("connection successed ");
			} else {
				throw new RuntimeException("connection fail ");
			}
			SslHandler sslHandler = (SslHandler) connectFuture.channel().pipeline().get(SslHandler.class);
			if (sslHandler != null) {
				Future<?> sslFuture = sslHandler.handshakeFuture();
				sslFuture.awaitUninterruptibly();
				if (sslFuture.isSuccess()) {
					System.out.print("handskaing successed ");
				} else {
					throw new RuntimeException("handskaing failed ");
				}
			} else {
				System.out.println("ssl is not configured");
			}
			return connectFuture.channel();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void processTransaction() throws Exception {
		Channel channel = connect("127.0.0.1", 9999);
		ByteBuf buf = Unpooled.buffer();
		buf.writeBytes(to_Request());
		channel.writeAndFlush(buf);
	}

	private ByteBuf to_Request() throws Exception {
		byte[] dst = PostPackagerRND.test0100Req().pack();
		ByteBuf isoByteBuf = Unpooled.buffer();
		isoByteBuf.writeBytes(dst);
		int len = isoByteBuf.readableBytes();

		ByteBuf reqByteBuf = Unpooled.buffer();
		reqByteBuf.writeByte(len >> 8);
		reqByteBuf.writeByte(len);
		reqByteBuf.writeBytes(isoByteBuf);
		return reqByteBuf;
	}

	public static void main(String[] args) throws Exception {
		PostilionClient client = new PostilionClient();
		client.processTransaction();
		Thread.sleep(5000);
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
