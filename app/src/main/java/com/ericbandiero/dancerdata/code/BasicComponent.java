package com.ericbandiero.dancerdata.code;

import com.ericbandiero.dancerdata.activities.AndroidDataActivity;
import com.ericbandiero.dancerdata.activities.DancedAtVenue;
import com.ericbandiero.dancerdata.activities.DetailActivity;
import com.ericbandiero.dancerdata.activities.PerfActivity;
import com.ericbandiero.dancerdata.activities.PredictActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Eric Bandiero on 11/1/2017.
 */

@Singleton
@Component(modules = {AppModule.class})
public interface BasicComponent {
	void inject(AndroidDataActivity activity);
	void inject(PredictActivity activity);
	void inject(DetailActivity activity);
	void inject(PerfActivity activity);
	void inject(DancedAtVenue activity);

	void inject(TestDaggerObject td);
}
