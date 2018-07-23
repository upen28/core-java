

package com.payment.security;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.bouncycastle.util.encoders.Base64;


public class CertificateLoadDemo {
	public static void main(String... args) throws Exception {

		String bse64 = "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";
		char[] passwd = "password".toCharArray();

		File keystoreFile = new File("C:/upendra/keystore.jks");
		FileInputStream in = new FileInputStream(keystoreFile);

		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(in, passwd);
		in.close();

		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		byte[] unencryptedCert = Base64.decode(bse64);
		ByteArrayInputStream bais = new ByteArrayInputStream(unencryptedCert);
		X509Certificate cert = (X509Certificate) certFactory.generateCertificate(bais);

		ks.setCertificateEntry("keymaker_third_party_intermediate_ca", cert);

		FileOutputStream out = new FileOutputStream(keystoreFile);
		ks.store(out, passwd);
		out.close();

	}
}
