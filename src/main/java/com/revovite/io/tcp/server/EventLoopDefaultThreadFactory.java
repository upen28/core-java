package com.revovite.io.tcp.server;

import io.netty.util.concurrent.DefaultThreadFactory;

public class EventLoopDefaultThreadFactory extends DefaultThreadFactory {
    public EventLoopDefaultThreadFactory(String threadNamePrefix) {
        super(threadNamePrefix, true);
    }
}
