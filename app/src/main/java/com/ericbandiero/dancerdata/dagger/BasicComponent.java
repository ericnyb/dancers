package com.ericbandiero.dancerdata.dagger;

import com.ericbandiero.dancerdata.activities.AndroidDataActivity;
import com.ericbandiero.dancerdata.activities.DancedAtVenue;
import com.ericbandiero.dancerdata.activities.DetailActivity;
import com.ericbandiero.dancerdata.activities.HandleListClickForVenueCount;
import com.ericbandiero.dancerdata.activities.PerfActivity;
import com.ericbandiero.dancerdata.activities.PredictActivity;
import com.ericbandiero.dancerdata.code.DancerDao;
import com.ericbandiero.dancerdata.code.HandleAChildClick;
import com.ericbandiero.dancerdata.code.StatData;
import com.ericbandiero.dancerdata.code.TestDaggerObject;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Eric Bandiero on 11/1/2017.
 */

@Singleton
@Component(modules = {AppModule.class,TestObjectModule.class,DancerModule.class})
public interface BasicComponent {
	void inject(AndroidDataActivity activity);
	void inject(PredictActivity activity);
	void inject(DetailActivity activity);
	void inject(PerfActivity activity);
	void inject(DancedAtVenue activity);
	void inject(TestDaggerObject td);
	void inject(HandleAChildClick ck);
	void inject (HandleListClickForVenueCount venueClick);
	void inject(DancerDao dancerDao);
}
