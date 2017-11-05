package com.ericbandiero.dancerdata.dagger;

import com.ericbandiero.dancerdata.code.StatData;
import com.ericbandiero.dancerdata.code.TestDaggerObject;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Eric Bandiero on 11/5/2017.
 */

@Singleton
@Component(modules = {TestObjectModule.class})
public interface TestObjectComponent {
	TestDaggerObject providesTestObject();
	void inject(StatData stats);
}
