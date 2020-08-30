
package com.payment.jpos;

import org.jpos.iso.ISOUtil;

public class BytesDemo {
	public static void main(String[] args) throws Exception {
		tTwoByte();
	}

	public static void tTwoByte() {
		byte[] by = new byte[2];
		by[0] = 05;
		by[1] = 0x5A;

		int twoByte = (by[0] & 0xFF) << 8 | by[1] & 0xFF;
		System.out.println(twoByte);
		printInt(twoByte);
	}

	public static void toTwoByte() {
		int length = 128;
		printInt(length);

		byte[] by = new byte[2];

		by[0] = (byte) (length >> 8);
		by[1] = (byte) (length);

		System.out.println(ISOUtil.hexdump(by));

		int twoByte = (by[0] & 0xFF) << 8 | by[1] & 0xFF;
		printInt(twoByte);
	}

	public static void toFourByte() {
		int length = 255;
		printInt(length);

		byte[] by = new byte[4];

		by[0] = (byte) (length >> 24);
		by[1] = (byte) (length >> 16);
		by[2] = (byte) (length >> 8);
		by[3] = (byte) (length);

		System.out.println(ISOUtil.hexdump(by));

		int fourByte = (by[0] & 0xFF) << 24 | (by[1] & 0xFF) << 16 | (by[2] & 0xFF) << 8 | by[3] & 0xFF;
		printInt(fourByte);
	}

	public static void printByte(byte b) {
		for (int i = 7; i >= 0; i--) {
			int k = 1;
			int masking = k << i;
			if ((b & masking) == masking) {
				System.out.print("1");
			} else {
				System.out.print("0");
			}
		}
		System.out.print("\n");
	}

	public static void printInt(int b) {
		for (int i = 31; i >= 0; i--) {
			int k = 1;
			int masking = k << i;
			if ((b & masking) == masking) {
				System.out.print("1");
			} else {
				System.out.print("0");
			}
			if (i != 32 & i % 8 == 0) {
				System.out.print(" ");
			}
		}
		System.out.print("\n");

	}
}
