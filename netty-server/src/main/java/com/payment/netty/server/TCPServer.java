package com.payment.netty.server;

import java.net.InetSocketAddress;

import com.payment.netty.server.handlers.PostilionDecoder;
import com.payment.netty.server.handlers.PostilionReqHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TCPServer {

	public void startTcpServer() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(group);
			serverBootstrap.channel(NioServerSocketChannel.class);
			serverBootstrap.localAddress(new InetSocketAddress("localhost", 9999));

			serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel socketChannel) throws Exception {
					socketChannel.pipeline().addLast(new PostilionDecoder());
					socketChannel.pipeline().addLast(new PostilionReqHandler());
				}
			});
			ChannelFuture channelFuture = serverBootstrap.bind().sync();
			System.out.println("Server is started at port 9999");
			channelFuture.channel().closeFuture().sync();
			System.out.println("Server is stopped at port 9999");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully().sync();
		}
		System.out.println("Existing  TCPServer");
	}

	public static void main(String... args) throws Exception {
		TCPServer server = new TCPServer();
		server.startTcpServer();
	}
}
