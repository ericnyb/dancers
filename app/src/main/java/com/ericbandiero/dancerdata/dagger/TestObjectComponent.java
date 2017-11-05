package com.ericbandiero.dancerdata.dagger;

import com.ericbandiero.dancerdata.code.StatData;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Eric Bandiero on 11/5/2017.
 */

@Singleton
@Component(modules = {TestObjectModule.class})
public interface TestObjectComponent {
	void inject(StatData stats);
}
