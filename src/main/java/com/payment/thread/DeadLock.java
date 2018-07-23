

package com.payment.thread;

import java.util.concurrent.*;


public class DeadLock {

	Object lock1 = new Object();
	Object lock2 = new Object();

	static ExecutorService executor = Executors.newCachedThreadPool();

	public void put() {
		synchronized (lock1) {
			try {
				System.out.println("lock1 acquired");
				Thread.sleep(5000);
			}
			catch (InterruptedException e) {

			}
			System.out.println("waiting for lock2");
			synchronized (lock2) {
				System.out.println("lock2 acquired");
			}

		}

	}

	public void get() {
		synchronized (lock2) {
			try {
				System.out.println("lock2 acquired");
				Thread.sleep(6000);
			}
			catch (InterruptedException e) {

			}
			System.out.println("waiting for lock1");
			synchronized (lock1) {
				System.out.println("lock1 acquired");
			}

		}

	}

	public static void main(String... args) {

		DeadLock deadLock = new DeadLock();
		executor.submit(() -> {
			deadLock.put();
		});
		executor.submit(() -> {
			deadLock.get();
		});
	}

}
