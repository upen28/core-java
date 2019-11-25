
package com.payment.io.tcp.server.pipeline.handlers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class LengthBasedDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LogManager.getLogger(LengthBasedDecoder.class);
    private int msgLength;
    private ByteDecoder byteDecoder;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        Object decoded = decode(ctx, in);
        if (decoded != null) {
            out.add(decoded);
        }
    }

    private Object decode(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        if (byteBuf.readableBytes() < msgLength) {
            return null;
        }
        byteBuf.markReaderIndex();
        byte[] array = new byte[msgLength];
        byteBuf.readBytes(array);

        int dataLength = byteDecoder.decode(array);
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return null;
        }
        ByteBuf frame = Unpooled.buffer(dataLength);
        frame.writeBytes(byteBuf, dataLength);
        return frame;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.fatal("exception 	caught " + this.getClass().getName() + "  " + cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

    public int getMsgLength() {
        return msgLength;
    }

    public void setMsgLength(int msgLength) {
        this.msgLength = msgLength;
        if (msgLength == 2) {
            byteDecoder = new TwoByteDecoder();
        } else if (msgLength == 4) {
            byteDecoder = new FourByteDecoder();
        }
    }

    public interface ByteDecoder {
        public int decode(byte[] arr);
    }

    public static class TwoByteDecoder implements ByteDecoder {
        @Override
        public int decode(byte[] arr) {
            return ((int) arr[0] & 0xFF) << 8 | (int) arr[1] & 0xFF;
        }
    }

    public static class FourByteDecoder implements ByteDecoder {

        @Override
        public int decode(byte[] arr) {
            return ((int) arr[0] & 0xFF) << 8 | (int) arr[1] & 0xFF;
        }
    }

}
