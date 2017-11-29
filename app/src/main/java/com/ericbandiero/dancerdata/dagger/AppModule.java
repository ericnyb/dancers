package com.ericbandiero.dancerdata.dagger;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.ericbandiero.dancerdata.R;
import com.ericbandiero.dancerdata.activities.HandleListClickForVenueCountOrDancerCount;
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
	public HandleListClickForVenueCountOrDancerCount provideHandleTestClick(String v){
		return new HandleListClickForVenueCountOrDancerCount(v);
	}
/*

	@Singleton @Provides @Named("dance")
	public HandleListClickForVenueCountOrDancerCount provideHandleClickDancer(){
		return new HandleListClickForVenueCountOrDancerCount(HandleListClickForVenueCountOrDancerCount.DANCER_COUNT);
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
				ContextCompat.getColor(context, R.color.Background_Light_Yellow),
				statData.runStats(),null);
	}

	@Provides @Named (AppConstant.DAG_CONTROLLER_VENUE_BY_PERFORM)
	public ControlStatsActivityBuilder provideDaggerControlStatsActivity1(StatData statData){
		return new ControlStatsActivityBuilder("Venue Stats",
				"Venues By Shoots",
				ContextCompat.getColor(context, R.color.Background_Light_Yellow),
				statData.runVenueStats(), provideHandleTestClick(HandleListClickForVenueCountOrDancerCount.VENUE_COUNT));
	}

	@Provides @Named (AppConstant.DAG_CONTROLLER_VENUE_BY_DANCE)
	public ControlStatsActivityBuilder provideDaggerControlStatsActivity2(StatData statData){
		return new ControlStatsActivityBuilder("Venue Stats",
				"Venues By Dance Pieces Shots",
				ContextCompat.getColor(context, R.color.Background_Light_Yellow),
				statData.runVenueMostPiecesStats(), provideHandleTestClick(HandleListClickForVenueCountOrDancerCount.VENUE_COUNT));
	}

	@Provides @Named (AppConstant.DAG_CONTROLLER_GIGS_PER_YEAR)
	public ControlStatsActivityBuilder provideDaggerControlStatsActivityGigs(StatData statData){
		return new ControlStatsActivityBuilder("Gigs By Year",
				"Gigs By Year",
				ContextCompat.getColor(context, R.color.Background_Light_Yellow),
				statData.runGigsByYear(), null);
	}

	@Provides @Named (AppConstant.DAG_CONTROLLER_DANCER_COUNT)
	public ControlStatsActivityBuilder provideDaggerControlStatsActivityDancerCounts(StatData statData){
		return new ControlStatsActivityBuilder("Dancer Stats",
				"Dancers by performance",
				ContextCompat.getColor(context, R.color.Background_Light_Yellow),
				statData.runDancersCountByWorks(), provideHandleTestClick(HandleListClickForVenueCountOrDancerCount.DANCER_COUNT));
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