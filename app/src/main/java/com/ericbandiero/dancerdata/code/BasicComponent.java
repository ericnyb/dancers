package com.ericbandiero.dancerdata.code;

import com.ericbandiero.dancerdata.activities.AndroidDataActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Eric Bandiero on 11/1/2017.
 */

@Singleton
@Component(modules = {AppModule.class})
public interface BasicComponent {
	void inject(AndroidDataActivity activity);
	void inject(TestDaggerObject td);
}
