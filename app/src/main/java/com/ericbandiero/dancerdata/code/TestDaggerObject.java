package com.ericbandiero.dancerdata.code;

import android.util.Log;

/**
 * Created by Eric Bandiero on 11/1/2017.
 */

public class TestDaggerObject {
	private String name;

	public TestDaggerObject(String name) {
		this.name=name;
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","We got created!");
	}

	public String getName() {
		return name;
	}
}
