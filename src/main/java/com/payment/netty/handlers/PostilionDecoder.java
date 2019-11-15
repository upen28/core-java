
package com.payment.netty.handlers;

import java.util.List;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.util.LogSource;
import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class PostilionDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        ByteBuf isoByteBuf = (ByteBuf) decode(ctx, byteBuf);
        if (isoByteBuf != null) {
            out.add(isoByteBuf);
        }
        System.out.println("response hex dump" + System.lineSeparator() + hexDump(isoByteBuf));

        Logger logger = new Logger();
        logger.addListener(new SimpleLogListener(System.out));

        int tLen = isoByteBuf.readableBytes();
        GenericPackager packager = new GenericPackager("jar:post-packager.xml");
        ((LogSource) packager).setLogger(logger, "debug");

        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setDirection(1);
        isoMsg.setPackager(packager);

        byte[] messageBytes = new byte[tLen];
        isoByteBuf.readBytes(messageBytes);
        isoMsg.unpack(messageBytes);

        isoMsg.dump(System.out, "");
    }

    private Object decode(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        if (byteBuf.readableBytes() < 2) {
            return null;
        }
        byteBuf.markReaderIndex();

        byte[] array = new byte[2];
        byteBuf.readBytes(array);

        int dataLength = 0;
        try {
            dataLength = ((int) array[0] & 0xFF) << 8 | (int) array[1] & 0xFF;
        } catch (Exception ex) {
            System.out.println("Invalid data length received {}" + new String(array));
            byteBuf.resetReaderIndex();
            return null;
        }
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return null;
        }
        ByteBuf frame = extractFrame(byteBuf, dataLength);

        return frame;
    }

    protected ByteBuf extractFrame(ByteBuf buffer, int length) {
        ByteBuf frame = Unpooled.buffer(length);
        frame.writeBytes(buffer, length);
        return frame;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exception 	caught " + this.getClass().getName() + "  " + cause.getMessage());
        super.exceptionCaught(ctx, cause);

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
