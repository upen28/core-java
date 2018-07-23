

package com.payment.jpos;

import java.util.Enumeration;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;
import org.jpos.tlv.TLVList;
import org.jpos.tlv.TLVMsg;


public class TDLMessage {

	public static void main(String... arfgs) {
		byte[] hexBytes = ISOUtil.hex2byte(
				"FFA70881ADDFA709014DFA70A018DFA70B014DFA713209C10F187FD1D4dF88851B2E60195D09FDFA71066FF0F63FF282CDF640847433130302D3031DF68083030303030303031DF2913323031312D31322D32302031313A32323A3437FFAE7030FFAE712CFFAE7228DFAE73144241544D303431314D5431303030303030313835DFAE740532352E3030DFA86D03555344FFA70C0CDFA71408C3C02295");
		TLVList tlvList = new TLVList();
		try {
			tlvList.unpack(hexBytes);
		}
		catch (ISOException e) {
			e.printStackTrace();
		}

		Enumeration<TLVMsg> enu = tlvList.elements();

		if (enu.hasMoreElements()) {
			System.err.println(enu.nextElement());
		}
	}

}
