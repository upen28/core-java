package com.payment.io.tcp.server;

import java.net.SocketAddress;

import javax.net.ssl.SSLEngine;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;

public class TcpServerConfiguration<R, W> extends ServerConfiguration<R, W> {

    private final boolean secure;

    protected TcpServerConfiguration(SocketAddress socketAddress, EventLoopGroup parent, EventLoopGroup child,
            Class<? extends ServerChannel> channelClass) {
        super(socketAddress, parent, child, channelClass);
        secure = false;
    }

    protected TcpServerConfiguration(TcpServerConfiguration<?, ?> toCopy, SSLEngine sslEngine) {
        super(toCopy, toCopy.pipeLine.configureSSL(sslEngine));
        secure = true;
    }

    protected TcpServerConfiguration(TcpServerConfiguration<R, W> toCopy, SocketAddress socketAddress) {
        super(toCopy, socketAddress);
        secure = toCopy.secure;
    }

    protected TcpServerConfiguration(TcpServerConfiguration<R, W> toCopy, ServerBootstrap clone) {
        super(toCopy, clone);
        secure = toCopy.secure;
    }

    protected TcpServerConfiguration(TcpServerConfiguration<?, ?> toCopy, ChannelPipelineConfigurator newPipeline) {
        super(toCopy, newPipeline);
        secure = toCopy.secure;
    }

    public TcpServerConfiguration<R, W> secure(SSLEngine sslEngine) {
        return new TcpServerConfiguration<>(this, sslEngine);
    }

    @Override
    protected ServerConfiguration<R, W> copyBootstrapOnly() {
        return new TcpServerConfiguration<>(this, bootstrap.clone());
    }

    @Override
    protected <RR, WW> ServerConfiguration<RR, WW> copy() {
        return new TcpServerConfiguration<>(this, pipeLine.copy());
    }

    @Override
    protected ServerConfiguration<R, W> copy(SocketAddress newSocketAddress) {
        return new TcpServerConfiguration<>(this, socketAddress);
    }

    public static <RR, WW> TcpServerConfiguration<RR, WW> create(SocketAddress socketAddress) {
        return null;
    }

    public static <RR, WW> TcpServerConfiguration<RR, WW> create(SocketAddress socketAddress, EventLoopGroup group,
            Class<? extends ServerChannel> channelClass) {
        return null;
    }

    public static <RR, WW> TcpServerConfiguration<RR, WW> create(SocketAddress socketAddress, EventLoopGroup parent,
            EventLoopGroup child, Class<? extends ServerChannel> channelClass) {
        return new TcpServerConfiguration<>(socketAddress, parent, child, channelClass);
    }

}
