package com.payment.netty.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.pool.ChannelPool;

public class TCPClient {
	private ChannelPool pool;
	private String id;
	private ReentrantLock lock = new ReentrantLock();

	public TCPClient(ChannelPool pool, String id) {
		this.pool = pool;
		this.id = id;
	}

	public void processTransaction(ByteBuf buf) {
		try {
			//System.out.println(id + " " + lock.isLocked());
			lock.lock();
			Channel channel = pool.acquire().syncUninterruptibly().getNow();
			CompletableFuture<Boolean> event = new CompletableFuture<>();
			channel.pipeline().fireUserEventTriggered(event);

			ChannelFuture chFuture = channel.writeAndFlush(buf);
			chFuture.addListener(future -> {
				if (future.isSuccess()) {
					System.out.println("write successfully response by TCPClient " + id);
				} else {
					System.out.println(
							"write un-successfully response by TCPClient  " + future.cause().getLocalizedMessage());
				}
			});
			try {
				boolean status = event.get(30000, TimeUnit.MILLISECONDS);
				if (status) {
					System.out.println("Response recieved by connector" + id);
					pool.release(channel);
				}
			} catch (Exception tEx) {
				// handle response not comming on time
			}

		} catch (Exception ex) {

		} finally {
			lock.unlock();
		}
	}

}
