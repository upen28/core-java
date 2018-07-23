

package com.payment.netty.client;

import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import com.payment.netty.handlers.Decoder;
import com.payment.netty.handlers.RequestHandler;
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


public class Client {

	public void perform() {
		Channel channel = connect(8080);
		ByteBuf buf = Unpooled.buffer();
		buf.writeBytes("upendra\r\n".getBytes());
		channel.writeAndFlush(buf);

	}

	public static void main(String[] args) throws Exception {
		Client client = new Client();
		client.perform();
		Thread.sleep(5000);
	}

	private Channel connect(int port) {
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
					// ch.pipeline().addLast(getSslHandler());
					ch.pipeline().addLast(new Decoder());
					ch.pipeline().addLast(new RequestHandler());
					/*
					 * ch.pipeline().addLast(new RequestHandler2());
					 * ch.pipeline().addLast(new Encoder());
					 * ch.pipeline().addLast(new ResponseHandler2());
					 * ch.pipeline().addLast(new ResponseHandler());
					 */
				}
			});

			ChannelFuture connectFuture = bootStrap.connect("localhost", port);
			connectFuture.awaitUninterruptibly();

			if (connectFuture.isSuccess()) {
				System.out.print("connection successed ");
			}
			else {
				throw new RuntimeException("connection fail ");
			}
			/*
			 * SslHandler sslHandler = (SslHandler)
			 * connectFuture.channel().pipeline().get(SslHandler.class);
			 * Future<?> sslFuture = sslHandler.handshakeFuture();
			 * sslFuture.awaitUninterruptibly(); if (sslFuture.isSuccess()) {
			 * System.out.print("handskaing successed "); } else { throw new
			 * RuntimeException("handskaing failed "); }
			 */
			return connectFuture.channel();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private SslHandler getSslHandler() throws Exception {
		char[] passwd = "password".toCharArray();

		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream("C:/upendra/keystore.jks"), passwd);

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ks);

		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(null, tmf.getTrustManagers(), null);

		SSLEngine engine = ctx.createSSLEngine();
		engine.setEnabledProtocols(new String[] { "TLSv1.1" });
		engine.setUseClientMode(true);
		engine.setEnabledProtocols(engine.getSupportedProtocols());
		engine.setEnabledCipherSuites(engine.getSupportedCipherSuites());
		engine.setEnableSessionCreation(true);
		SslHandler sslHandler = new SslHandler(engine);
		return sslHandler;
	}

}
