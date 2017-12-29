package com.ericbandiero.dancerdata.code;

import android.util.Log;

import com.ericbandiero.dancerdata.activities.PerformanceActivity;
import com.squareup.otto.Subscribe;

/**
 * Created by Eric Bandiero on 10/31/2017.
 */

public class TestBus {
	public TestBus() {
		PerformanceActivity.bus.register(this);
	}

	//This method name doesn't matter - what matters is the argument (String) - which producer sends.
	@Subscribe
	public void getMessage(String s) {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Message received:"+s);
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","We will now stop receiving messages from the event bus...");
		//We will no longer receive these.
		//PerformanceActivity.bus.unregister(this);
	}

}
