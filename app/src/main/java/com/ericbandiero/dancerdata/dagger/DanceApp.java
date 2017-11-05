package com.ericbandiero.dancerdata.dagger;

import android.app.Application;
import android.util.Log;

import com.ericbandiero.dancerdata.code.AppConstant;
import com.ericbandiero.dancerdata.dagger.DaggerBasicComponent;

/**
 * Created by Eric Bandiero on 11/1/2017.
 */

public class DanceApp extends Application {
	private static DanceApp app;
	private BasicComponent basicComponent;
	private TestObjectComponent testObjectComponent;

	@Override
	public void onCreate() {
		super.onCreate();
		if (AppConstant.DEBUG) Log.d(new Object() { }.getClass().getEnclosingClass()+">","App created!");
		app = this;

		basicComponent = DaggerBasicComponent.builder()
				.appModule(new AppModule(getApplicationContext()))
				.build();

		testObjectComponent=DaggerTestObjectComponent.builder().testObjectModule(new TestObjectModule()).build();

	}

	public static DanceApp app() {
		return app;
	}

	public BasicComponent basicComponent() {
		return basicComponent;
	}

	public TestObjectComponent testObjectComponent(){
		return testObjectComponent;
	}
}
