
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
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.util.LogSource;
import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;

public class JpsoRnd {

    public static String getCurrecy(String currencyCode) throws Exception {
        return ISOCurrency.getCurrency(currencyCode).getAlphaCode();
    }

    public static Double getAmount(String amountStr, String currencyCode) throws Exception {
        Currency currency = ISOCurrency.getCurrency(currencyCode);
        int dec = currency.getDecimals();
        BigDecimal test = new BigDecimal(amountStr).movePointLeft(dec);
        return test.doubleValue();
    }

    public static void testISOAmount() throws Exception {
        ISOAmount iSOAmount = new ISOAmount(4, 710, new BigDecimal("000000004000"));
        System.out.println(iSOAmount.getScale());
        System.out.println(iSOAmount.getAmount());
        System.out.println(iSOAmount.getAmountAsLegacyString());
        System.out.println(iSOAmount.getAmountAsString());
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

    public static void testPakagerOAR() throws Exception {
        ISOMsg msg = new ISOMsg();
        Logger logger = new Logger();
        logger.addListener(new SimpleLogListener(System.out));

        GenericPackager packager = new GenericPackager("jar:post-packager.xml");
        ((LogSource) packager).setLogger(logger, "debug");
        msg.setPackager(packager);

        msg.set(0, "0110");
        msg.set(3, "800000");
        msg.set(7, "0909111216");
        msg.set(11, "227720");
        msg.set(12, "111217");
        msg.set(13, "0909");
        msg.set(15, "0909");
        msg.set(37, "PA0500071217");
        msg.set(39, "00");
        msg.set(41, "0000D473");
        msg.set(59, "0009373963");

        ISOMsg _72Msg = new ISOMsg(72);
        ISOMsg _722Msg = new ISOMsg(2);
        _72Msg.set(_722Msg);
        msg.set(_72Msg);

        ISOMsg _7221Msg = new ISOMsg(1);
        _722Msg.set(_7221Msg);
        _7221Msg.set(3, "4478");
        _7221Msg.set(4, "SP");
        _7221Msg.set(5, "0000");
        _7221Msg.set(6, "000");
        _7221Msg.set(7, "SBC");
        _7221Msg.set(9, new byte[] { 0, 128 - 255 });
        _7221Msg.set(10, "C00000120863");
        _7221Msg.set(15, "240098064");
        _7221Msg.set(21, "PRIMARY");

        ISOMsg _7222Msg = new ISOMsg(2);
        _722Msg.set(_7222Msg);
        _7222Msg.set(3, "9257");
        _7222Msg.set(4, "SP");
        _7222Msg.set(5, "0000");
        _7222Msg.set(6, "000");
        _7222Msg.set(7, "ODS");
        _7222Msg.set(9, new byte[] { 0, 128 - 255 });
        _7222Msg.set(10, "C00000107408");
        _7222Msg.set(15, "428091105");

        ISOMsg _7223Msg = new ISOMsg(3);
        _722Msg.set(_7223Msg);
        _7223Msg.set(3, "1520");
        _7223Msg.set(4, "CR");
        _7223Msg.set(5, "0002");
        _7223Msg.set(6, "000");
        _7223Msg.set(7, "GLV");
        _7223Msg.set(9, new byte[] { 0, 8 });
        _7223Msg.set(10, "C00000000000");

        ISOMsg _7224Msg = new ISOMsg(4);
        _722Msg.set(_7224Msg);
        _7224Msg.set(3, "9260");
        _7224Msg.set(4, "SP");
        _7224Msg.set(5, "0000");
        _7224Msg.set(6, "000");
        _7224Msg.set(7, "AC2");
        _7224Msg.set(9, new byte[] { 0, 128 - 255 });
        _7224Msg.set(10, "C00000010565");
        _7224Msg.set(15, "003338185");
        _7224Msg.set(21, "PRIMARY");

        _72Msg.set(14, "00500000");
        _72Msg.set(15, "15500000");
        _72Msg.set(16, "100000000");
        _72Msg.set(21, "500000");
        _72Msg.set(22, "15500000");
        _72Msg.set(23, "100000000");

        _72Msg.set(40, "AVENTURA");
        _72Msg.set(41, "A");
        _72Msg.set(42, "E");

        _72Msg.set(51, new byte[] { 0, 0 });

        ISOMsg _7254Msg = new ISOMsg(54);
        _72Msg.set(_7254Msg);

        ISOMsg _72541Msg = new ISOMsg(1);
        _7254Msg.set(_72541Msg);
        _72541Msg.set(13, "000064");
        _72541Msg.set(14, "009833");
        _72541Msg.set(15, "012652");
        _72541Msg.set(16, "002525");
        _72541Msg.set(17, "0");
        _72541Msg.set(18, "1");
        _72541Msg.set(19, "2");
        _72541Msg.set(23, "3");
        _72541Msg.set(43, "4");
        _72541Msg.set(46, "001864");

        ISOMsg _72542Msg = new ISOMsg(2);
        _7254Msg.set(_72542Msg);
        _72542Msg.set(13, "000000");
        _72542Msg.set(14, "000000");
        _72542Msg.set(15, "000000");
        _72542Msg.set(16, "000000");
        _72542Msg.set(17, "0");
        _72542Msg.set(18, "1");
        _72542Msg.set(19, "2");
        _72542Msg.set(23, "3");
        _72542Msg.set(43, "4");
        _72542Msg.set(44, "5");
        _72542Msg.set(46, "000000");
        _72542Msg.set(47, "000000");

        _72Msg.set(56, "20190909");
        _72Msg.set(57, "111213");
        _72Msg.set(64, "0000000000000000");

        byte[] dst = msg.pack();

        System.out.println(ISOUtil.hexdump(dst));
        msg.dump(System.out, "");

        ISOMsg ms = new ISOMsg();
        ms.setPackager(packager);
        ms.unpack(dst);

        String account = ms.getString("72.40");
        System.out.println(account);

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
        // getAmount("0000000004000020", "710");
        System.out.println(getCurrecy("710"));
    }
}
