
package com.payment.netty;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;

public class PostPackagerRND {

	public static ISOMsg test0100Req() throws Exception {
		ISOMsg msg = new ISOMsg();
		GenericPackager packager = new GenericPackager("jar:postpack.xml");
		msg.setPackager(packager);

		msg.set(0, "0100"); // Mandatory
		msg.set(2, "4848500000000008008");// Conditional
		msg.set(3, "000000");// Mandatory
		msg.set(4, "9447");// Mandatory
		msg.set(7, "0609114338");// Mandatory
		msg.set(11, "323058");// Mandatory
		msg.set(12, "134338");// Mandatory
		msg.set(13, "0609");// Mandatory
		msg.set(14, "2203");// Conditional
		msg.set(15, "0609");// Conditional
		msg.set(18, "7299");// Conditional
		msg.set(22, "051");// Mandatory
		msg.set(23, "000");// Conditional
		msg.set(25, "00");// Mandatory
		msg.set(26, "12");// Conditional
		msg.set(32, "100");// Conditional
		msg.set(35, "4848500000000008008=22030000000000000");// Conditional
		msg.set(37, "000051876275");// Optional
		msg.set(40, "226");// Optional
		msg.set(41, "00000004");// Mandatory
		msg.set(42, "000008300026708");// Mandatory
		msg.set(43, "WOERMANN OLIMPIA 60 - PWINDHOEK     NANA");// Mandatory
		msg.set(49, "516");// Mandatory
		msg.set(59, "17520.1245");// Optional
		msg.set(100, "100");// Conditional
		msg.set(123, "51010151133C101");// Mandatory

		ISOMsg _127Msg = new ISOMsg(127);
		msg.set(_127Msg);
		_127Msg.set(22, "[TenderDetailGUID]=[XXXXXXXX] [Postilion:MetaData]=[XXXXXXXX]");// Optional

		ISOMsg _1271_25Msg = new ISOMsg(25);// Conditional
		_127Msg.set(_1271_25Msg);

		_1271_25Msg.set(2, "000000009447");
		_1271_25Msg.set(3, "000000000000");
		_1271_25Msg.set(4, "A0000000031010");
		_1271_25Msg.set(5, "3C00");
		_1271_25Msg.set(6, "0180");
		_1271_25Msg.set(7, "FF80");
		_1271_25Msg.set(12, "A6C2DC2A5A1B5E15");
		_1271_25Msg.set(13, "80");
		_1271_25Msg.set(14, "000000000000000042014403410342035E031F02");
		_1271_25Msg.set(15, "440302");
		_1271_25Msg.set(16, "02867624");
		_1271_25Msg.set(17, "0B8683C9800");
		_1271_25Msg.set(18, "06010A03A42002");
		_1271_25Msg.set(20, "0096");
		_1271_25Msg.set(21, "E0F0C8");
		_1271_25Msg.set(22, "516");
		_1271_25Msg.set(23, "22");
		_1271_25Msg.set(24, "0080008000");
		_1271_25Msg.set(26, "516");
		_1271_25Msg.set(27, "200609");
		_1271_25Msg.set(29, "00");
		_1271_25Msg.set(30, "C5240879");

		msg.dump(System.out, "");
		System.out.println(ISOUtil.hexdump(msg.pack()));
		return msg;
	}

	public static ISOMsg test0100Res() throws Exception {
		ISOMsg msg = new ISOMsg();
		GenericPackager packager = new GenericPackager("jar:postpack.xml");
		msg.setPackager(packager);

		msg.set(0, "0110");
		msg.set(2, "4848500000000008008");
		msg.set(3, "000000");
		msg.set(4, "000000009447");
		msg.set(7, "0609114338");
		msg.set(11, "323058");
		msg.set(12, "134338");
		msg.set(13, "0609");
		msg.set(14, "2203");
		msg.set(15, "0609");
		msg.set(18, "7299");
		msg.set(22, "051");
		msg.set(23, "000");
		msg.set(25, "00");
		msg.set(32, "100");
		msg.set(35, "4848500000000008008=22030000000000000");
		msg.set(37, "000051876275");
		msg.set(38, "409924");
		msg.set(39, "00");
		msg.set(40, "226");
		msg.set(41, "00000004");
		msg.set(42, "000008300026708");
		msg.set(43, "WOERMANN OLIMPIA 60 - PWINDHOEK     NANA");
		msg.set(49, "516");
		msg.set(54, "0053516D000000009447");

		msg.set(59, "17520.1245");
		msg.set(100, "100");
		msg.set(123, "51010151133C101");

		ISOMsg _127Msg = new ISOMsg(127);
		msg.set(_127Msg);
		_127Msg.set(3, "NaInnvIPSrc0NaInnvPCSnk0323058323058DefaultTG");
		_127Msg.set(6, "11");
		_127Msg.set(20, "20200609");
		_127Msg.set(22,
				"[RspRoutingInformation]=[XXXXXXXX] [Postilion:MetaData]=[XXXXXXXX] [Base24Data]=[XXXXXXXX] [TenderDetailGUID]=[XXXXXXXX] [Base24Bankserv:ReceivingInstitution]=[XXXXXXXX]");

		ISOMsg _1271_25Msg = new ISOMsg(25);
		_127Msg.set(_1271_25Msg);
		_1271_25Msg.set(31, "8323708DAB52AF473030");

		msg.dump(System.out, "");
		System.out.println(ISOUtil.hexdump(msg.pack()));
		return msg;
	}

	public static ISOMsg test0220Req() throws Exception {
		ISOMsg msg = new ISOMsg();
		GenericPackager packager = new GenericPackager("jar:postpack.xml");
		msg.setPackager(packager);

		msg.set(0, "0220");// Mandatory
		msg.set(2, "4848500000000008008");// Conditional
		msg.set(3, "000000");// Mandatory
		msg.set(4, "000000009447");// Mandatory
		msg.set(7, "0609114355");// Mandatory
		msg.set(11, "323058");// Mandatory
		msg.set(12, "134338");// Mandatory
		msg.set(13, "0609");// Mandatory
		msg.set(14, "2203");// Conditional
		msg.set(15, "0609");// Conditional
		msg.set(18, "7299");// Conditional
		msg.set(22, "051");// Mandatory
		msg.set(23, "000");// Conditional
		msg.set(25, "00");// Mandatory
		msg.set(26, "12");// Conditional
		msg.set(32, "100");// Conditional
		msg.set(35, "4848500000000008008=22030000000000000");// Conditional
		msg.set(37, "000051876275");// Conditional
		msg.set(38, "409924");// Optional
		msg.set(39, "00");// Optional
		msg.set(40, "226");// Optional
		msg.set(41, "00000004");// Mandatory
		msg.set(42, "000008300026708");// Mandatory
		msg.set(43, "WOERMANN OLIMPIA 60 - PWINDHOEK     NANA");// Mandatory
		msg.set(49, "516");// Mandatory
		msg.set(54, "0053516D000000009447");// Conditional
		msg.set(59, "17520.1245");// Optional
		msg.set(100, "100");// Conditional
		msg.set(123, "51010151133C101");// Mandatory

		ISOMsg _127Msg = new ISOMsg(127);
		msg.set(_127Msg);
		_127Msg.set(22, "[TenderDetailGUID]=[XXXXXXXX] [Postilion:MetaData]=[XXXXXXXX]");// Optional

		ISOMsg _1271_25Msg = new ISOMsg(25);// Conditional
		_127Msg.set(_1271_25Msg);

		_1271_25Msg.set(2, "000000009447");
		_1271_25Msg.set(3, "000000000000");
		_1271_25Msg.set(4, "A0000000031010");
		_1271_25Msg.set(5, "3C00");
		_1271_25Msg.set(6, "0180");
		_1271_25Msg.set(7, "FF80");
		_1271_25Msg.set(8, "00");
		_1271_25Msg.set(12, "C35DDAD8005429F2");
		_1271_25Msg.set(13, "40");
		_1271_25Msg.set(14, "000000000000000042014403410342035E031F02");
		_1271_25Msg.set(15, "440302");
		_1271_25Msg.set(16, "02867624");
		_1271_25Msg.set(17, "0B8683C9800");
		_1271_25Msg.set(18, "06010A03A42002");
		_1271_25Msg.set(20, "0096");
		_1271_25Msg.set(21, "E0F0C8");
		_1271_25Msg.set(22, "516");
		_1271_25Msg.set(23, "22");
		_1271_25Msg.set(24, "0080008000");
		_1271_25Msg.set(26, "516");
		_1271_25Msg.set(27, "200609");
		_1271_25Msg.set(29, "00");
		_1271_25Msg.set(30, "C5240879");
		_1271_25Msg.set(31, "8323708DAB52AF473030");

		msg.dump(System.out, "");
		System.out.println(ISOUtil.hexdump(msg.pack()));
		return msg;

	}

	public static ISOMsg test0220Res() throws Exception {
		ISOMsg msg = new ISOMsg();
		GenericPackager packager = new GenericPackager("jar:postpack.xml");
		msg.setPackager(packager);

		msg.set(0, "0230");
		msg.set(2, "4848500000000008008");
		msg.set(3, "000000");
		msg.set(4, "000000009447");
		msg.set(7, "0609114355");
		msg.set(11, "323058");
		msg.set(12, "134338");
		msg.set(13, "0609");
		msg.set(14, "2203");
		msg.set(17, "0609");
		msg.set(18, "5411");
		msg.set(22, "051");
		msg.set(23, "000");
		msg.set(25, "00");
		msg.set(28, "C00000000");
		msg.set(30, "C00000000");
		msg.set(32, "10031784");
		msg.set(35, "4848500000000008008=22030000000000000");
		msg.set(37, "000051876275");
		msg.set(38, "409924");
		msg.set(39, "00");
		msg.set(40, "226");
		msg.set(41, "00000004");
		msg.set(42, "000008300026708");
		msg.set(43, "WOERMANN OLIMPIA 60 - PWINDHOEK     NANA");
		msg.set(49, "516");
		msg.set(54, "0053516D000000009447");
		msg.set(90, "010032305806091143380001003178400000000000");
		msg.set(95, "000000009447000000009447C00000000C00000000");

		msg.set(100, "10031784");

		ISOMsg _130Msg = new ISOMsg(130);
		msg.set(_130Msg);

		msg.dump(System.out, "");
		System.out.println(ISOUtil.hexdump(msg.pack()));
		return msg;

	}

	public static ISOMsg test0200Req() throws Exception {
		ISOMsg msg = new ISOMsg();
		GenericPackager packager = new GenericPackager("jar:postpack.xml");
		msg.setPackager(packager);

		msg.set(0, "0200");
		msg.set(2, "4174600000000007733");
		msg.set(3, "002000");
		msg.set(4, "000000011499");
		msg.set(7, "0609114338");
		msg.set(11, "961773");
		msg.set(12, "134338");
		msg.set(13, "0609");
		msg.set(14, "2404");
		msg.set(15, "0609");
		msg.set(18, "7299");
		msg.set(22, "051");
		msg.set(23, "000");
		msg.set(25, "00");
		msg.set(26, "12");
		msg.set(32, "100");
		msg.set(35, "4174600000000007733=24040000000000000");
		msg.set(37, "000051877470");
		msg.set(40, "220");
		msg.set(41, "00000004");
		msg.set(42, "000008300025569");
		msg.set(43, "WOERMANN OLIMPIA 60 - PWINDHOEK     NANA");
		msg.set(49, "516");
		msg.set(59, "17520.1246");
		msg.set(100, "100");
		msg.set(123, "51010151133C101");

		ISOMsg _127Msg = new ISOMsg(127);
		msg.set(_127Msg);
		_127Msg.set(22, "[TenderDetailGUID]=[XXXXXXXX] [Postilion:MetaData]=[XXXXXXXX]");

		ISOMsg _1271_25Msg = new ISOMsg(25);
		_127Msg.set(_1271_25Msg);

		_1271_25Msg.set(2, "000000011499");
		_1271_25Msg.set(3, "000000000000");
		_1271_25Msg.set(4, "A0000000031010");
		_1271_25Msg.set(5, "3C00");
		_1271_25Msg.set(6, "0180");
		_1271_25Msg.set(7, "FF80");
		_1271_25Msg.set(12, "B7948A78DA1E0C70");
		_1271_25Msg.set(13, "80");
		_1271_25Msg.set(14, "000000000000000002054403410342035E031F02");
		_1271_25Msg.set(15, "440302");
		_1271_25Msg.set(16, "03141271");
		_1271_25Msg.set(17, "0B8683C9800");
		_1271_25Msg.set(18, "06010A03A42002");
		_1271_25Msg.set(20, "009A");
		_1271_25Msg.set(21, "E0F0C8");
		_1271_25Msg.set(22, "516");
		_1271_25Msg.set(23, "22");
		_1271_25Msg.set(24, "0080008000");
		_1271_25Msg.set(26, "516");
		_1271_25Msg.set(27, "200609");
		_1271_25Msg.set(29, "00");
		_1271_25Msg.set(30, "61A01C64");

		msg.dump(System.out, "");
		System.out.println(ISOUtil.hexdump(msg.pack()));
		return msg;

	}

	public static ISOMsg test0200Res() throws Exception {
		ISOMsg msg = new ISOMsg();
		GenericPackager packager = new GenericPackager("jar:postpack.xml");
		msg.setPackager(packager);

		msg.set(0, "0210");
		msg.set(2, "4174600000000007733");
		msg.set(3, "001000");
		msg.set(4, "000000011499");
		msg.set(7, "0609114338");
		msg.set(11, "961773");
		msg.set(12, "134338");
		msg.set(13, "0609");
		msg.set(14, "2404");
		msg.set(15, "0609");
		msg.set(18, "5411");
		msg.set(22, "051");
		msg.set(23, "000");
		msg.set(25, "00");
		msg.set(28, "C00000000");
		msg.set(30, "C00000000");
		msg.set(32, "100");
		msg.set(35, "4174600000000007733=24040000000000000");
		msg.set(37, "000051877470");
		msg.set(38, "355748");
		msg.set(39, "00");
		msg.set(40, "220");
		msg.set(41, "00000004");
		msg.set(42, "000008300025569");
		msg.set(43, "WOERMANN OLIMPIA 60 - PWINDHOEK     NANA");
		msg.set(49, "516");
		msg.set(54, "0002516C0000000358470001516C0000000608911053516D000000011499");

		msg.set(59, "17520.1246");
		msg.set(100, "100");
		msg.set(102, "12991568008");
		msg.set(123, "51010151133C101");

		ISOMsg _127Msg = new ISOMsg(127);
		msg.set(_127Msg);
		_127Msg.set(3, "NaInnvIPSrc0NaInnvPCSnk0323058323058DefaultTG");
		_127Msg.set(6, "11");
		_127Msg.set(20, "20200609");
		_127Msg.set(22,
				"[RspRoutingInformation]=[XXXXXXXX] [TempProcCode]=[XXXXXXXX] [Postilion:MetaData]=[XXXXXXXX] [&&REMOVE]=[XXXXXXXX] [Nedbank::Server]=[XXXXXXXX]");// missing

		ISOMsg _1271_25Msg = new ISOMsg(25);
		_127Msg.set(_1271_25Msg);
		_1271_25Msg.set(6, "0009");
		_1271_25Msg.set(31, "D64327682CA867613030");

		msg.dump(System.out, "");
		System.out.println(ISOUtil.hexdump(msg.pack()));
		return msg;

	}

	public static ISOMsg test0420Req() throws Exception {
		// 3 Processing code Mandatory
		// 4 Amount, transaction Mandatory
		// 11 System trace audit number Mandatory
		// 12 Time, local transaction Mandatory
		// 13 Date, local transaction Mandatory
		// 22 POS entry mode Mandatory
		// 25 POS condition code Mandatory
		// 41 Card acceptor terminal ID Mandatory
		// 42 Card acceptor ID code Mandatory
		// 43 Card acceptor name location Mandatory
		// 90 Original data elements Mandatory
		// 123 POS data code Mandatory
		return null;
	}

	public static void main(String... args) throws Exception {
		test0100Req();
	}
}
