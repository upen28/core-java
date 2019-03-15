package com.payment.security.keystore;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

public class KeyStoreDemo {

	public static PublicKey getPublicKey() throws Exception {
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		char[] storePassword = "welcome".toCharArray();
		try (InputStream keyStoreData = new FileInputStream(
				"C:\\projects\\core-java\\src\\main\\resources\\keystore.jks")) {
			keyStore.load(keyStoreData, storePassword);
		}

		X509Certificate certificate = (X509Certificate) keyStore.getCertificate("reno-upen");
		return certificate.getPublicKey();
	}

	public static PrivateKey getPrivateKey() throws Exception {
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		char[] storePassword = "welcome".toCharArray();
		char[] keyStorePassword = "welcome-key".toCharArray();

		try (InputStream keyStoreData = new FileInputStream(
				"C:\\projects\\core-java\\src\\main\\resources\\keystore.jks")) {
			keyStore.load(keyStoreData, storePassword);
		}

		KeyStore.PasswordProtection PasswordProtection = new KeyStore.PasswordProtection(keyStorePassword);
		KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("reno-upen",
				PasswordProtection);
		return privateKeyEntry.getPrivateKey();
	}

	public static void certificateInfoBySunProvider() throws Exception {
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		char[] storePassword = "welcome".toCharArray();
		char[] keyStorePassword = "welcome-key".toCharArray();

		try (InputStream keyStoreData = new FileInputStream(
				"C:\\projects\\core-java\\src\\main\\resources\\keystore.jks")) {
			keyStore.load(keyStoreData, storePassword);
		}

		System.out.println("*************************certificate*************************");
		X509Certificate certificate = (X509Certificate) keyStore.getCertificate("reno-upen");
		System.out.println(certificate);
		System.out.println("**************************************************");

		System.out.println("*************************public key*************************");
		// public key
		System.out.println(certificate.getPublicKey());
		System.out.println("**************************************************");

		System.out.println("*************************signature*************************");
		// signature
		System.out.println(certificate.getSigAlgName());
		System.out.println(certificate.getSigAlgOID());
		System.out.println(certificate.getSignature());
		System.out.println("**************************************************");

		System.out.println("*************************private key*************************");
		// private key
		KeyStore.PasswordProtection PasswordProtection = new KeyStore.PasswordProtection(keyStorePassword);
		KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("reno-upen",
				PasswordProtection);
		System.out.println(privateKeyEntry.getPrivateKey().getFormat());
		System.out.println(privateKeyEntry.getPrivateKey().getEncoded());
		System.out.println("**************************************************");
	}

	public static void main(String... args) throws Exception {
		certificateInfoBySunProvider();
	}
}
