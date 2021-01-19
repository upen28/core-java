package com.payment.io.tcp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FileUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.reactivex.rxjava3.processors.PublishProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TransactionHandlerMasterCard {
	private volatile boolean isRunning = true;
	private AtomicInteger stan = new AtomicInteger(10);
	private AtomicLong rrn = new AtomicLong(10);
	private File logFile;

	private ChannelHandlerContext channelContext;
	private PublishProcessor<ByteBuf> transactionProcessor;
	private PublishProcessor<ChannelHandlerContext> channelHandlerProcessor;

	public TransactionHandlerMasterCard() {
	}

	public TransactionHandlerMasterCard(PublishProcessor<ByteBuf> inTransactionHandler,
			PublishProcessor<ChannelHandlerContext> channelHandler, File logFile) {
		this.transactionProcessor = inTransactionHandler;
		this.channelHandlerProcessor = channelHandler;
		this.logFile = logFile;
	}

	public void initializeSubscribers() {
		channelHandlerProcessor.observeOn(Schedulers.io()).subscribe(channelContext -> {
			log(Thread.currentThread().getName());
			log("channel active from remote host " + channelContext.channel().remoteAddress());
			this.channelContext = channelContext;
			startSendingTransactions();
		}, exception -> {
			log("exception on socket " + channelContext.channel().remoteAddress() + exception.getLocalizedMessage());
		}, () -> {
			log("channel inactive from remote host " + channelContext.channel().remoteAddress());
			channelContext = null;
			stopSendingTransactions();
		});

		transactionProcessor.observeOn(Schedulers.io()).map(byteBuf -> {
			log(Thread.currentThread().getName());
			GenericPackager packager = new GenericPackager("jar:postpack.xml");
			ISOMsg reqIsoMsg = new ISOMsg();
			reqIsoMsg.setPackager(packager);

			int len = byteBuf.readableBytes();
			byte[] messageBytes = new byte[len];
			byteBuf.readBytes(messageBytes);

			reqIsoMsg.unpack(messageBytes);
			dump(reqIsoMsg, true);
			return reqIsoMsg;
		}).filter(reqIsoMsg -> {
			return reqIsoMsg.getMTI().equals("0800") && reqIsoMsg.getString("70").equals("301")
					|| reqIsoMsg.getMTI().equals("0800") && reqIsoMsg.getString("70").equals("101");
		}).doOnNext(reqIsoMsg -> {
			ISOMsg resIsoMsg = (ISOMsg) reqIsoMsg.clone();
			resIsoMsg.setMTI("0810");
			resIsoMsg.set(39, "00");
			resIsoMsg.dump(System.out, "-->");
			sendMessage(channelContext, resIsoMsg.pack());
		}).subscribe();
	}

	private void stopSendingTransactions() {
		isRunning = false;
	}

	private void startSendingTransactions() throws Exception {
		new Thread(() -> {
			int i = 0;
			while (isRunning) {
				byte[] respMsg;
				try {
					respMsg = getEchoMsg();
					log("sending echo request to ned bank");
					sendMessage(channelContext, respMsg);
					if (i == 0) {
						sleep(5000);
						respMsg = magStripeWithPinTxn();
						log("sending magStripeWithPinTxn request to ned bank");
						sendMessage(channelContext, respMsg);
					}
				} catch (Exception e) {
					e.printStackTrace();

				}
				sleep(20000);
			}
			log("==========leaving echo thread=========");
		}).start();
	}

	private void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}

	public byte[] getKeyExchangeMsg() throws Exception {
		Instant instant = Instant.now();
		LocalDateTime dateTimeUTC = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
		LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

		String field_7 = dateTimeUTC.format(DateTimeFormatter.ofPattern("MMddHHmmss"));
		String field_12 = dateTime.format(DateTimeFormatter.ofPattern("HHmmss"));
		String field_13 = dateTime.format(DateTimeFormatter.ofPattern("MMdd"));

		ISOMsg reqIsoMsg = new ISOMsg();
		GenericPackager packager = new GenericPackager("jar:postpack.xml");
		reqIsoMsg.setPackager(packager);
		reqIsoMsg.setMTI("0800");
		reqIsoMsg.set(7, field_7);
		reqIsoMsg.set(11, generateStan());
		reqIsoMsg.set(12, field_12);
		reqIsoMsg.set(13, field_13);
		reqIsoMsg.set(70, "101");
		dump(reqIsoMsg, false);
		return reqIsoMsg.pack();
	}

	private byte[] getEchoMsg() throws Exception {
		Instant instant = Instant.now();
		LocalDateTime dateTimeUTC = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);

		LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);

		LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

		String field_7 = dateTimeUTC.format(DateTimeFormatter.ofPattern("MMddHHmmss"));
		String field_12 = dateTime.format(DateTimeFormatter.ofPattern("HHmmss"));
		String field_13 = dateTime.format(DateTimeFormatter.ofPattern("MMdd"));

		ISOMsg reqIsoMsg = new ISOMsg();
		GenericPackager packager = new GenericPackager("jar:postpack.xml");
		reqIsoMsg.setPackager(packager);
		reqIsoMsg.setMTI("0800");
		reqIsoMsg.set(7, field_7);
		reqIsoMsg.set(11, generateStan());
		reqIsoMsg.set(12, field_12);
		reqIsoMsg.set(13, field_13);
		reqIsoMsg.set(70, "301");
		dump(reqIsoMsg, false);
		return reqIsoMsg.pack();
	}

	private byte[] magStripeWithPinTxn() throws Exception {

		System.out.println("magStripeWithPinTxn transaction");
		Instant instant = Instant.now();
		LocalDateTime dateTimeUTC = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
		LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

		String field_7 = dateTimeUTC.format(DateTimeFormatter.ofPattern("MMddHHmmss"));
		String field_12 = dateTime.format(DateTimeFormatter.ofPattern("HHmmss"));
		String field_13 = dateTime.format(DateTimeFormatter.ofPattern("MMdd"));

		ISOMsg reqIsoMsg = new ISOMsg();
		GenericPackager packager = new GenericPackager("jar:postpack.xml");
		reqIsoMsg.setPackager(packager);
		reqIsoMsg.set(0, "0100");
		reqIsoMsg.set(2, "5413330089020508");
		reqIsoMsg.set(3, "003000");
		reqIsoMsg.set(4, "000000030000");
		reqIsoMsg.set(7, field_7);
		reqIsoMsg.set(11, generateStan());
		reqIsoMsg.set(12, field_12);
		reqIsoMsg.set(13, field_13);
		reqIsoMsg.set(14, "2304");
		reqIsoMsg.set(15, field_13);

		reqIsoMsg.set(18, "5411");
		reqIsoMsg.set(22, "901");
		reqIsoMsg.set(23, "000");

		reqIsoMsg.set(25, "00");
		reqIsoMsg.set(26, "12");

		reqIsoMsg.set(32, "47689550000");
		reqIsoMsg.set(35, "5413330089020508=230422001003490");
		reqIsoMsg.set(37, genertaeRrn());
		reqIsoMsg.set(40, "220");
		reqIsoMsg.set(41, "00000001");
		reqIsoMsg.set(42, "000008300007336");
		reqIsoMsg.set(43, "WOERMANN OLIMPIA 60 - PWINDHOEK     NANA");
		reqIsoMsg.set(49, "710");
		reqIsoMsg.set(52, "A6A513ECE4AC3BFC");

		reqIsoMsg.set("123.1", "2");
		reqIsoMsg.set("123.2", "0");
		reqIsoMsg.set("123.3", "1");
		reqIsoMsg.set("123.4", "1");
		reqIsoMsg.set("123.5", "0");
		reqIsoMsg.set("123.6", "1");
		reqIsoMsg.set("123.7", "2");
		reqIsoMsg.set("123.8", "0");

		reqIsoMsg.set("123.9", "2");
		reqIsoMsg.set("123.10", "2");
		reqIsoMsg.set("123.11", "4");
		reqIsoMsg.set("123.12", "C");
		reqIsoMsg.set("123.13", "1");
		reqIsoMsg.set("123.14", "01");

		System.out.println(ISOUtil.hexdump(reqIsoMsg.pack()));

		dump(reqIsoMsg, false);
		return reqIsoMsg.pack();
	}

	private byte[] emvWithPin() throws Exception {
		System.out.println("emvWithPin transaction");
		Instant instant = Instant.now();
		LocalDateTime dateTimeUTC = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
		LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

		String field_7 = dateTimeUTC.format(DateTimeFormatter.ofPattern("MMddHHmmss"));
		String field_12 = dateTime.format(DateTimeFormatter.ofPattern("HHmmss"));
		String field_13 = dateTime.format(DateTimeFormatter.ofPattern("MMdd"));

		ISOMsg reqIsoMsg = new ISOMsg();
		GenericPackager packager = new GenericPackager("jar:postpack.xml");
		reqIsoMsg.setPackager(packager);
		reqIsoMsg.set(0, "0100");
		reqIsoMsg.set(2, "5413330089020508");
		reqIsoMsg.set(3, "003000");
		reqIsoMsg.set(4, "000000030000");
		reqIsoMsg.set(7, field_7);
		reqIsoMsg.set(11, generateStan());
		reqIsoMsg.set(12, field_12);
		reqIsoMsg.set(13, field_13);
		reqIsoMsg.set(14, "2304");
		reqIsoMsg.set(15, field_13);

		reqIsoMsg.set(18, "5411");
		reqIsoMsg.set(22, "051");
		reqIsoMsg.set(23, "000");

		reqIsoMsg.set(25, "00");
		reqIsoMsg.set(26, "12");

		reqIsoMsg.set(32, "47689550000");
		reqIsoMsg.set(35, "5387565376885327D230422001003490FFFFF");
		reqIsoMsg.set(37, genertaeRrn());
		reqIsoMsg.set(40, "220");
		reqIsoMsg.set(41, "00000001");
		reqIsoMsg.set(42, "000008300007336");
		reqIsoMsg.set(43, "WOERMANN OLIMPIA 60 - PWINDHOEK     NANA");
		reqIsoMsg.set(49, "710");
		reqIsoMsg.set(52, "A6A513ECE4AC3BFC");

		reqIsoMsg.set(59, "105272.32747");
		reqIsoMsg.set(100, "100");

		reqIsoMsg.set(123, "51010151133C101");

		ISOMsg _127Msg = new ISOMsg(127);
		reqIsoMsg.set(_127Msg);

		_127Msg.set(22, "[TenderDetailGUID]=[XXXXXXXX] [Postilion:MetaData]=[XXXXXXXX]");

		ISOMsg _1271_25Msg = new ISOMsg(25);
		_127Msg.set(_1271_25Msg);

		_1271_25Msg.set(2, "000000030000");
		_1271_25Msg.set(3, "000000000000");
		_1271_25Msg.set(4, "A0000000041010");
		_1271_25Msg.set(5, "3900");
		_1271_25Msg.set(6, "01A5");
		_1271_25Msg.set(7, "FFC0");
		_1271_25Msg.set(12, "C6AD42BB574E78C1");
		_1271_25Msg.set(13, "80");
		_1271_25Msg.set(14, "00000000000000004203440341031E031F0300000000000000000000");
		_1271_25Msg.set(15, "420300");
		_1271_25Msg.set(16, "62070604");
		_1271_25Msg.set(17, "BC70BC9800");
		_1271_25Msg.set(18, "0110A04003240000000000000000000000FF");
		_1271_25Msg.set(20, "0083");
		_1271_25Msg.set(21, "E0F0C8");
		_1271_25Msg.set(22, "710");
		_1271_25Msg.set(23, "22");
		_1271_25Msg.set(24, "0000048000");
		_1271_25Msg.set(26, "710");
		_1271_25Msg.set(27, "201223");
		_1271_25Msg.set(29, "00");
		_1271_25Msg.set(30, "0B661AF3");

		System.out.println(ISOUtil.hexdump(reqIsoMsg.pack()));

		dump(reqIsoMsg, false);
		return reqIsoMsg.pack();
	}

	private byte[] nfc() throws Exception {
		Instant instant = Instant.now();
		LocalDateTime dateTimeUTC = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
		LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

		String field_7 = dateTimeUTC.format(DateTimeFormatter.ofPattern("MMddHHmmss"));
		String field_12 = dateTime.format(DateTimeFormatter.ofPattern("HHmmss"));
		String field_13 = dateTime.format(DateTimeFormatter.ofPattern("MMdd"));

		ISOMsg reqIsoMsg = new ISOMsg();
		GenericPackager packager = new GenericPackager("jar:postpack.xml");
		reqIsoMsg.setPackager(packager);
		reqIsoMsg.set(0, "0100");
		reqIsoMsg.set(2, "5413330089020508");
		reqIsoMsg.set(3, "003000");
		reqIsoMsg.set(4, "000000030000");
		reqIsoMsg.set(7, field_7);
		reqIsoMsg.set(11, generateStan());
		reqIsoMsg.set(12, field_12);
		reqIsoMsg.set(13, field_13);
		reqIsoMsg.set(14, "2304");
		reqIsoMsg.set(15, field_13);

		reqIsoMsg.set(18, "5411");
		reqIsoMsg.set(22, "051");
		reqIsoMsg.set(23, "000");

		reqIsoMsg.set(25, "00");
		reqIsoMsg.set(26, "12");

		reqIsoMsg.set(32, "47689550000");
		reqIsoMsg.set(35, "5387565376885327D230422001003490");
		reqIsoMsg.set(37, genertaeRrn());
		reqIsoMsg.set(40, "220");
		reqIsoMsg.set(41, "00000001");
		reqIsoMsg.set(42, "000008300007336");
		reqIsoMsg.set(43, "WOERMANN OLIMPIA 60 - PWINDHOEK     NANA");
		reqIsoMsg.set(49, "710");
		// reqIsoMsg.set(52, "1DA3610DBCC39B00");

		// reqIsoMsg.set(59, "105272.32747");
		// reqIsoMsg.set(100, "100");

		reqIsoMsg.set(123, "51010151133C101");

		ISOMsg _127Msg = new ISOMsg(127);
		reqIsoMsg.set(_127Msg);

		// _127Msg.set(22, "[TenderDetailGUID]=[XXXXXXXX]
		// [Postilion:MetaData]=[XXXXXXXX]");

		ISOMsg _1271_25Msg = new ISOMsg(25);
		_127Msg.set(_1271_25Msg);

		_1271_25Msg.set(2, "000000030000");
		_1271_25Msg.set(3, "000000000000");
		_1271_25Msg.set(4, "A0000000041010");
		_1271_25Msg.set(5, "3900");
		_1271_25Msg.set(6, "01A5");
		_1271_25Msg.set(7, "FFC0");
		_1271_25Msg.set(12, "C6AD42BB574E78C1");
		_1271_25Msg.set(13, "80");
		_1271_25Msg.set(14, "00000000000000004203440341031E031F0300000000000000000000");
		_1271_25Msg.set(15, "420300");
		_1271_25Msg.set(16, "62070604");
		_1271_25Msg.set(17, "BC70BC9800");
		_1271_25Msg.set(18, "0110A04003240000000000000000000000FF");
		_1271_25Msg.set(20, "0083");
		_1271_25Msg.set(21, "E0F0C8");
		_1271_25Msg.set(22, "710");
		_1271_25Msg.set(23, "22");
		_1271_25Msg.set(24, "0000048000");
		_1271_25Msg.set(26, "710");
		_1271_25Msg.set(27, "201223");
		_1271_25Msg.set(29, "00");
		_1271_25Msg.set(30, "0B661AF3");

		System.out.println(ISOUtil.hexdump(reqIsoMsg.pack()));

		dump(reqIsoMsg, false);
		return reqIsoMsg.pack();
	}

	private String generateStan() throws ISOException {
		int result = stan.addAndGet(1);
		if (result > 999999) {
			stan.set(0);
			result = 0;
		}
		return ISOUtil.padleft(String.valueOf(result), 6, '0');
	}

	private String genertaeRrn() throws ISOException {
		long result = rrn.addAndGet(1);
		if (result > 999999999999L) {
			rrn.set(0);
			result = 0;
		}
		return ISOUtil.padleft(String.valueOf(result), 12, '0');
	}

	private void sendMessage(ChannelHandlerContext ctx, byte[] dst) {
		log("sending to" + ctx.channel().remoteAddress());
		ByteBuf reqByteBuf = Unpooled.buffer();
		reqByteBuf.writeByte(dst.length >> 8);
		reqByteBuf.writeByte(dst.length);
		reqByteBuf.writeBytes(dst);
		ctx.writeAndFlush(reqByteBuf).addListener(future -> {
			if (future.isSuccess()) {
				log("write sucessfully");
			} else {
				log("write unsucessfully");
			}
		});
	}

	private void dump(ISOMsg msg, boolean in) throws FileNotFoundException {
		PrintStream printStream = null;
		try {
			String direction = null;
			if (in) {
				direction = "<--";
			} else {
				direction = "-->";
			}
			msg.dump(System.out, direction);
			printStream = new PrintStream(new FileOutputStream(logFile, true));
			msg.dump(printStream, direction);
			printStream.flush();
		} finally {
			if (printStream != null) {
				printStream.close();
			}
		}
	}

	private void log(String log) {
		System.out.println(log);
		try {
			String logNewLine = log + System.lineSeparator();
			FileUtils.writeByteArrayToFile(logFile, logNewLine.getBytes(), true);
		} catch (IOException e) {
		}
	}

	public static void main(String... args) throws Exception {
		TransactionHandlerMasterCard ha = new TransactionHandlerMasterCard();
		ha.emvWithPin();
	}

}
