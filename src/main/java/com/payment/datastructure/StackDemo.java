
package com.payment.datastructure;

import java.util.*;

class Stack<E> {
	private Object[] elements = null;

	private int counter = -1;

	private int defaultSize = 16;

	Stack() {
		elements = new Object[defaultSize];
	}

	public void push(E element) {
		elements[++counter] = element;
	}

	public E pull() {
		return (E) elements[counter--];
	}

}

class Stack2<E> {
	private E[] elements = null;

	private int counter = -1;

	private int defaultSize = 16;

	Stack2() {
		elements = (E[]) new Object[defaultSize];
	}

	public void push(E element) {
		elements[++counter] = element;
	}

	public E pop() {
		return elements[counter--];
	}

	public void pushAll(List<? extends E> put) {
		put.forEach(item -> {
			push(item);
		});

		// put.add(100);

	}

	public void popAll(List<? super E> get) {
		get.add(pop());
		get.add(pop());
	}

}

public class StackDemo {

	public static void main(String... args) {

		Stack2<Number> stack2 = new Stack2<>();

		for (int i = 0; i < 10; i++) {
			stack2.push(i);
		}

		for (int i = 0; i < 10; i++) {
			System.out.println(stack2.pop());
		}

		List<Integer> ls = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			ls.add(i);
		}

		List<Number> ls2 = new ArrayList<>();
		stack2.pushAll(ls);
		stack2.popAll(ls2);
		System.out.println(ls2);

	}

}
