

package com.payment.thread;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;


class LockDemo {
	static ReentrantLock lock = new ReentrantLock();
	private static ExecutorService executor = Executors.newFixedThreadPool(2);

	public LockDemo() {
		lock.lock();
		System.out.println("Lock Acquired...");
	}

	public void doSomeWork() throws InterruptedException {
		lock.lockInterruptibly();
		try {
			System.out.println("Lock Acquired...");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			lock.unlock();
		}

	}

	public static void main(String[] args) throws Exception {
		LockDemo demo = new LockDemo();

		executor.submit(() -> {
			try {
				demo.doSomeWork();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName());
		});

		Thread.sleep(20000);

		/*if (lock.isLocked()) {
			System.out.println("isLocked() is true");
			try {
			}
			finally {
				lock.unlock();
			}
		}*/
		executor.shutdownNow();
	}
}
