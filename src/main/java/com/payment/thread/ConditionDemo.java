
package com.payment.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionDemo {
	Lock lock = new ReentrantLock();
	Condition awake = lock.newCondition();
	ExecutorService executor = Executors.newFixedThreadPool(2);
	boolean response = false;

	public void testCondition() {
		Runnable runnable = () -> {
			try {
				lock.lock();
				System.out.println("Awaiting.......");
				while (response != true)
					awake.await();
			} catch (InterruptedException e) {
			} finally {
				lock.unlock();
			}
		};

		Runnable responseRunnable = () -> {
			try {
				lock.lock();
				response = true;
				awake.signalAll();
			} finally {
				lock.unlock();
			}
		};

		executor.submit(runnable);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {

		}
		executor.submit(responseRunnable);
		executor.shutdown();
	}

	public static void main(String[] args) {
		ConditionDemo con = new ConditionDemo();
		con.testCondition();
	}
}
