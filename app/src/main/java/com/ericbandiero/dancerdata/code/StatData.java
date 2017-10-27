package com.ericbandiero.dancerdata.code;

import android.database.Cursor;
import android.util.Log;

import java.util.Collection;
import java.util.Collections;
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
		getDancerCount();
		getChoreographerCount();
		getVenueCount();
		getDanceWorksCount();
		getPerformanceCount();
		return dataMap;
	}

	private void getDancerCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.CODE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Dancer count"+cursor.getCount());
		dataMap.put("Dancers",cursor.getCount());
	}


	private void getChoreographerCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.CHOR_CODE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Chore count"+cursor.getCount());
		dataMap.put("Choreographers",cursor.getCount());
	}

	private void getVenueCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.VENUE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Venue count"+cursor.getCount());
		dataMap.put("Venues",cursor.getCount());
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

public static String formatStatData(Map<String,Integer> stringIntegerMap){
	//if (com.ericbandiero.dancerdata.AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Data:"+stringIntegerMap.toString());
	StringBuilder stringBuilder=new StringBuilder();

	String line_sep = System.getProperty("line.separator");

	for (Map.Entry<String, Integer> stringIntegerEntry : stringIntegerMap.entrySet()) {
		stringBuilder.append(stringIntegerEntry.getKey());
		stringBuilder.append(":");
		stringBuilder.append(stringIntegerEntry.getValue());
		stringBuilder.append(line_sep);
	}
	return stringBuilder.toString();
}

	public static String[] formatStatDataForTwoColumnsArray(Map<String,Integer> stringIntegerMap){
		//if (com.ericbandiero.dancerdata.AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Data:"+stringIntegerMap.toString());

		//First get max value
		int maxValue=maxLengthOfValueFromMap(stringIntegerMap);

		int maxValueLength=String.valueOf(maxValue).length();

		if (AppConstant.DEBUG) Log.d(new Object() { }.getClass().getEnclosingClass()+">","Max value:"+maxValue);
		if (AppConstant.DEBUG) Log.d(new Object() { }.getClass().getEnclosingClass()+">","Max value length:"+maxValueLength);



		String [] dataArray=new String[stringIntegerMap.size()*2];

		if (AppConstant.DEBUG) Log.d(new Object() { }.getClass().getEnclosingClass()+">","data array length:"+dataArray.length);


		StringBuilder stringBuilder=new StringBuilder();

		String line_sep = System.getProperty("line.separator");
		int counter=0;

		for (Map.Entry<String, Integer> stringIntegerEntry : stringIntegerMap.entrySet()) {
				dataArray[counter]=stringIntegerEntry.getKey()+":";
				counter++;
				int stuff=maxValueLength-String.valueOf(stringIntegerEntry.getValue()).length();
				if (AppConstant.DEBUG) Log.d(new Object() { }.getClass().getEnclosingClass()+">","Stuff:"+stuff);

			String s1="";
			if (stuff>0) {
				s1 = String.format("%0" + stuff + "d", 0).replace("0", "  ");
				if (AppConstant.DEBUG) Log.d(new Object() { }.getClass().getEnclosingClass()+">","String:"+s1);
			}
				dataArray[counter]=s1+String.valueOf(stringIntegerEntry.getValue());
				counter++;
		}
		return dataArray;
	}

	public static int maxLengthOfValueFromMap(Map<String,Integer> stringIntegerMap){
		Collection c = stringIntegerMap.values();
		return (int) Collections.max(c);
	}
}
