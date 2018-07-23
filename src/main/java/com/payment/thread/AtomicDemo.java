

package com.payment.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class AtomicDemo {

	static AtomicReference<String> ar = new AtomicReference<String>("ref");
	static ExecutorService executors =Executors.newCachedThreadPool(); 

	public static void main(String... args) {

		AtomicBoolean bol = new AtomicBoolean();
		System.out.println(bol.get());

		if (bol.compareAndSet(false, true)) {
			System.out.println(bol.get());
		}

		bol.set(false);
		System.out.println(bol.get());

		Runnable task = () -> {

			ar.compareAndSet("ref", "ref1");
			System.out.println(Thread.currentThread().getName() + " " + ar.get());

			ar.set("reference");
			System.out.println(Thread.currentThread().getName() + " " + ar.get());

		};
		
		executors.submit(task);
		executors.submit(task);

	}
}
