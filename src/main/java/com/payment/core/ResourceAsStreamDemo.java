package com.payment.core;

import java.io.IOException;
import java.io.InputStream;

//path that start with '/' is relative and that does no start with '/' is absolute
public class ResourceAsStreamDemo {

	public static void main(String... args) {
		InputStream stream = null;
		try {
			stream = ResourceAsStreamDemo.class.getResourceAsStream("/keystore.jks");
			if (stream != null) {
				System.out.println("resource loaded successfully");
			} else {
				System.out.println("resource not loaded successfully");
			}
		} finally {
			if (stream != null) {
				try {
					stream.close();
					System.out.println("stream closed successfully");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
