
package com.payment.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamDemo {

	public static void main(String... args) {
		List<String> lsString = new ArrayList<>();
		lsString.add("Upendra");
		lsString.add("Sandeep");
		System.out.println(
				lsString.stream().map(name -> name.split("")).flatMap(Arrays::stream).collect(Collectors.toList()));
	}
}
