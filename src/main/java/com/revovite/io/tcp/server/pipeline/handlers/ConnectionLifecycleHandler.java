package com.revovite.io.tcp.server.pipeline.handlers;

import static com.renovite.communication.mgmt.SharedConstants.REMOTE_CHANNEL_INST_ID;

import java.net.SocketAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.renovite.communication.acq.ch.InChannelRegistry;
import com.renovite.communication.acq.ch.TCPServer;
import com.renovite.communication.acq.ch.handler.ConnectionAcceptHandler;
import com.renovite.communication.mgmt.SocketAddressParser;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

public class ConnectionLifecycleHandler<I, O> extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LogManager.getLogger(TCPServer.class);

    private Map<String, AtomicInteger> ipSessions;

    private AtomicInteger sessions;

    private InChannelRegistry inChannelRegistry;

    private ConnectionAcceptHandler connectionAcceptHandler;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        sessions.decrementAndGet();
        String ipAddress = SocketAddressParser.getIP(ctx.channel().remoteAddress());
        getInChannelRegistry().deRegisterClientChannel(
                (String) ctx.channel().attr(AttributeKey.valueOf(REMOTE_CHANNEL_INST_ID)).get());
        AtomicInteger ai = ipSessions.get(ipAddress);
        if (ai != null) {
            synchronized (ipSessions) {
                ai.decrementAndGet();
                if (ai.intValue() == 0) {
                    ipSessions.remove(ipAddress);
                }
            }
        }
        super.channelInactive(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ipSessions = new ConcurrentHashMap<>();
        sessions = new AtomicInteger();
        synchronized (ipSessions) {
            if (!canAccept(ctx.channel().remoteAddress())) {
                ctx.channel().close();
                return;
            } else {
                String remoteChannelInstId = UUID.randomUUID().toString();
                ctx.channel().attr(AttributeKey.valueOf(REMOTE_CHANNEL_INST_ID)).set(remoteChannelInstId);
                getInChannelRegistry().registerClientChannel(remoteChannelInstId, null, null);
            }
        }
    }

    private boolean canAccept(SocketAddress address) {
        boolean lReturnVal = Boolean.TRUE;
        String ipAddress = SocketAddressParser.getIP(address);
        try {
            if (connectionAcceptHandler != null) {
                AtomicInteger ai = ipSessions.get(ipAddress);
                lReturnVal = connectionAcceptHandler.accept(sessions.intValue(), ai != null ? ai.intValue() : 0,
                        address);
            }
        } catch (Exception exp) {
            logger.error("({}) exception in connectionAcceptHandler, rejecting connection --{}",
                    ConnectionLifecycleHandler.class.getSimpleName(), exp);
        }
        if (lReturnVal) {
            sessions.incrementAndGet();
            AtomicInteger ai = ipSessions.get(ipAddress);
            if (ai != null) {
                ai.incrementAndGet();
            } else {
                ipSessions.put(ipAddress, new AtomicInteger(1));
            }
        }
        return lReturnVal;
    }

    public InChannelRegistry getInChannelRegistry() {
        return inChannelRegistry;
    }

    public void setInChannelRegistry(InChannelRegistry inChannelRegistry) {
        this.inChannelRegistry = inChannelRegistry;
    }

    public ConnectionAcceptHandler getConnectionAcceptHandler() {
        return connectionAcceptHandler;
    }

    public void setConnectionAcceptHandler(ConnectionAcceptHandler connectionAcceptHandler) {
        this.connectionAcceptHandler = connectionAcceptHandler;
    }

}
