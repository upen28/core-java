
package com.payment.netty.client;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.util.LogSource;
import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;

import com.payment.netty.handlers.PostilionDecoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;

public class PostilionClient {

    private Channel connect(String ip, int port) {
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);
        try {
            Bootstrap bootStrap = new Bootstrap();
            bootStrap.group(workerGroup);
            bootStrap.channel(NioSocketChannel.class);
            bootStrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootStrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000);
            bootStrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new PostilionDecoder());
                }
            });
            ChannelFuture connectFuture = bootStrap.connect(ip, port);
            connectFuture.awaitUninterruptibly();
            if (connectFuture.isSuccess()) {
                System.out.print("connection successed ");
            } else {
                throw new RuntimeException("connection fail ");
            }
            SslHandler sslHandler = (SslHandler) connectFuture.channel().pipeline().get(SslHandler.class);
            if (sslHandler != null) {
                Future<?> sslFuture = sslHandler.handshakeFuture();
                sslFuture.awaitUninterruptibly();
                if (sslFuture.isSuccess()) {
                    System.out.print("handskaing successed ");
                } else {
                    throw new RuntimeException("handskaing failed ");
                }
            } else {
                System.out.println("ssl is not configured");
            }
            return connectFuture.channel();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void processTransaction() throws Exception {
        Channel channel = connect("127.0.0.1", 2501);
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(to_0100Req());
        channel.writeAndFlush(buf);
    }

    private ByteBuf to_0100Req() throws Exception {
        ISOMsg message = new ISOMsg();
        Logger logger = new Logger();
        logger.addListener(new SimpleLogListener(System.out));

        GenericPackager packager = new GenericPackager("jar:post-packager.xml");
        ((LogSource) packager).setLogger(logger, "debug");
        message.setPackager(packager);

        message.setMTI("0100");
        message.set(2, "5573890087012011");
        message.set(3, "300000");
        message.set(4, "000000000000");
        message.set(7, "0912234624");
        message.set(11, "000005");
        message.set(12, "234624");
        message.set(13, "0912");
        message.set(18, "6011");

        message.set(22, "001");
        message.set(25, "00");
        message.set(32, "00123456789");
        message.set(35, "5573890087012011D30072011000000000684");
        message.set(37, "918223000005");

        message.set(41, "RRL74151");
        message.set(42, "0000PDBB7WSHUH5");
        message.set(43, "ACQUIRER^NAME^^^^^^^^^^^^CITY^NAME^^^^US");

        message.set(52, "0CC6A0044B53CA3A");

        message.set(123, "000000123456789");

        message.dump(System.out, "");

        byte[] dst = message.pack();

        System.out.println("request hex dump" + System.lineSeparator() + hexDump(dst));

        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(dst);

        int len = buf.readableBytes();

        ByteBuf twoByteBuf = Unpooled.buffer();
        twoByteBuf.writeByte(len >> 8);
        twoByteBuf.writeByte(len);
        twoByteBuf.writeBytes(buf);

        return twoByteBuf;
    }

    public static void main(String[] args) throws Exception {
        PostilionClient client = new PostilionClient();
        client.processTransaction();
        Thread.sleep(5000);
    }

    public static String hexDump(ByteBuf buffer) {
        byte[] dst = new byte[buffer.readableBytes()];
        buffer.getBytes(0, dst);
        return ISOUtil.hexdump(dst);
    }

    public static String hexDump(byte[] buffer) {
        return ISOUtil.hexdump(buffer);
    }

}
