package com.payment.rxjava;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import rx.Observable;

public class NetworkCommand extends HystrixCommand<Long> {

    private static AtomicLong lng = new AtomicLong(0);

    public NetworkCommand() {
        super(HystrixCommandGroupKey.Factory.asKey("tsys-group"));
    }

    @Override
    protected Long run() throws Exception {
        System.out.println("currrent thread " + Thread.currentThread().getName());
        Thread.sleep(200);
        return lng.addAndGet(1);
    }

    @Override
    protected Long getFallback() {
        System.out.println("fallback");
        return lng.addAndGet(1);
    }

    public static void testHappyPath() {
        NetworkCommand netCommand = new NetworkCommand();
        try {
            Long response = netCommand.execute();
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testLoad() {
        Observable.interval(50, TimeUnit.MILLISECONDS).flatMap(number -> {
            return new NetworkCommand().toObservable();
        }).onErrorResumeNext(ex -> {
            System.out.println(ex);
            return Observable.empty();
        }).subscribe(onNext -> {
            System.out.println(onNext);
        });
    }

    public static void main(String... args) throws InterruptedException {
        testLoad();
        Thread.sleep(60000);
    }

}
