

package com.payment.security;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;


public class Client {

	public static void main(String... args) throws Exception {

		char[] passwd = "password".toCharArray();

		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream("C:/upendra/keystore.jks"), passwd);

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ks);

		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(null, tmf.getTrustManagers(), null);

		SSLSocketFactory sslFact = ctx.getSocketFactory();
		SSLSocket s = (SSLSocket) sslFact.createSocket("localhost", 8080);

		InputStream in = s.getInputStream();
		DataInputStream dis = new DataInputStream(in);

		int secretNumber = dis.readInt();
		System.out.println(secretNumber);

	}
}
