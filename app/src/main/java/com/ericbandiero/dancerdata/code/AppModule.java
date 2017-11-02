package com.ericbandiero.dancerdata.code;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.ericbandiero.dancerdata.R;
import com.ericbandiero.dancerdata.activities.HandleTestClick;
import com.ericbandiero.librarymain.basecode.ControlStatsActivityBuilder;
import com.ericbandiero.librarymain.basecode.ControlStatsAdapterBuilder;
import com.ericbandiero.librarymain.data_classes.DataHolderTwoFields;

import java.util.ArrayList;
import java.util.List;

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
	private DancerDao dancerDao;

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
	public TestDaggerObject provideDaggerObject(){
		return new TestDaggerObject("Eric");
	}

	@Singleton @Provides
	public DancerDao provideDancerDao(){
		return new DancerDao(context);
	}

	@Singleton @Provides
	public HandleTestClick provideHandleTestClick(){
		return new HandleTestClick();
	}

	@Singleton @Provides
	public StatData provideStatData(DancerDao dancerDao){
		return new StatData(dancerDao);
	}

	@Singleton @Provides @Named ("stats")
	public ControlStatsActivityBuilder provideDaggerControlStatsActivity(StatData statData){
		return new ControlStatsActivityBuilder("Shooting History Stats",
				"Data",
				ContextCompat.getColor(context, R.color.Background_Light_Yellow),
				statData.runStats(), provideHandleTestClick());
	}

	@Singleton @Provides @Named ("stats_new")
	public ControlStatsActivityBuilder provideDaggerControlStatsActivity1(StatData statData){
		return new ControlStatsActivityBuilder("Shooting History Stats",
				"My New Data",
				ContextCompat.getColor(context, R.color.Background_Light_Yellow),
				statData.runStats(), provideHandleTestClick());
	}

	@Singleton @Provides
	public ControlStatsAdapterBuilder provideDaggerControlStatsAdapterBuilder(){
		return new ControlStatsAdapterBuilder(
				ContextCompat.getColor(context, R.color.LightBlue),
				ContextCompat.getColor(context,R.color.LightSalmon),
				ContextCompat.getColor(context,R.color.LightGreen),
				true);
	}

	/*
	@Singleton @Provides
	public ObjectManager provideObjectManager(SharedPreferences sharedPreferences, Gson gson){
		return new ObjectManager(sharedPreferences, gson);
	}
	*/
}