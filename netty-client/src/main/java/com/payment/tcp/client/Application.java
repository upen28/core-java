package com.payment.tcp.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import com.payment.tcp.client.properties.TcpClientProperties;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

@SpringBootApplication
@PropertySource("classpath:tcpclient.properties")
@EnableConfigurationProperties(TcpClientProperties.class)
public class Application {
	@Autowired
	private TcpClientMultiplexer multiplexer;

	public void processTrans() throws Exception {
		for (int i = 0; i < 1; i++) {
			multiplexer.createConnection();
		}

		sleep(Integer.MAX_VALUE);
	}

	public void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void sendTransaction(byte[] dst) {
		ByteBuf isoByteBuf = Unpooled.buffer();
		isoByteBuf.writeBytes(dst);
		int len = isoByteBuf.readableBytes();

		ByteBuf reqByteBuf = Unpooled.buffer();
		reqByteBuf.writeByte(len >> 8);
		reqByteBuf.writeByte(len);
		reqByteBuf.writeBytes(isoByteBuf);
		try {
			multiplexer.processTransaction(reqByteBuf, 40000);
		} catch (Exception e) {
			System.out.println("exception in sending request");
		}

	};

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
