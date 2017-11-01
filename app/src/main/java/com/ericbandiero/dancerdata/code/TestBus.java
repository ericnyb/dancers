package com.ericbandiero.dancerdata.code;

import android.util.Log;

import com.ericbandiero.dancerdata.*;
import com.ericbandiero.dancerdata.activities.PerfActivity;
import com.squareup.otto.Subscribe;

/**
 * Created by Eric Bandiero on 10/31/2017.
 */

public class TestBus {
	public TestBus() {
		PerfActivity.bus.register(this);
	}

	@Subscribe
	public void getMessage(String s) {
		if (com.ericbandiero.dancerdata.AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Message received:"+s);
		//We will no longer receive these.
		PerfActivity.bus.unregister(this);
	}

}
