
package com.payment.datastructure;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueueDemo<E> {

    private int takeIndex;

    private int putIndex;

    private Object[] elements;

    private int count;

    Lock lock;
    Condition notFull;
    Condition notEmpty;
    static ExecutorService executor;

    BlockingQueueDemo(int capacity) {
        elements = new Object[capacity];
        takeIndex = 0;
        putIndex = 0;
        lock = new ReentrantLock();
        notFull = lock.newCondition();
        notEmpty = lock.newCondition();
        executor = Executors.newFixedThreadPool(2);

    }

    private void enqueue(E e) {
        final Object[] items = this.elements;
        items[putIndex] = e;
        if (++putIndex == elements.length)
            putIndex = 0;
        count++;
        notEmpty.signalAll();
    }

    @SuppressWarnings("unchecked")
    private E dequeue() {
        final Object[] items = this.elements;
        E e = (E) items[takeIndex];
        items[takeIndex] = null;
        if (++takeIndex == elements.length)
            takeIndex = 0;
        count--;
        notFull.signalAll();
        return e;
    }

    public void put(E e) throws InterruptedException {
        try {
            lock.lock();
            while (elements.length == count)
                notFull.await();
            enqueue(e);
        } finally {
            lock.unlock();
        }

    }

    public E take() throws InterruptedException {
        try {
            lock.lock();
            while (count == 0)
                notEmpty.await();
            return dequeue();

        } finally {
            lock.unlock();
        }
    }

    public static void main(String... args) throws Exception {
        BlockingQueueDemo<Integer> queue = new BlockingQueueDemo<>(100);

        CountDownLatch latch = new CountDownLatch(2);

        Runnable runnable1 = () -> {
            try {
                latch.await();

                while (true) {
                    System.out.println("put");
                    for (int i = 0; i < 200; i++) {
                        queue.put(i);
                        System.out.print("P=" + i + " ,");
                    }
                    System.out.println();
                }
            } catch (Exception e) {

            }
        };

        Runnable runnable2 = () -> {
            try {
                latch.await();

                while (true) {
                    System.out.println("take");
                    for (int i = 0; i < 200; i++) {
                        Integer value = queue.take();
                        Thread.sleep(20);
                        System.out.print("T=" + value + " ,");
                    }
                    System.out.println();
                }
            } catch (Exception e) {

            }
        };

        executor.submit(runnable1);
        executor.submit(runnable2);
        latch.countDown();
        latch.countDown();

    }

}
