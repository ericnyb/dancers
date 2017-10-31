package com.ericbandiero.dancerdata.code;

import android.database.Cursor;
import android.util.Log;

import com.ericbandiero.librarymain.data_classes.DataHolderTwoFields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eric Bandiero on 10/25/2017.
 */

public class StatData {

	DancerDao dancerDao;;

	Map<String,Integer> dataMap=new LinkedHashMap<>();

	List<DataHolderTwoFields> dataHolderTwoFieldsList=new ArrayList<>();


	public StatData(DancerDao sqLiteDatabase) {
		dancerDao=sqLiteDatabase;
	}

	public List<DataHolderTwoFields> runStats() {
		getDancerCount();
		getChoreographerCount();
		getVenueCount();
		getDanceWorksCount();
		getPerformanceCount();
		getFirstAndLastPerformance();
		getMostCommonName();
		return dataHolderTwoFieldsList;
	}

	private void getDancerCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.CODE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Dancer count"+cursor.getCount());
		dataMap.put("Dancers",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Dancers:",String.valueOf(cursor.getCount())));
	}


	private void getChoreographerCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.CHOR_CODE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Chore count"+cursor.getCount());
		dataMap.put("Choreographers",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Choreographers:",String.valueOf(cursor.getCount())));
	}

	private void getVenueCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.VENUE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Venue count"+cursor.getCount());
		dataMap.put("Venues",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Venues:",String.valueOf(cursor.getCount())));

	}

	private void getDanceWorksCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.DANCE_CODE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Dance pieces count"+cursor.getCount());
		dataMap.put("Dance pieces",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Dance pieces:",String.valueOf(cursor.getCount())));

	}

	private void getPerformanceCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.PERF_CODE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Performance count"+cursor.getCount());
		dataMap.put("Performances",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Performances:",String.valueOf(cursor.getCount())));
	}

	private void getFirstAndLastPerformance() {
		Cursor cursor = dancerDao.runRawQuery("Select min ("+ DancerDao.PERF_DATE+") from info");
		cursor.moveToFirst();
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("First shoot:",String.valueOf(cursor.getString(0))));

		cursor = dancerDao.runRawQuery("Select max ("+ DancerDao.PERF_DATE+") from info");
		cursor.moveToFirst();
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Performance last shoot"+cursor.getString(0));
		dataMap.put("Latest shoot",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Last shoot:",String.valueOf(cursor.getString(0))));
	}

	private void getMostCommonName() {
		String sql="Select distinct "+DancerDao.FIRST_NAME +",count(distinct "+ DancerDao.CODE+"+"+DancerDao.FIRST_NAME+") from info group by "+DancerDao.FIRST_NAME +" order by 2 desc";
		Cursor cursor = dancerDao.runRawQuery(sql);
		cursor.moveToFirst();
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Most common first name:",String.valueOf(cursor.getString(0))+"("+String.valueOf(cursor.getString(1))+")"));
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
