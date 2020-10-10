
package com.payment.netty;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.stream.Stream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.header.BASE1Header;
import org.jpos.iso.packager.GenericPackager;

import com.payment.tcp.client.handlers.VtsDecoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.FixedChannelPool.AcquireTimeoutAction;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class VtsClient {

	private static final byte[] HEADER2003_BYTES = ISOUtil.hex2byte("160102003E0000000000000000000000000000000000");

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
					ch.pipeline().addLast(new VtsDecoder());
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

	public void processTransaction() throws Exception {
		Channel channel = connect("172.20.215.97", 9999);
		ByteBuf buf = Unpooled.buffer();
		buf.writeBytes(toVtsReq());
		channel.writeAndFlush(buf);
	}

	@SuppressWarnings("unchecked")
	public void processTransactions() {
		EventLoopGroup workerGroup = new NioEventLoopGroup(2);
		Bootstrap bootStrap = new Bootstrap();
		bootStrap.group(workerGroup);
		bootStrap.channel(NioSocketChannel.class);
		bootStrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootStrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000);
		bootStrap.remoteAddress("172.20.215.97", 9999);
		bootStrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new VtsDecoder());
			}
		});

		ChannelPoolHandler cpH = new ChannelPoolHandler() {
			@Override
			public void channelReleased(Channel ch) throws Exception {
				System.out.println("channelReleased" + ch.toString());

			}

			@Override
			public void channelAcquired(Channel ch) throws Exception {
				System.out.println("channelAcquired" + ch.toString());
			}

			@Override
			public void channelCreated(Channel ch) throws Exception {
				System.out.println("channelCreated" + ch.toString());
				ch.pipeline().addLast(new VtsDecoder());
			}
		};

		FixedChannelPool pool = new FixedChannelPool(bootStrap, cpH, ChannelHealthChecker.ACTIVE,
				AcquireTimeoutAction.FAIL, 2000, 1, 5);
		Stream.iterate(0, x -> x + 1).limit(6).forEach(dara -> {
			Future<Channel> fChannel = null;
			try {
				fChannel = pool.acquire();
				fChannel.addListener(new GenericFutureListener() {
					@Override
					public void operationComplete(Future future) throws Exception {
						if (future.isSuccess()) {
							ByteBuf buf = toVtsReq();
							System.out.println("writing=======");
							// pool.release((Channel) future.get());
							((Channel) future.get()).writeAndFlush(buf);
						} else {
							System.out.println(future.cause());
						}
					}

				});

			} catch (Exception e) {
				e.printStackTrace();
			} finally {

			}
		});

	}

	private ByteBuf toVtsReq() throws Exception {
		ISOMsg message = new ISOMsg();
		GenericPackager packager = new GenericPackager("jar:sms-packager.xml");
		message.setPackager(packager);

		message.setMTI("0200");
		message.set(2, "5573890087012011");
		message.set(3, "300000");
		message.set(4, "000000000000");
		message.set(7, "0701234624");
		message.set(11, "000005");
		message.set(12, "010100");
		message.set(13, "1120");

		// message.set(14, "3007");
		message.set(18, "6011");
		// message.set(19, "840");
		message.set(22, "0001");
		message.set(25, "00");
		message.set(32, "0100");
		message.set(35, "5573890087012011D30072011000000000684");
		message.set(37, "918223000005");

		message.set(41, "RRL74151");
		message.set(42, "0000PDBB7WSHUH5");
		message.set(43, "ACQUIRER^NAME^^^^^^^^^^^^CITY^NAME^^^^US");

		message.set(52, "0CC6A0044B53CA3A");
		message.set(53, "2001010100000000");
		ISOMsg inner = new ISOMsg(63);
		inner.set(1, "0002");
		message.set(inner);

		message.dump(System.out, "");

		byte[] b;
		try {
			message.setHeader(new byte[] {});
			b = message.pack();
		} catch (Exception e) {
			throw new RuntimeException("prepareNativeMessage unable to pack message-", e);
		}

		ByteBuf buf = null;
		byte[] messageLengthBytes = getMessageVTSLengthBytes(b.length + getHeaderLength());

		buf = Unpooled.buffer(messageLengthBytes.length + b.length + getHeaderLength());
		buf.writeBytes(messageLengthBytes);

		BASE1Header h = new BASE1Header("000000", "000000", 2);
		h.setLen(b.length);

		buf.writeBytes(h.pack());
		h.swapDirection();
		buf.writeBytes(b);

		return buf;
	}

	private byte[] getMessageVTSLengthBytes(int len) {
		byte[] by = new byte[4];
		by[0] = (byte) (len >> 8);
		by[1] = (byte) (len);
		by[2] = (byte) (0);
		by[3] = (byte) (0);
		return by;
	}

	protected int getHeaderLength() {
		return HEADER2003_BYTES.length;
	}

	public static void main(String[] args) throws Exception {
		VtsClient client = new VtsClient();
		client.processTransactions();
		Thread.sleep(5000);
	}

}
