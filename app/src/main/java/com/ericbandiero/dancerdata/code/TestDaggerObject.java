package com.ericbandiero.dancerdata.code;

import android.content.Context;
import android.util.Log;

import javax.inject.Inject;

/**
 * Created by Eric Bandiero on 11/1/2017.
 */

public class TestDaggerObject {
	private String name;
	@Inject
	Context c;



	public TestDaggerObject(String name) {
		DanceApp.app().basicComponent().inject(this);
		this.name=name;
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","We got created!");
	}

	public String getName() {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","context:"+c.getPackageName().toString());
		return name;
	}
}