

package com.payment.security;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.util.Random;
import java.util.stream.Stream;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;


public class SSLServer {

	private static Random randomGenerator = new Random();

	public SSLContext getSSLContext() throws Exception {

		char[] passwd = "password".toCharArray();
		char[] pvtPassword = "welcome".toCharArray();
		SSLContext ctx = null;
		try

		{
			ctx = SSLContext.getInstance("TLS");

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream("C:/upendra/keystore.jks"), passwd);
			kmf.init(ks, pvtPassword);

			ctx.init(kmf.getKeyManagers(), null, null);
			return ctx;
		}
		catch (Exception e) {
			System.out.println("Unable to initialize SSLContext " + e.getMessage());
			throw e;
		}
	}

	public static void main(String[] args) throws Exception {
		SSLServer server = new SSLServer();
		SSLContext ctx = server.getSSLContext();

		SSLServerSocketFactory sslSrvFact = ctx.getServerSocketFactory();
		SSLServerSocket ss = null;

		ss = (SSLServerSocket) sslSrvFact.createServerSocket(8081);

		while (true) {
			try {
				SSLSocket client = null;

				System.out.println("waiting for the request");

				client = (SSLSocket) ss.accept();

				int secretNumber = randomGenerator.nextInt();

				System.out.println("Secret Number: " + secretNumber);

				OutputStream out = client.getOutputStream();

				DataOutputStream dos = new DataOutputStream(out);

				// dos.writeInt(secretNumber);

				Stream.iterate(0, x -> x + 1).forEach(no -> {
					try {
						dos.writeInt(no);
						dos.flush();
					}
					catch (Exception e) {
						e.printStackTrace();

					}
				});

				/*
				 * InputStream in = client.getInputStream(); DataInputStream dis
				 * = new DataInputStream(in); System.out.println("Receving *** "
				 * + dis.readInt()); Thread.sleep(15000);
				 */

				// dos.flush();

				/*
				 * dis.close(); in.close();
				 */

				dos.close();

				out.close();

				client.close();

				System.out.println("client channel is closed");

			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}
}
