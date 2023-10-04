package com.ericbandiero.dancerdata.code;

import android.content.Context;
import android.util.Log;

import com.ericbandiero.dancerdata.dagger.DanceApp;

import javax.inject.Inject;

/**
 * Created by Eric Bandiero on 11/1/2017.
 */

public class TestDaggerObject {
	private String name;
	@Inject
	Context c;

	@Inject
	DancerDao dancerDao;


	public TestDaggerObject(String name) {
		DanceApp.app().basicComponent().inject(this);
		this.name=name;
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","We got created!");
		//if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","is Dancer dao created:"+dancerDao.getVenueData());
	}

	public String getName() {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","context:"+ c.getPackageName());
		return name;
	}
}
