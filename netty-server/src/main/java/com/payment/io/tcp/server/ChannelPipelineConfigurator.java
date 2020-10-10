package com.payment.io.tcp.server;

import java.util.LinkedList;
import java.util.Map.Entry;

import javax.net.ssl.SSLEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.EventExecutorGroup;

public class ChannelPipelineConfigurator {

    private static final Logger logger = LoggerFactory.getLogger(ChannelPipelineConfigurator.class);
    private final LinkedList<HandlerHolder> holdersInOrder;

    public ChannelPipelineConfigurator() {
        holdersInOrder = new LinkedList<>();
    }

    private ChannelPipelineConfigurator(final ChannelPipelineConfigurator copyFrom) {
        holdersInOrder = new LinkedList<>();
        synchronized (copyFrom.holdersInOrder) {
            for (HandlerHolder handlerHolder : copyFrom.holdersInOrder) {
                holdersInOrder.addLast(handlerHolder);
            }
        }
    }

    public ChannelInitializer<Channel> getChannelInitializer() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                final ChannelPipeline pipeline = ch.pipeline();
                synchronized (holdersInOrder) {
                    unguardedCopyToPipeline(pipeline);
                }
            }
        };
    }

    public ChannelPipelineConfigurator addLast(String name, ChannelHandler channelHandler) {
        return _guardedAddLast(new HandlerHolder(name, channelHandler));
    }

    public ChannelPipelineConfigurator configureSSL(SSLEngine sslEngine) {
        SslHandler toReturn = new SslHandler(sslEngine);
        return _guardedAddFirst(new HandlerHolder("ssl", toReturn));
    }

    private ChannelPipelineConfigurator _guardedAddFirst(HandlerHolder toAdd) {
        synchronized (holdersInOrder) {
            holdersInOrder.addFirst(toAdd);
        }
        return this;
    }

    private ChannelPipelineConfigurator _guardedAddLast(HandlerHolder toAdd) {
        synchronized (holdersInOrder) {
            holdersInOrder.addLast(toAdd);
        }
        return this;
    }

    private void unguardedCopyToPipeline(ChannelPipeline pipeline) {
        for (HandlerHolder holder : holdersInOrder) {
            if (holder.hasGroup()) {
                if (holder.hasName()) {
                    pipeline.addLast(holder.getGroupIfConfigured(), holder.getNameIfConfigured(),
                            holder.getChannelHandler());
                } else {
                    pipeline.addLast(holder.getGroupIfConfigured(), holder.getChannelHandler());
                }
            } else if (holder.hasName()) {
                pipeline.addLast(holder.getNameIfConfigured(), holder.getChannelHandler());
            } else {
                pipeline.addLast(holder.getChannelHandler());
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Channel pipeline in initializer: " + pipelineToString(pipeline));
        }
    }

    private static String pipelineToString(ChannelPipeline pipeline) {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, ChannelHandler> handlerEntry : pipeline) {
            if (builder.length() == 0) {
                builder.append("[\n");
            } else {
                builder.append(" ==> ");
            }
            builder.append("{ name =>").append(handlerEntry.getKey()).append(", handler => ")
                    .append(handlerEntry.getValue()).append("}\n");
        }

        if (builder.length() > 0) {
            builder.append("}\n");
        }
        return builder.toString();
    }

    public ChannelPipelineConfigurator copy() {
        return new ChannelPipelineConfigurator(this);
    }

    static class HandlerHolder {

        private final String nameIfConfigured;
        private final ChannelHandler channelHandler;
        private final EventExecutorGroup groupIfConfigured;

        HandlerHolder(String name, ChannelHandler channelHandler) {
            this(name, channelHandler, null);
        }

        HandlerHolder(String name, ChannelHandler channelHandler, EventExecutorGroup group) {
            nameIfConfigured = name;
            this.channelHandler = channelHandler;
            groupIfConfigured = group;
        }

        public String getNameIfConfigured() {
            return nameIfConfigured;
        }

        public ChannelHandler getChannelHandler() {
            return channelHandler;
        }

        public EventExecutorGroup getGroupIfConfigured() {
            return groupIfConfigured;
        }

        public boolean hasName() {
            return null != nameIfConfigured;
        }

        public boolean hasGroup() {
            return null != groupIfConfigured;
        }
    }
}
