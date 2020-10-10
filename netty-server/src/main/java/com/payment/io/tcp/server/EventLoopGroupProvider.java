package com.payment.io.tcp.server;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class EventLoopGroupProvider {
	private EventLoopGroup eventLoop;
	private final int eventLoopCount;

	public EventLoopGroupProvider() {
		this(Runtime.getRuntime().availableProcessors());
	}

	public EventLoopGroupProvider(int threadCount) {
		eventLoopCount = threadCount;
		eventLoop = new NioEventLoopGroup(eventLoopCount, new EventLoopDefaultThreadFactory("netty-nio-eventloop"));
	}

	public EventLoopGroup globalServerEventLoop() {
		return eventLoop;
	}

	public EventLoopGroup globalServerEventLoop(boolean nativeTransport) {
		if (nativeTransport && Epoll.isAvailable()) {
			return getNativeEventLoop();
		} else if (nativeTransport && KQueue.isAvailable()) {
			return getMacOsEventLoop();
		} else {
			return globalServerEventLoop();
		}
	}

	private EventLoopGroup getNativeEventLoop() {
		EventLoopGroup eventLoopGroup = this.eventLoop;
		if (null == eventLoopGroup) {
			eventLoopGroup = new EpollEventLoopGroup(eventLoopCount,
					new EventLoopDefaultThreadFactory("netty-epoll-eventloop"));
		}
		return eventLoopGroup;
	}

	private EventLoopGroup getMacOsEventLoop() {
		EventLoopGroup eventLoopGroup = this.eventLoop;
		if (null == eventLoopGroup) {
			eventLoopGroup = new KQueueEventLoopGroup(eventLoopCount,
					new EventLoopDefaultThreadFactory("netty-kQueue-eventloop"));
		}
		return eventLoopGroup;
	}
}
