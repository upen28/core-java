

package com.payment.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.StampedLock;


public class StampedThreadDemo {

	public static class Counter {
		private int counter = 0;

		public int getNextCounter() {
			return ++counter;
		}

		public int getCounter() {
			return counter;
		}

	}

	public static void main(String... args) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		StampedLock stampLock = new StampedLock();
		Counter cnter = new Counter();
		Runnable writerTask = () -> {
			long writeStamp = stampLock.writeLock();
			try {
				try {
					System.out.println("writer in");
					Thread.sleep(20000);
					System.out.println(cnter.getNextCounter());
					System.out.println("writer out");
				}
				catch (Exception e) {

				}
			}
			finally {
				stampLock.unlock(writeStamp);
			}
		};

		Runnable readerTask = () -> {
			long writeStamp = stampLock.readLock();
			try {
				try {
					System.out.println("reader in " + Thread.currentThread().getName());
					System.out.println(cnter.getCounter());
					Thread.sleep(2 * 1000);
					System.out.println("reader out");
				}
				catch (Exception e) {

				}
			}
			finally {
				stampLock.unlock(writeStamp);
			}
		};
		Runnable optimisticReaderTask = () -> {
			long stamp = stampLock.tryOptimisticRead();
			try {
				try {
					boolean val = stampLock.validate(stamp);
					System.out.println("validate " + val);
					System.out.println(cnter.getCounter());
					Thread.sleep(1000);
					val = stampLock.validate(stamp);
					while (!val) {
						System.out.println("read again");
						System.out.println(cnter.getCounter());
						Thread.sleep(5000);
						stamp = stampLock.tryOptimisticRead();
						val = stampLock.validate(stamp);
						System.out.println("while validate " + val);
					}
					System.out.println("optimistic reader out");
				}
				catch (Exception e) {

				}
			}
			finally {
				stampLock.unlock(stamp);
			}
		};

		/*
		 * executor.submit(readerTask); executor.submit(readerTask);
		 * executor.submit(readerTask); executor.submit(readerTask);
		 */
		executor.submit(optimisticReaderTask);
		Thread.sleep(100);
		executor.submit(writerTask);

	}

}
