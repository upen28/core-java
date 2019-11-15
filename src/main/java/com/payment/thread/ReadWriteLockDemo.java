
package com.payment.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockDemo {

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executor = Executors.newCachedThreadPool();

        Map<String, String> map = new HashMap<>();

        ReadWriteLock lock = new ReentrantReadWriteLock();

        executor.submit(() -> {
            try {
                lock.writeLock().lock();
                System.out.println("Accquiring write lock by " + Thread.currentThread().getName());
                Thread.sleep(15000);
                map.put("upendra", "verma");
                System.out.println("write susccessfully");

            } catch (Exception e) {

            } finally {
                lock.writeLock().unlock();
            }

        });

        Runnable reader1 = () -> {
            try {
                lock.readLock().lock();
                System.out.println("Accquiring read lock by " + Thread.currentThread().getName());
                Thread.sleep(5000);
                System.out.println(map.get("upendra"));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.readLock().unlock();
            }
        };

        Runnable reader2 = () -> {

            try {
                lock.readLock().lock();
                System.out.println("Accquiring read lock by " + Thread.currentThread().getName());
                Thread.sleep(5000);
                System.out.println(map.get("upendra"));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.readLock().unlock();
            }
        };

        executor.submit(reader1);
        executor.submit(reader2);

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);

    }

}
