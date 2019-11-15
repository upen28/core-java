
package com.payment.thread;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

class LockInterruptibly {
    static ReentrantLock lock = new ReentrantLock();
    private static ExecutorService executor = Executors.newFixedThreadPool(2);

    LockInterruptibly() {
        lock.lock();
    }

    public void doSomeWork() throws InterruptedException {
        try {
            lock.lockInterruptibly();
            System.out.println("Lock Acquired...");
            Thread.sleep(10000);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public static void main(String[] args) throws Exception {
        LockInterruptibly demo = new LockInterruptibly();
        executor.submit(() -> {
            try {
                demo.doSomeWork();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        Thread.sleep(5000);
        executor.shutdownNow();
    }
}
