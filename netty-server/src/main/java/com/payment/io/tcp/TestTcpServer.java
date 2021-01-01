package com.payment.io.tcp;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.payment.io.tcp.server.pipeline.handlers.LengthBasedDecoder;
import com.payment.io.tcp.server.pipeline.handlers.TransactionInHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.reactivex.rxjava3.processors.PublishProcessor;

@SpringBootApplication
public class TestTcpServer {

	private static String protocol;
	private static String ip;
	private static String port;
	private static String ssl;
	private static String handshakeTimeout;

	public void runTcpServer() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(group);
			serverBootstrap.channel(NioServerSocketChannel.class);
			serverBootstrap.localAddress(new InetSocketAddress(ip, Integer.valueOf(port)))
					.childOption(ChannelOption.SO_KEEPALIVE, true).handler(new LoggingHandler(LogLevel.DEBUG));
			serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel socketChannel) throws Exception {
					File logFile = generateLogFile(socketChannel.remoteAddress().toString());
					log(logFile, "initChannel() connection from remote socket " + socketChannel.remoteAddress());
					if (Boolean.valueOf(ssl)) {
						List<String> ciphers = new ArrayList<String>();
						ciphers.add("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384");
						SslContext sslContext = SslContextBuilder.forServer(createKeyManagerFactory())
								.protocols(new String[] { protocol }).ciphers(ciphers).build();
						SSLEngine engine = sslContext.newEngine(socketChannel.alloc());
						SslHandler sslHandler = new SslHandler(engine);
						sslHandler.setHandshakeTimeout(Integer.valueOf(handshakeTimeout), TimeUnit.MINUTES);
						socketChannel.pipeline().addLast("ssl", sslHandler);
					}
					socketChannel.pipeline().addLast(new LengthBasedDecoder(logFile));

					PublishProcessor<ByteBuf> transactionProcessor = PublishProcessor.create();
					PublishProcessor<ChannelHandlerContext> channelHandlerProcessor = PublishProcessor.create();
					TransactionHandler txHandler = new TransactionHandler(transactionProcessor, channelHandlerProcessor,
							logFile);
					txHandler.initializeSubscribers();
					socketChannel.pipeline()
							.addLast(new TransactionInHandler(transactionProcessor, channelHandlerProcessor));

					socketChannel.pipeline().names().stream().forEach(handler -> {
						log(logFile, handler);
					});
					log(logFile, "leaving initChannel() from remote socket " + socketChannel.remoteAddress());
				}
			});
			ChannelFuture channelFuture = serverBootstrap.bind().sync();
			System.out.printf("Server started on ip=%S and port=%s", ip, port);
			channelFuture.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully().sync();
		}
	}

	private KeyManagerFactory createKeyManagerFactory() throws Exception {
		final KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(TestTcpServer.class.getResourceAsStream("/keystore.jks"), "secret".toCharArray());

		final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, "secret".toCharArray());
		return kmf;
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			runTcpServer();
		};
	}

	private File generateLogFile(String remoteAddress) throws IOException {
		LocalDateTime dateTime = LocalDateTime.now();
		String time = dateTime.format(DateTimeFormatter.ofPattern("MMddHHmmss"));

		File dir = new File("logs");
		dir.mkdirs();
		String fileName = remoteAddress.replace("/", "").replace(":", "#") + "#" + time;
		File logFile = new File(dir, fileName);
		log(logFile, " file created with name " + fileName);
		boolean status = logFile.createNewFile();
		if (status) {
			log(logFile, logFile.getAbsoluteFile() + " is created successfully");
		}
		return logFile;
	}

	private void log(File logFile, String log) {
		System.out.println(log);
		try {
			String logNewLine = log + System.lineSeparator();
			FileUtils.writeByteArrayToFile(logFile, logNewLine.getBytes(), true);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public static void main(String[] args) {
		System.setProperty("javax.net.debug", "all");
		ip = args[2];
		port = args[3];
		ssl = args[4];
		if (Boolean.valueOf(ssl)) {
			protocol = args[5];
			handshakeTimeout = args[6];
		}
		System.out.printf("server configuration[ip=%s port=%s ssl=%s protocol=%s handshake-timeout=%s]", ip, port, ssl,
				protocol, handshakeTimeout);
		SpringApplication.run(TestTcpServer.class, args);
	}
}
