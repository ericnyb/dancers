package com.ericbandiero.dancerdata.dagger;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.ericbandiero.dancerdata.R;
import com.ericbandiero.dancerdata.activities.HandleListClickForVenueCount;
import com.ericbandiero.dancerdata.code.DancerDao;
import com.ericbandiero.dancerdata.code.StatData;
import com.ericbandiero.librarymain.basecode.ControlStatsActivityBuilder;
import com.ericbandiero.librarymain.basecode.ControlStatsAdapterBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Eric Bandiero on 11/1/2017.
 */

@Module
public class AppModule {
	private Context context;

	public AppModule(Context context) {
		this.context = context;
	}

	@Singleton
	@Provides
	public Context provideContext(){
		return context;
	}

	@Singleton @Provides
	public SharedPreferences provideSharedPreferences(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Singleton @Provides
	public HandleListClickForVenueCount provideHandleTestClick(){
		return new HandleListClickForVenueCount();
	}

	@Singleton @Provides
	public StatData provideStatData(DancerDao dancerDao){
		return new StatData(dancerDao);
	}

	@Provides @Named ("stats")
	public ControlStatsActivityBuilder provideDaggerControlStatsActivity(StatData statData){
		return new ControlStatsActivityBuilder("Shooting History Stats",
				"Data",
				ContextCompat.getColor(context, R.color.Background_Light_Yellow),
				statData.runStats(),null);
	}

	@Provides @Named ("stats_venues")
	public ControlStatsActivityBuilder provideDaggerControlStatsActivity1(StatData statData){
		return new ControlStatsActivityBuilder("Venue Stats",
				"Venues By Shoots",
				ContextCompat.getColor(context, R.color.Background_Light_Yellow),
				statData.runVenueStats(), provideHandleTestClick() );
	}

	@Provides @Named ("stats_gigs_by_year")
	public ControlStatsActivityBuilder provideDaggerControlStatsActivityGigs(StatData statData){
		return new ControlStatsActivityBuilder("Gigs By Year",
				"Gigs By Year",
				ContextCompat.getColor(context, R.color.Background_Light_Yellow),
				statData.runGigsByYear(), null);
	}

	@Singleton @Provides
	public ControlStatsAdapterBuilder provideDaggerControlStatsAdapterBuilder(){
		return new ControlStatsAdapterBuilder(
				ContextCompat.getColor(context, R.color.LightBlue),
				ContextCompat.getColor(context,R.color.LightSalmon),
				ContextCompat.getColor(context,R.color.LightGreen),
				false);
	}

	/*
	@Singleton @Provides
	public ObjectManager provideObjectManager(SharedPreferences sharedPreferences, Gson gson){
		return new ObjectManager(sharedPreferences, gson);
	}
	*/
}