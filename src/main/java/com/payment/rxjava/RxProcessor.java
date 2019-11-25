package com.payment.rxjava;

import io.reactivex.rxjava3.processors.PublishProcessor;

public class RxProcessor {
    public static void main(String... args) {
        PublishProcessor<Integer> processor = PublishProcessor.create();

        processor.retry().subscribe(txt -> {
            System.out.println(txt);
        }, onError -> {
            System.out.println(onError);
        });

        processor.onNext(1);
        processor.onNext(2);
        processor.onNext(3 / 0);
        processor.onNext(4);

    }
}
