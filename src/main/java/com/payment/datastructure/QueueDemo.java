
package com.payment.datastructure;

import java.util.concurrent.*;

public class QueueDemo<E> {

	private int takeIndex;

	private int putIndex;

	private Object[] elements;

	private int count;

	QueueDemo(int capacity) {
		elements = (E[]) new Object[capacity];
		takeIndex = 0;
		putIndex = 0;
	}

	private void enqueue(E e) {
		final Object[] items = this.elements;
		items[putIndex] = e;
		if (++putIndex == items.length)
			putIndex = 0;
		count++;
	}

	private E dequeue() {
		final Object[] items = this.elements;
		E e = (E) items[takeIndex];
		items[takeIndex] = null;
		if (++takeIndex == items.length)
			takeIndex = 0;
		count--;
		return e;
	}

	private boolean offer(E e) {
		if (count == elements.length) {
			return false;
		} else {
			enqueue(e);
			return true;
		}
	}

	private E poll() {
		if (count == 0) {
			return null;
		} else {
			return dequeue();
		}
	}

	public static void main(String... args) {
		QueueDemo<Integer> queue = new QueueDemo(50);

		for (int i = 0; i < 100; i++) {
			boolean status = queue.offer(i);
			if (status) {
				System.out.println(i + " is insert sucssesfully");
			} else {
				System.out.println(i + " is not insert sucssesfully");
			}

		}

		System.out.println("********************************************");

		for (int i = 0; i < 30; i++) {
			Integer value = queue.poll();
			if (value != null) {
				System.out.println("value of " + i + " " + value);
			} else {
				System.out.println(i + " return null");
			}

		}
		System.out.println("********************************************");

		for (int i = 0; i < 30; i++) {
			boolean status = queue.offer(new Integer(i));
			if (status) {
				System.out.println(i + " is insert sucssesfully");
			} else {
				System.out.println(i + " is not insert sucssesfully");
			}

		}
		System.out.println("********************************************");

		for (int i = 0; i < 50; i++) {
			Integer value = queue.poll();
			if (value != null) {
				System.out.println("value of " + i + " " + value);
			} else {
				System.out.println(i + " return null");
			}

		}

	}

}
