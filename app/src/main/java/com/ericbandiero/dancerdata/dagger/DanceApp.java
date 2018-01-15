package com.ericbandiero.dancerdata.dagger;

import android.app.Application;
import android.util.Log;

import com.ericbandiero.dancerdata.code.AppConstant;

/**
 * Standard Dagger object
 * Created by Eric Bandiero on 11/1/2017.
 */

public class DanceApp extends Application {
	private static DanceApp app;
	private BasicComponent basicComponent;


	@Override
	public void onCreate() {
		super.onCreate();
		if (AppConstant.DEBUG) Log.d(new Object() { }.getClass().getEnclosingClass()+">","App created!");
		app = this;

		basicComponent = DaggerBasicComponent.builder()
				.appModule(new AppModule(getApplicationContext()))
				.build();
	}

	public static DanceApp app() {
		return app;
	}

	public BasicComponent basicComponent() {
		return basicComponent;
	}
}
