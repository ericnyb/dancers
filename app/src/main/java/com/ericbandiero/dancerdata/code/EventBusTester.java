package com.ericbandiero.dancerdata.code;

/**
 * Created by Eric Bandiero on 10/31/2017.
 */

public class EventBusTester {
	String s="Hello";

	public EventBusTester(String s) {
		this.s = s;
	}

	public String getS() {
		return s;
	}

	@Override
	public String toString() {
		return "EventBusTester{" +
				"s='" + s + '\'' +
				'}';
	}
}
