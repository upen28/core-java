
package com.payment.jpos;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bouncycastle.util.encoders.Base64;
import org.jpos.iso.AsciiPrefixer;
import org.jpos.iso.BinaryPrefixer;
import org.jpos.iso.Currency;
import org.jpos.iso.EbcdicBinaryInterpreter;
import org.jpos.iso.EbcdicHexInterpreter;
import org.jpos.iso.EbcdicInterpreter;
import org.jpos.iso.ISOAmount;
import org.jpos.iso.ISOCurrency;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;

public class JpsoRnd {

	private static Double getAmount(String amountStr, String currencyCode) throws Exception {
		Currency currency = ISOCurrency.getCurrency(currencyCode);
		int dec = currency.getDecimals();
		BigDecimal test = new BigDecimal(amountStr).movePointLeft(dec);
		return test.doubleValue();
	}

	public static void testISOAmount() throws Exception {
		ISOAmount iSOAmount = new ISOAmount(28, 840, new BigDecimal("000000002000"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		iSOAmount.dump(new PrintStream(baos, true), "");
		System.out.println(baos.toString());
		System.out.println(iSOAmount.getValue());
		System.out.println(getAmount("000000002000", "840"));
	}

	public static void testCurrencies() throws Exception {
		List<java.util.Currency> currencies = java.util.Currency.getAvailableCurrencies().stream()
				.sorted(Comparator.comparing(java.util.Currency::getCurrencyCode)).collect(Collectors.toList());

		currencies.forEach(currency -> {
			Currency ccy = null;
			try {
				ccy = new Currency(currency.getCurrencyCode().toUpperCase(),
						Integer.parseInt(ISOUtil.zeropad(Integer.toString(currency.getNumericCode()), 3)),
						currency.getDefaultFractionDigits());
			} catch (NumberFormatException | ISOException e) {
				e.printStackTrace();
			}
			System.out.println(ccy);
		});

	}

	public static void testPakager() throws Exception {
		GenericPackager packager = new GenericPackager("jar:sms-packager.xml");

		ISOMsg msg = new ISOMsg();
		msg.setPackager(packager);

		msg.set(0, "0200");
		ISOMsg inner = new ISOMsg(62);
		inner.set(1, "3");
		inner.set(2, "123456781234567");
		inner.set(22, "222222");

		msg.set(inner);

		byte[] dst = msg.pack();

		System.out.println(ISOUtil.hexdump(dst));
		msg.dump(System.out, "");

	}

	public static void testPakager1() throws Exception {
		GenericPackager packager = new GenericPackager("jar:sms-packager.xml");

		ISOMsg msg = new ISOMsg();
		msg.setPackager(packager);

		msg.set(0, "0800");

		byte[] dst = msg.pack();

		System.out.println(ISOUtil.hexdump(dst));
		msg.dump(System.out, "");

		ISOMsg dstMsg = new ISOMsg();
		dstMsg.setPackager(packager);

		dstMsg.unpack(dst);
		dstMsg.dump(System.out, "");

	}

	public static void testPrefixer() throws ISOException {
		AsciiPrefixer prefixer = AsciiPrefixer.LL;
		byte[] bts = new byte[2];
		prefixer.encodeLength(56, bts);
		System.out.println(ISOUtil.hexdump(bts));

		BinaryPrefixer binaryPrefixer = BinaryPrefixer.B;
		bts = new byte[1];
		binaryPrefixer.encodeLength(56, bts);
		System.out.println(ISOUtil.hexdump(bts));

	}

	public static void testEbcdic() {
		byte[] _secure = Base64.decode("AAABBhBxKAAAAAAAAAAAAAAAAAA=");
		String s = new String(_secure);

		System.out.println(ISOUtil.hexdump(_secure));
		System.out.println(s);

		byte[] dst = new byte[40];
		EbcdicHexInterpreter.INSTANCE.interpret(_secure, dst, 0);
		System.out.println(ISOUtil.hexdump(dst));

		byte[] dst1 = new byte[20];
		EbcdicBinaryInterpreter.INSTANCE.interpret(_secure, dst1, 0);
		System.out.println(ISOUtil.hexdump(dst1));

		byte[] dst2 = new byte[20];
		EbcdicInterpreter.INSTANCE.interpret(s, dst2, 0);
		System.out.println(ISOUtil.hexdump(dst2));

	}

	public static void main(String... args) throws Exception {
		testPakager();
	}
}
