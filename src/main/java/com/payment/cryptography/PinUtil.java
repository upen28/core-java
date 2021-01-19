package com.payment.cryptography;

import java.security.GeneralSecurityException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

public class PinUtil {

	public static byte[] encodePinBlockInFormat0(String pin, String pan) throws ISOException {
		String hexCnFld = "0";
		String hexPinLength = ISOUtil.byte2hex(new byte[] { (byte) pin.length() }).substring(1, 2);
		String start = hexCnFld + hexPinLength + pin;
		String hexPinData = ISOUtil.padright(start, 16, 'F');

		final String panPart = extractPanAccountNumberPart(pan);
		final String panData = ISOUtil.padleft(panPart, 16, '0');

		byte[] bPin = ISOUtil.hex2byte(hexPinData);
		byte[] bPan = ISOUtil.hex2byte(panData);

		final byte[] pinblock = new byte[8];
		for (int i = 0; i < 8; i++)
			pinblock[i] = (byte) (bPin[i] ^ bPan[i]);

		return pinblock;
	}

	public static String extractPanAccountNumberPart(String accountNumber) {
		String accountNumberPart = null;
		if (accountNumber.length() > 12)
			accountNumberPart = accountNumber.substring(accountNumber.length() - 13, accountNumber.length() - 1);
		else
			accountNumberPart = accountNumber;
		return accountNumberPart;
	}

	public static SecretKey defineKey(byte[] keyBytes) {
		if (keyBytes.length != 16) {
			throw new IllegalArgumentException("keyBytes wrong length for triple Des key");
		}
		return new SecretKeySpec(keyBytes, "DESede");
	}

	public static byte[] cbcDecrypt(SecretKey key, byte[] iv, byte[] cipherText) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		return cipher.doFinal(cipherText);
	}

	public static byte[][] cbcEncrypt(SecretKey key, byte[] data) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }));
		return new byte[][] { cipher.getIV(), cipher.doFinal(data) };
	}

	public static byte[] ecbEncrypt(SecretKey key, byte[] data) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(data);
	}

	public static byte[] ecbDecrypt(SecretKey key, byte[] cipherText) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(cipherText);
	}

	public static void main(String... args) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		if (Security.getProvider("BC") == null) {
			System.out.println("Bouncy Castle provider is NOT available");
		} else {
			System.out.println("Bouncy Castle provider is available");
		}

		byte[] zmkDecp = ISOUtil.hex2byte("13AED5DA1F323475333333C11F2608FD");
		byte[] zpkEnc = ISOUtil.hex2byte("F8FAFB04FFA20FB5D5EACF28121752B0");

		SecretKey zmk = defineKey(zmkDecp);

		byte[] zpkDec = ecbDecrypt(zmk, zpkEnc);
		SecretKey zpk = defineKey(zpkDec);
		System.out.println("zpk " + ISOUtil.byte2hex(zpk.getEncoded()));

		byte[] pinBlock = encodePinBlockInFormat0("5327", "5387565376885327");
		System.out.println("pin block " + ISOUtil.hexString(pinBlock));

		byte[] encResult = ecbEncrypt(zpk, pinBlock);
		System.out.println("enc pin block " + ISOUtil.hexString(encResult));
		
		

		zmk = new SecretKeySpec(ISOUtil.hex2byte("13AED5DA1F323475333333C11F2608FD"), "DESede");
		Cipher desCipher = Cipher.getInstance("DESede/ECB/NoPadding");
		desCipher.init(Cipher.DECRYPT_MODE, zmk);
		String clearZPK = ISOUtil.hexString(desCipher.doFinal(ISOUtil.hex2byte("F8FAFB04FFA20FB5D5EACF28121752B0")));
		System.out.println("Clear ZPK:" + clearZPK);

		// Doing the same thing as we did for 3 part ZMK.
		String clearZPKtripleLength = clearZPK.substring(0, 16) + clearZPK.substring(16, 32)
				+ clearZPK.substring(0, 16);
		// Encrypt the pinblock with the clear ZPK
		zpk = new SecretKeySpec(ISOUtil.hex2byte(clearZPKtripleLength), "DESede");
		desCipher.init(Cipher.ENCRYPT_MODE, zpk);
		String encryptedPinBlock = ISOUtil.hexString(desCipher.doFinal(ISOUtil.hex2byte("0453529AC8977ACD")));
		System.out.println("Encrypted Pin Block :" + encryptedPinBlock);
	}
}
