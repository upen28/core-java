
package com.payment.datastructure;

import java.util.stream.Stream;

public class StackResized<E> {

	private Object[] elements;
	private int counter;

	StackResized(int size) {
		elements = new Object[size];
	}

	public void push(E e) {
		if (counter == elements.length) {
			resized();
		}
		elements[counter++] = e;
	}

	@SuppressWarnings("unchecked")
	public E pop() {
		return (E) elements[--counter];
	}

	private void resized() {
		Object[] dest = new Object[this.elements.length * 2];
		System.arraycopy(elements, 0, dest, 0, elements.length);
		elements = dest;
	}

	public static void main(String... args) {
		StackResized<Integer> stack = new StackResized<>(10);

		Stream.iterate(1, n -> n + 1).limit(10).forEach((n) -> {
			stack.push(n);
		});

		Stream.iterate(1, n -> n + 1).limit(10).forEach((n) -> {
			System.out.println(stack.pop());
		});

		Stream.iterate(1, n -> n + 1).limit(40).forEach((n) -> {
			stack.push(n);
		});

		Stream.iterate(1, n -> n + 1).limit(40).forEach((n) -> {
			System.out.println(stack.pop());
		});

	}

}
