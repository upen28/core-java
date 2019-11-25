package com.revovite.io.tcp.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.SSLEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;

public class TcpServerImpl<R, W> extends TcpServer<R, W> {

    private static final Logger logger = LoggerFactory.getLogger(TcpServerImpl.class);

    protected enum ServerStatus {
        Created, Starting, Started, Shutdown
    }

    private final ServerConfiguration<R, W> configuration;
    private ChannelFuture bindFuture;
    protected final AtomicReference<ServerStatus> serverStateRef;

    public TcpServerImpl(SocketAddress socketAddress) {
        configuration = TcpServerConfiguration.create(socketAddress);
        serverStateRef = new AtomicReference<>(ServerStatus.Created);
    }

    public TcpServerImpl(SocketAddress socketAddress, EventLoopGroup parent, EventLoopGroup child,
            Class<? extends ServerChannel> channelClass) {
        configuration = TcpServerConfiguration.create(socketAddress, parent, child, channelClass);
        serverStateRef = new AtomicReference<>(ServerStatus.Created);
    }

    private TcpServerImpl(ServerConfiguration<R, W> state) {
        this.configuration = state;
        serverStateRef = new AtomicReference<>(ServerStatus.Created);
    }

    @Override
    public <T> TcpServer<R, W> channelOption(ChannelOption<T> option, T value) {
        return copy(configuration.channelOption(option, value));
    }

    @Override
    public <T> TcpServer<R, W> clientChannelOption(ChannelOption<T> option, T value) {
        return copy(configuration.clientChannelOption(option, value));
    }

    @Override
    public <RR, WW> TcpServer<RR, WW> addChannelHandlerLast(String name, ChannelHandler channelHandler) {
        return copy(configuration.<RR, WW> addChannelHandlerLast(name, channelHandler));
    }

    @Override
    public TcpServer<R, W> secure(SSLEngine sslEngine) {
        return copy(((TcpServerConfiguration<R, W>) configuration).secure(sslEngine));
    }

    @Override
    public int getServerPort() {
        final SocketAddress localAddress = getServerAddress();
        if (localAddress instanceof InetSocketAddress) {
            return ((InetSocketAddress) localAddress).getPort();
        } else {
            return 0;
        }
    }

    @Override
    public SocketAddress getServerAddress() {
        SocketAddress localAddress;
        if (null != bindFuture && bindFuture.isDone()) {
            localAddress = bindFuture.channel().localAddress();
        } else {
            localAddress = configuration.getServerAddress();
        }
        return localAddress;
    }

    @Override
    public TcpServer<R, W> start() {
        return null;
    }

    @Override
    public void shutdown() {
        if (!serverStateRef.compareAndSet(ServerStatus.Started, ServerStatus.Shutdown)) {
            throw new IllegalStateException("The server is already shutdown.");
        } else {
            try {
                bindFuture.channel().close().sync();
            } catch (InterruptedException e) {
                logger.error("Interrupted while waiting for the server socket to close.", e);
            }
        }
    }

    @Override
    public void awaitShutdown() {
        ServerStatus status = serverStateRef.get();
        switch (status) {
        case Created:
        case Starting:
            throw new IllegalStateException("Server not started yet.");
        case Started:
            try {
                bindFuture.channel().closeFuture().await();
            } catch (InterruptedException e) {
                Thread.interrupted();
                logger.error("Interrupted while waiting for the server socket to close.", e);
            }
            break;
        case Shutdown:
            break;
        }
    }

    private static <RR, WW> TcpServer<RR, WW> copy(ServerConfiguration<RR, WW> newState) {
        return new TcpServerImpl<>(newState);
    }

}
