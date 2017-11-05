package com.ericbandiero.dancerdata.dagger;

import com.ericbandiero.dancerdata.code.TestDaggerObject;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Eric Bandiero on 11/5/2017.
 */
@Module
public class TestObjectModule {
	@Singleton
	@Provides
	public TestDaggerObject provideDaggerObject(){
		return new TestDaggerObject("Eric");
	}
}
