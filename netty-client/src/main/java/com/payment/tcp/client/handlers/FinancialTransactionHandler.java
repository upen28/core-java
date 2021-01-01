
package com.payment.tcp.client.handlers;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import com.payment.netty.PostPackagerRND;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class FinancialTransactionHandler extends ChannelInboundHandlerAdapter {

	private int counter = 0;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;
		unpackAndSend(ctx, byteBuf);
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		System.out.println("connection closed");
	}

	private void unpackAndSend(ChannelHandlerContext ctx, ByteBuf byteBuf) throws ISOException {
		GenericPackager packager = new GenericPackager("jar:postpack.xml");
		ISOMsg reqIsoMsg = new ISOMsg();
		reqIsoMsg.setPackager(packager);

		int len = byteBuf.readableBytes(); 
		byte[] messageBytes = new byte[len];
		byteBuf.readBytes(messageBytes);
		reqIsoMsg.unpack(messageBytes);
		reqIsoMsg.dump(System.out, "<--");

		if (reqIsoMsg.getMTI().equals("0800") && reqIsoMsg.getString("70").equals("301")
				|| reqIsoMsg.getMTI().equals("0800") && reqIsoMsg.getString("70").equals("101")) {
			counter++;
			ISOMsg resIsoMsg = (ISOMsg) reqIsoMsg.clone();
			resIsoMsg.setMTI("0810");
			resIsoMsg.set(39, "00");
			if (reqIsoMsg.getString("70").equals("101")) {
				//resIsoMsg.set(125, "1219E2C04801D64F90C1493B844B3F32C8CFB1");
			}
			resIsoMsg.dump(System.out, "-->");
			if (counter > 3) {
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {

				}
			}
			sendMessage(ctx, resIsoMsg.pack());
		} else if (reqIsoMsg.getMTI().equals("0100")) {
			byte[] dst = null;
			try {
				dst = PostPackagerRND.test0100Res(reqIsoMsg).pack();
			} catch (Exception e) {
				e.printStackTrace();
			}
			sendMessage(ctx, dst);
		}
	}

	private void sendMessage(ChannelHandlerContext ctx, byte[] dst) {
		System.out.println("sending to" + ctx.channel().remoteAddress());
		ByteBuf reqByteBuf = Unpooled.buffer();
		reqByteBuf.writeByte(dst.length >> 8);
		reqByteBuf.writeByte(dst.length);
		reqByteBuf.writeBytes(dst);
		ctx.writeAndFlush(reqByteBuf).addListener(future -> {
			if (future.isSuccess()) {
				System.out.println("write sucessfully");
			} else {
				System.out.println("write unsucessfully");
			}
		});
	}
}
