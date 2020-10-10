
package com.payment.thread;

class InterruptedDemo {
	public static void main(String[] args) throws Exception {
		System.out.println(Thread.currentThread().interrupted());

		if (args.length > 0) {
			Thread.currentThread().interrupt();
			System.out.println("Interrupted");
		}

		System.out.println(Thread.currentThread().interrupted());

		try {
			Thread.sleep(10000);
			System.out.println("wait for 10 sec");

		} catch (Exception ex) {
			System.out.println("Thread is interrupted");
		}

		Thread.currentThread().interrupt();
		System.out.println("Interrupted");

		System.out.println(Thread.currentThread().interrupted());

		System.out.println(Thread.currentThread().isInterrupted());

		long start = System.currentTimeMillis();

		try {
			Thread.sleep(20000);
		} catch (Exception ex) {
			System.out.println("Thread is interrupted");
		}

		long end = System.currentTimeMillis() - start;

		System.out.println(Thread.currentThread().isInterrupted());
		System.out.println("Total time taken by program " + end);

	}
}
