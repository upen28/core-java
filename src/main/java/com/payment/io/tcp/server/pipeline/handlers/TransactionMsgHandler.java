package com.payment.io.tcp.server.pipeline.handlers;

import static com.renovite.communication.mgmt.SharedConstants.CURRENT_TIME;
import static com.renovite.communication.mgmt.SharedConstants.OUTPUT_QUEUE;
import static com.renovite.communication.mgmt.SharedConstants.REMOTE_CHANNEL_INST_ID;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import com.renovite.communication.acq.IQueueClient;
import com.renovite.communication.mgmt.SharedConstants;
import com.renovite.renoswitch.shared.lib.common.StatsCollector;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

public class TransactionMsgHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(TransactionMsgHandler.class);

    @Autowired
    private IQueueClient<ByteBuf> queueClient;

    @Autowired
    private StatsCollector statsCollector;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        try {
            if (byteBuf != null) {
                byte[] bts = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(bts);
                Message<?> message = buildMessage(bts, ctx);
                postMessageToQueue(message);
            }
        } finally {
            byteBuf.release();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void postMessageToQueue(Message message) {
        boolean status = getQueueClient().sendMessage(message);
        if (!status) {
            logger.error("Error to send message on queue");
        }
    }

    private Message<?> buildMessage(byte[] inputByteArray, ChannelHandlerContext ctx) {
        String ip = ((InetSocketAddress) ctx.channel().localAddress()).getHostName();
        int port = ((InetSocketAddress) ctx.channel().localAddress()).getPort();
        String remoteChannelInstId = (String) ctx.channel().attr(AttributeKey.valueOf("REMOTE_CHANNEL_INST_ID")).get();
        return MessageBuilder.withPayload(inputByteArray).setHeader(REMOTE_CHANNEL_INST_ID, remoteChannelInstId)
                .setHeader(OUTPUT_QUEUE, ip.concat(":").concat(String.valueOf(port)))
                .setHeader(CURRENT_TIME, System.nanoTime())
                .setHeader(SharedConstants.REMOTE_CLIENT_ADDRESS, getClientAddress(ctx)).build();
    }

    private String getClientAddress(ChannelHandlerContext ctx) {
        return ((InetSocketAddress) ctx.channel().localAddress()).getHostName() + ":"
                + ((InetSocketAddress) ctx.channel().localAddress()).getPort();
    }

    public IQueueClient<ByteBuf> getQueueClient() {
        return queueClient;
    }

    public void setQueueClient(IQueueClient<ByteBuf> queueClient) {
        this.queueClient = queueClient;
    }

}