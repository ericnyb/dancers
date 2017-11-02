package com.ericbandiero.dancerdata.code;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
	public TestDaggerObject provideDaggerObject(){
		return new TestDaggerObject("Eric");
	}

	/*
	@Singleton @Provides
	public ObjectManager provideObjectManager(SharedPreferences sharedPreferences, Gson gson){
		return new ObjectManager(sharedPreferences, gson);
	}
	*/
}