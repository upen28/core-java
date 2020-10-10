package com.payment.io.tcp.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import javax.net.ssl.SSLEngine;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;

public abstract class TcpServer<R, W> {
	public abstract <T> TcpServer<R, W> channelOption(ChannelOption<T> option, T value);

	public abstract <T> TcpServer<R, W> clientChannelOption(ChannelOption<T> option, T value);

	public abstract <RR, WW> TcpServer<RR, WW> addChannelHandlerLast(String name, ChannelHandler channelHandler);

	public abstract TcpServer<R, W> secure(SSLEngine sslEngineFactory);

	public abstract int getServerPort();

	public abstract SocketAddress getServerAddress();

	public abstract TcpServer<R, W> start();

	public abstract void shutdown();

	public abstract void awaitShutdown();

	public static TcpServer<ByteBuf, ByteBuf> newServer() {
		return newServer(0);
	}

	public static TcpServer<ByteBuf, ByteBuf> newServer(int port) {
		return new TcpServerImpl<>(new InetSocketAddress(port));
	}

	public static TcpServer<ByteBuf, ByteBuf> newServer(int port, EventLoopGroup eventLoopGroup,
			Class<? extends ServerChannel> channelClass) {
		return newServer(port, eventLoopGroup, eventLoopGroup, channelClass);
	}

	public static TcpServer<ByteBuf, ByteBuf> newServer(int port, EventLoopGroup acceptGroup,
			EventLoopGroup clientGroup, Class<? extends ServerChannel> channelClass) {
		return newServer(new InetSocketAddress(port), acceptGroup, clientGroup, channelClass);
	}

	public static TcpServer<ByteBuf, ByteBuf> newServer(SocketAddress socketAddress) {
		return new TcpServerImpl<>(socketAddress);
	}

	public static TcpServer<ByteBuf, ByteBuf> newServer(SocketAddress socketAddress, EventLoopGroup eventLoopGroup,
			Class<? extends ServerChannel> channelClass) {
		return new TcpServerImpl<>(socketAddress, eventLoopGroup, eventLoopGroup, channelClass);
	}

	public static TcpServer<ByteBuf, ByteBuf> newServer(SocketAddress socketAddress, EventLoopGroup acceptGroup,
			EventLoopGroup clientGroup, Class<? extends ServerChannel> channelClass) {
		return new TcpServerImpl<>(socketAddress, acceptGroup, clientGroup, channelClass);
	}

}
