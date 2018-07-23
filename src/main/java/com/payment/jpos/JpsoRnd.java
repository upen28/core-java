

package com.payment.jpos;

import org.bouncycastle.util.encoders.Base64;
import org.jpos.iso.AsciiPrefixer;
import org.jpos.iso.BinaryPrefixer;
import org.jpos.iso.EbcdicBinaryInterpreter;
import org.jpos.iso.EbcdicHexInterpreter;
import org.jpos.iso.EbcdicInterpreter;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;


public class JpsoRnd {

	public static void testPakager() throws Exception {
		GenericPackager packager = new GenericPackager("jar:worldpay2015binary.xml");

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

	public static void testPakager1() throws Exception {
		GenericPackager packager = new GenericPackager("jar:worldpay2015binary.xml");

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
		System.out.println("starting &&&&&&& main");
		testEbcdic();
	}
}
