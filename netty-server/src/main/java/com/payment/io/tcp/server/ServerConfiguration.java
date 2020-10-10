package com.payment.io.tcp.server;

import java.net.SocketAddress;

import javax.net.ssl.SSLEngine;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;

public abstract class ServerConfiguration<R, W> {
    protected final SocketAddress socketAddress;
    protected final ServerBootstrap bootstrap;
    protected final ChannelPipelineConfigurator pipeLine;

    protected ServerConfiguration(SocketAddress socketAddress, EventLoopGroup parent, EventLoopGroup child,
            Class<? extends ServerChannel> channelClass) {
        this.socketAddress = socketAddress;
        bootstrap = new ServerBootstrap();
        bootstrap.childOption(ChannelOption.AUTO_READ, false);
        bootstrap.group(parent, child);
        bootstrap.channel(channelClass);
        pipeLine = new ChannelPipelineConfigurator();
        bootstrap.childHandler(pipeLine.getChannelInitializer());
    }

    protected ServerConfiguration(ServerConfiguration<R, W> toCopy, final ServerBootstrap newBootstrap) {
        socketAddress = toCopy.socketAddress;
        bootstrap = newBootstrap;
        pipeLine = toCopy.pipeLine;
        bootstrap.childHandler(pipeLine.getChannelInitializer());
    }

    protected ServerConfiguration(ServerConfiguration<?, ?> toCopy, final ChannelPipelineConfigurator newPipeline) {
        socketAddress = toCopy.socketAddress;
        bootstrap = toCopy.bootstrap.clone();
        pipeLine = newPipeline;
        bootstrap.childHandler(pipeLine.getChannelInitializer());
    }

    protected ServerConfiguration(ServerConfiguration<R, W> toCopy, final SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
        bootstrap = toCopy.bootstrap.clone();
        pipeLine = toCopy.pipeLine;
        bootstrap.childHandler(pipeLine.getChannelInitializer());
    }

    public <RR, WW> ServerConfiguration<RR, WW> configureSSL(SSLEngine sslEngine) {
        ServerConfiguration<RR, WW> copy = copy();
        copy.pipeLine.configureSSL(sslEngine);
        return copy;
    }

    public <T> ServerConfiguration<R, W> channelOption(ChannelOption<T> option, T value) {
        ServerConfiguration<R, W> copy = copyBootstrapOnly();
        copy.bootstrap.option(option, value);
        return copy;
    }

    public <T> ServerConfiguration<R, W> clientChannelOption(ChannelOption<T> option, T value) {
        ServerConfiguration<R, W> copy = copyBootstrapOnly();
        copy.bootstrap.childOption(option, value);
        return copy;
    }

    public <RR, WW> ServerConfiguration<RR, WW> addChannelHandlerLast(String name, ChannelHandler channelHandler) {
        ServerConfiguration<RR, WW> copy = copy();
        copy.pipeLine.addLast(name, channelHandler);
        return copy;
    }

    public SocketAddress getServerAddress() {
        return socketAddress;
    }

    protected abstract ServerConfiguration<R, W> copyBootstrapOnly();

    protected abstract <RR, WW> ServerConfiguration<RR, WW> copy();

    protected abstract ServerConfiguration<R, W> copy(SocketAddress newSocketAddress);

}
