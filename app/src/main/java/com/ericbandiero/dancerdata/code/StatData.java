package com.ericbandiero.dancerdata.code;

import android.database.Cursor;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Eric Bandiero on 10/25/2017.
 */

public class StatData {

	DancerDao dancerDao;;

	Map<String,Integer> dataMap=new LinkedHashMap<>();


	public StatData(DancerDao sqLiteDatabase) {
		dancerDao=sqLiteDatabase;
	}

	public Map<String,Integer> runStats() {
		getChoreographerCount();
		getDancerCount();
		getVenueCount();
		getDanceWorksCount();
		getPerformanceCount();
		return dataMap;
	}

	private void getChoreographerCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.CHOR_CODE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Chore count"+cursor.getCount());
		dataMap.put("Choreographers",cursor.getCount());
	}


	private void getDancerCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.CODE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Dancer count"+cursor.getCount());
		dataMap.put("Dancers",cursor.getCount());
	}

	private void getVenueCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.VENUE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Venue count"+cursor.getCount());
		dataMap.put("Venue",cursor.getCount());
	}

	private void getDanceWorksCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.DANCE_CODE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Dance pieces count"+cursor.getCount());
		dataMap.put("Dance pieces",cursor.getCount());
	}

	private void getPerformanceCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.PERF_CODE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Performance count"+cursor.getCount());
		dataMap.put("Performances",cursor.getCount());
	}


}
