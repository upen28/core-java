package com.payment.rxjava;

import java.util.concurrent.TimeUnit;

import rx.Observable;

public class CombiningOperator {

	public static void testConcat() {
		Observable<String> name = Observable.just("upendra", "sandip", "arvind", "deepankar");
		Observable<String> surname = Observable.just("verma", "patro", "negi", "seth");
		Observable.concat(name, surname).subscribe(s -> System.out.println(s));
	}

	public static void testConcatWith() {
		Observable<Integer> name = Observable.just(1, 2, 3, 4);
		Observable<Integer> surname = Observable.just(5, 6, 7, 8);
		name.concatWith(surname).subscribe(i -> System.out.println(i));
	}

	// execute in the same thread that why it preserve ordering
	public static void testMerge() {
		Observable<String> name = Observable.just("upendra", "sandip", "arvind", "deepankar");
		Observable<String> surname = Observable.just("verma", "patro", "negi", "seth");
		Observable.merge(name, surname).subscribe(s -> System.out.println(s));
	}

	public static void testMergeUsingDiffThread() {
		Observable<String> oneSec = Observable.interval(1, TimeUnit.SECONDS).map(num -> {
			return Thread.currentThread().getName() + "-time elapsed  " + (num + 1) + "secs";
		});

		Observable<String> threeMilli = Observable.interval(300, TimeUnit.MILLISECONDS).map(num -> {
			return Thread.currentThread().getName() + "-time elapsed  " + (num + 1) * 300 + "millsecs";
		});
		Observable.merge(oneSec, threeMilli).subscribe(s -> System.out.println(s));

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}
	}

	public static void main(String... args) {
		testMergeUsingDiffThread();
	}

}
