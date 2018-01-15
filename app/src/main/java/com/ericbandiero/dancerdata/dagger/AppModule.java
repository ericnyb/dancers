package com.ericbandiero.dancerdata.dagger;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.ericbandiero.dancerdata.R;
import com.ericbandiero.dancerdata.code.HandleClickForRecyclerVenueOrDancer;
import com.ericbandiero.dancerdata.code.AppConstant;
import com.ericbandiero.dancerdata.code.DancerDao;
import com.ericbandiero.dancerdata.code.HandleAChildClick;
import com.ericbandiero.dancerdata.code.StatData;
import com.ericbandiero.librarymain.basecode.ControlStatsActivityBuilder;
import com.ericbandiero.librarymain.basecode.ControlStatsAdapterBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Main Dagger module
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
	public HandleClickForRecyclerVenueOrDancer provideHandleClickForRecyclerVenueOrDancer(String v){
		return new HandleClickForRecyclerVenueOrDancer(v);
	}
/*

	@Singleton @Provides @Named("dance")
	public HandleClickForVenueOrDancerCount provideHandleClickDancer(){
		return new HandleClickForVenueOrDancerCount(HandleClickForVenueOrDancerCount.DANCER_COUNT);
	}
*/

	@Singleton @Provides
	public StatData provideStatData(DancerDao dancerDao){
		return new StatData(dancerDao);
	}

	@Provides @Named (AppConstant.DAG_CONTROLLER_STATS)
	public ControlStatsActivityBuilder provideDaggerControlStatsActivity(StatData statData){
		return new ControlStatsActivityBuilder("Shooting History Stats",
				"Data",
				ContextCompat.getColor(context, R.color.Background_Light_Yellow)
				);
	}

	@Singleton @Provides @Named (AppConstant.DAG_CONTROLLER_VENUE_BY_PERFORM_SHOOTS)
	public ControlStatsActivityBuilder provideDaggerControlStatsActivity1(){
		return new ControlStatsActivityBuilder("Venue Stats",
				"Venues By Performance Shoots",
				ContextCompat.getColor(context, R.color.Background_Light_Yellow),
				provideHandleClickForRecyclerVenueOrDancer(HandleClickForRecyclerVenueOrDancer.VENUE_COUNT));
	}

	@Singleton @Provides @Named (AppConstant.DAG_CONTROLLER_VENUE_BY_DANCE)
	public ControlStatsActivityBuilder provideDaggerControlStatsActivity2(){
		return new ControlStatsActivityBuilder("Venue Stats",
				"Venues By Dance Pieces Shot",
				ContextCompat.getColor(context, R.color.Background_Light_Yellow),
				provideHandleClickForRecyclerVenueOrDancer(HandleClickForRecyclerVenueOrDancer.VENUE_COUNT));
	}

	@Singleton @Provides @Named (AppConstant.DAG_CONTROLLER_GIGS_PER_YEAR)
	public ControlStatsActivityBuilder provideDaggerControlStatsActivityGigs(){
		return new ControlStatsActivityBuilder("Gigs By Year",
				"Gigs By Year",
				ContextCompat.getColor(context, R.color.Background_Light_Yellow)
				);
	}

	@Singleton @Provides @Named (AppConstant.DAG_CONTROLLER_DANCER_COUNT)
	public ControlStatsActivityBuilder provideDaggerControlStatsActivityDancerCounts(){
		return new ControlStatsActivityBuilder("Dancer Stats",
				"Dancers by performance",
				ContextCompat.getColor(context, R.color.Background_Light_Yellow),
				provideHandleClickForRecyclerVenueOrDancer(HandleClickForRecyclerVenueOrDancer.DANCER_COUNT));
	}

	@Singleton @Provides
	public ControlStatsAdapterBuilder provideDaggerControlStatsAdapterBuilder(){
		return new ControlStatsAdapterBuilder(
				ContextCompat.getColor(context, R.color.LightBlue),
				ContextCompat.getColor(context,R.color.LightSalmon),
				ContextCompat.getColor(context,R.color.LightGreen),
				false);
	}




	@Singleton
	@Provides
	@Named (HandleAChildClick.GET_PERFORMANCE_FROM_CLICK)
	public HandleAChildClick provideDaggerHandleClickVenues(){
		return new HandleAChildClick(HandleAChildClick.GET_PERFORMANCE_FROM_CLICK);
	}

	@Singleton
	@Provides
	@Named (HandleAChildClick.GET_DANCE_DETAIL_FROM_CLICK)
	public HandleAChildClick provideDaggerHandleClickPerformances(){
		return new HandleAChildClick(HandleAChildClick.GET_DANCE_DETAIL_FROM_CLICK);
	}

}