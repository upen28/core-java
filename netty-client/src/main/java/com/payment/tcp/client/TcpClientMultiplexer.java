package com.payment.tcp.client;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.payment.tcp.client.eventgroup.EventLoopGroupProvider;
import com.payment.tcp.client.handlers.FinancialTransactionHandler;
import com.payment.tcp.client.handlers.LengthBasedDecoder;
import com.payment.tcp.client.properties.TcpClientProperties;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;

@Component
public class TcpClientMultiplexer {
	private ChannelPool channelPool;
	@Autowired
	private TcpClientProperties properties;

	@PostConstruct
	public void init() {
		configuredConnnectionPool();
	}

	public ByteBuf processTransaction(ByteBuf buf, long timeout) throws Exception {
		Future<Channel> promise = channelPool.acquire().await();
		if (promise.isSuccess()) {
			System.out.println("acquiring channel " + Thread.currentThread().getName());
			Channel channel = promise.getNow();
			return send(channel, buf, timeout);
		} else {
			throw new Exception("channelpool is busy please try some time later", promise.cause());
		}
	}

	private ByteBuf send(Channel channel, ByteBuf buf, long timeout) throws Exception {
		ByteBuf resByteBuf = null;
		try {
			try {
				channel.writeAndFlush(buf).await();
			} catch (Exception ex) {
				System.out.println("Exception in writing");
				throw new Exception("exception in writing to channel", ex);
			}
		} finally {
			channelPool.release(channel).await();
			System.out.println("release channel " + Thread.currentThread().getName());
		}
		return resByteBuf;
	}

	private void configuredConnnectionPool() {
		Bootstrap bootStrap = configureBootstrap();
		ClientChannelPoolHandler poolHandler = new ClientChannelPoolHandler();
		channelPool = new FixedChannelPool(bootStrap, poolHandler, properties.getConnectionPool().getHealthCheck(),
				properties.getConnectionPool().getAction(), properties.getConnectionPool().getAcquireTimeoutMillis(),
				properties.getConnectionPool().getMaxConnections(),
				properties.getConnectionPool().getMaxPendingAcquires(),
				properties.getConnectionPool().isReleaseHealthCheck(),
				properties.getConnectionPool().isLastRecentUsed());
	}

	private Bootstrap configureBootstrap() {
		Bootstrap bootStrap = new Bootstrap();

		EventLoopGroupProvider eventGroupProvider = new EventLoopGroupProvider();
		EventLoopGroup workerGroup = eventGroupProvider.globalServerEventLoop(true);
		bootStrap.group(workerGroup);
		bootStrap.channel(NioSocketChannel.class);

		bootStrap.remoteAddress(properties.getIp(), properties.getPort());
		bootStrap.option(ChannelOption.SO_KEEPALIVE, properties.isKeepAlive());
		bootStrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeoutMillis());
		return bootStrap;
	}

	public static class ClientChannelPoolHandler implements ChannelPoolHandler {
		@Override
		public void channelReleased(Channel ch) throws Exception {
			System.out.println("channel disconnected " + ch.id());
		}

		@Override
		public void channelAcquired(Channel ch) throws Exception {

		}

		@Override
		public void channelCreated(Channel ch) throws Exception {
			System.out.println("channel connected " + ch.id());
			ch.pipeline().addLast(new LengthBasedDecoder());
			ch.pipeline().addLast(new FinancialTransactionHandler());
		}
	}
}
