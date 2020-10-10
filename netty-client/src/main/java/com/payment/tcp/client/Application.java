package com.payment.tcp.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import com.payment.netty.PostPackagerRND;
import com.payment.tcp.client.properties.TcpClientProperties;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

@SpringBootApplication
@PropertySource("classpath:tcpclient.properties")
@EnableConfigurationProperties(TcpClientProperties.class)
public class Application {
	@Autowired
	private TcpClientSyncMultiplexer multiplexer;
	private ExecutorService executor = Executors.newFixedThreadPool(1);
	private AtomicInteger success = new AtomicInteger();
	private AtomicInteger error = new AtomicInteger();

	public void processTrans() throws Exception {
		Runnable task = () -> {
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
			try {
				ByteBuf resp = multiplexer.processTransaction(reqByteBuf, 40000);
				if (resp != null && resp.isReadable()) {
					success.getAndAdd(1);
				}
			} catch (Exception e) {
				error.getAndAdd(1);
			}
		};
		for (int i = 0; i < 1; i++) {
			executor.execute(task);
		}
		Thread.sleep(300000);
		System.out.println("success " + success.get());
		System.out.println("error " + error.get());

	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			processTrans();
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
