package com.payment.tcp.client.eventgroup;

import io.netty.util.concurrent.DefaultThreadFactory;

public class EventLoopDefaultThreadFactory extends DefaultThreadFactory {
	public EventLoopDefaultThreadFactory(String threadNamePrefix) {
		super(threadNamePrefix, true);
	}
}
