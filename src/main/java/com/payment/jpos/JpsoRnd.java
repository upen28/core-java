
package com.payment.jpos;

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
import org.jpos.iso.ISOUtil;

public class JpsoRnd {
	public static String getCurrecy(String currencyCode) throws Exception {
		return ISOCurrency.getCurrency(currencyCode).getAlphaCode();
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

	public static void testISOAmount() throws Exception {
		ISOAmount iSOAmount = new ISOAmount(4);
		iSOAmount.setValue("840200001414");
		System.out.println(iSOAmount.getValue());
		System.out.println(iSOAmount.getScale());
		System.out.println(iSOAmount.getAmount());
		System.out.println(iSOAmount.getAmountAsLegacyString());
		System.out.println(iSOAmount.getAmountAsString());
	}

	public static void testISOAmountInDecimal() throws Exception {
		ISOAmount iSOAmount = new ISOAmount(4, 840, new BigDecimal("14.14"));
		System.out.println(iSOAmount.getScale());
		System.out.println(iSOAmount.getAmount());
		System.out.println(iSOAmount.getAmountAsLegacyString());
		System.out.println(iSOAmount.getAmountAsString());
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
		testISOAmount();
	}
}
