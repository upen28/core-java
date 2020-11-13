package com.payment.tcp.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
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
	private AtomicInteger success = new AtomicInteger();
	private AtomicInteger error = new AtomicInteger();
	private ExecutorService service = Executors.newCachedThreadPool();

	public void processTrans() throws Exception {

		sendTransaction(PostPackagerRND.getSignInMsg());
		sleep(10000);

		sendTransaction(PostPackagerRND.getPinWorkingKey());

		Runnable echoTask = () -> {
			while (true) {
				try {
					sendTransaction(PostPackagerRND.getEchoMsg());
				} catch (Exception e) {
					System.out.println("Exception in sending message" + e.getLocalizedMessage());
				}
				sleep(10000);
			}
		};
		service.execute(echoTask);
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
			ByteBuf resByteBuf = multiplexer.processTransaction(reqByteBuf, 40000);
			if (resByteBuf != null && resByteBuf.isReadable()) {
				success.getAndAdd(1);
				ISOMsg respIsoMsg = new ISOMsg();
				GenericPackager packager = new GenericPackager("jar:postpack.xml");
				respIsoMsg.setPackager(packager);
				byte[] dstArray = new byte[resByteBuf.readableBytes()];
				resByteBuf.readBytes(dstArray);
				respIsoMsg.unpack(dstArray);
				respIsoMsg.dump(System.out, "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			error.getAndAdd(1);
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
