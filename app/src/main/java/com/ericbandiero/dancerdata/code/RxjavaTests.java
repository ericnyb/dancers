package com.ericbandiero.dancerdata.code;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Eric Bandiero on 11/19/2017.
 */

public class RxjavaTests {

	public static void runner() {
		List<String> words = Arrays.asList(
				"the",
				"quick",
				"brown",
				"fox",
				"jumped",
				"over",
				"the",
				"lazy",
				"dog"
		);

		Observable.just(words)
				.subscribe(System.out::println);



	}
}
