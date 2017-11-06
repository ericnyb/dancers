package com.ericbandiero.dancerdata.dagger;

import android.content.Context;

import com.ericbandiero.dancerdata.code.DancerDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Eric Bandiero on 11/6/2017.
 */
@Module
public class DancerModule {
	@Singleton
	@Provides
	public DancerDao provideDancerDao(Context context ){
		return new DancerDao(context);
	}
}
