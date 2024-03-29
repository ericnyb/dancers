package com.ericbandiero.dancerdata.code;

import android.database.Cursor;
import android.util.Log;

import com.ericbandiero.librarymain.data_classes.DataHolderTwoFields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class runs our stats query
 * Created by Eric Bandiero on 10/25/2017.
 */

public class StatData {

	private final DancerDao dancerDao;


	private final List<DataHolderTwoFields> dataHolderTwoFieldsList=new ArrayList<>();


	public StatData(DancerDao sqLiteDatabase) {
		//DanceApp.app().testObjectComponent().inject(this);
		//DaggerTestObjectComponent.create().inject(this);
		//testObjectComponent.inject(this);
		//if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Test object use:"+testDaggerObject.getName());
		dancerDao=sqLiteDatabase;
	}

	public void runStats() {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Running stats....!");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Stats thread:"+Thread.currentThread().getName());
		dataHolderTwoFieldsList.clear();
		getPerformanceCount();
		getDanceWorksCount();
		getVenueCount();
		getDancerCount();
		getChoreographerCount();
		getSolos();
		getMostShotVenue(true);
		//getMostPiecesShotAtVenue();
		getFirstAndLastPerformance();
		getMostCommonName();
	}

	private void getDancerCount() {
		Cursor cursor = dancerDao.runRawQueryMainThread("Select distinct "+ DancerDao.CODE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Dancer count"+cursor.getCount());
		//dataMap.put("Dancers",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Dancers:",String.valueOf(cursor.getCount())));
		cursor.close();
	}


	private void getChoreographerCount() {
		Cursor cursor = dancerDao.runRawQueryMainThread("Select distinct "+ DancerDao.CHOR_CODE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Chore count"+cursor.getCount());
		//dataMap.put("Choreographers",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Choreographers:",String.valueOf(cursor.getCount())));
		cursor.close();
	}

	private void getVenueCount() {
		Cursor cursor = dancerDao.runRawQueryMainThread("Select distinct "+ DancerDao.VENUE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Venue count"+cursor.getCount());
		//dataMap.put("Venues",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Venues:",String.valueOf(cursor.getCount())));
		cursor.close();
	}

	private void getDanceWorksCount() {
		Cursor cursor = dancerDao.runRawQueryMainThread("Select distinct "+ DancerDao.DANCE_CODE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Dance pieces count"+cursor.getCount());
		//dataMap.put("Dance pieces",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Dance pieces:",String.valueOf(cursor.getCount())));
		cursor.close();
	}

	private void getPerformanceCount() {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Thread plain:"+Thread.currentThread().getName());
		Cursor cursor = dancerDao.runRawQueryMainThread("Select distinct "+ DancerDao.PERF_CODE+" from info");
		List<Integer> code1=new ArrayList<>();

		while (cursor.moveToNext()){
			code1.add(cursor.getInt(0));
		}


		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Performance set 1:"+code1.size());

		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Performance count:"+cursor.getCount());
		//dataMap.put("Performances",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Performances:",String.valueOf(cursor.getCount())));
		cursor.close();
	}

	private void getFirstAndLastPerformance() {
		Cursor cursor = dancerDao.runRawQueryMainThread("Select min ("+ DancerDao.PERF_DATE+") from info");
		cursor.moveToFirst();
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("First shoot:",String.valueOf(cursor.getString(0))));

		cursor = dancerDao.runRawQueryMainThread("Select max ("+ DancerDao.PERF_DATE+") from info");
		cursor.moveToFirst();
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Performance last shoot"+cursor.getString(0));
		//dataMap.put("Latest shoot",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Last shoot:",String.valueOf(cursor.getString(0))));
		cursor.close();
	}

	private void getMostCommonName() {

		String sql="Select distinct "+DancerDao.FIRST_NAME +",count(distinct "+ DancerDao.CODE+"+"+DancerDao.FIRST_NAME+") from info group by "+DancerDao.FIRST_NAME +" order by 2 desc";
		Cursor cursor = dancerDao.runRawQueryMainThread(sql);

		if(cursor != null && cursor.moveToFirst()){
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Common first name:",String.valueOf(cursor.getString(0))+"("+String.valueOf(cursor.getString(1))+")"));
		cursor.close();
		}

		sql="Select distinct "+DancerDao.LAST_NAME +",count(distinct "+ DancerDao.CODE+"+"+DancerDao.LAST_NAME+") from info group by "+DancerDao.LAST_NAME +" order by 2 desc";
		cursor = dancerDao.runRawQueryMainThread(sql);

		if(cursor != null && cursor.moveToFirst()) {
			dataHolderTwoFieldsList.add(new DataHolderTwoFields("Common last name:", String.valueOf(cursor.getString(0)) + "(" + String.valueOf(cursor.getString(1)) + ")"));
			cursor.close();
		}
	}

	private void getSolos() {
		String sql="Select count(distinct "+ DancerDao.CODE+") as cnt from info group by "+DancerDao.DANCE_CODE +" having cnt=1";
		Cursor cursor = dancerDao.runRawQueryMainThread(sql);
		cursor.moveToFirst();
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Solos:",String.valueOf(cursor.getCount())));
		cursor.close();
}

	private void getMostShotVenue(boolean rollUp) {
		final int maxLengthOfVenueName=rollUp?10:30;
		String sql="Select "+DancerDao.VENUE+",count(distinct "+ DancerDao.PERF_DATE+") as cnt from info group by "+DancerDao.VENUE +" having cnt>1 order by cnt desc";
		if (AppConstant.DEBUG) Log.d(new Object() { }.getClass().getEnclosingClass()+">","Sql:"+sql);
		Cursor cursor = dancerDao.runRawQueryMainThread(sql);

		while (cursor.moveToNext()){
			String venueName=cursor.getString(0).trim();
			if (rollUp){
				DataHolderTwoFields dataHolderTwoFields=new DataHolderTwoFields("Venue most shot:",venueName.substring(0,(venueName.length()>maxLengthOfVenueName?maxLengthOfVenueName:venueName.length()))+":"+String.valueOf(cursor.getString(1)));
				dataHolderTwoFieldsList.add(dataHolderTwoFields);
			}
			else {
				DataHolderTwoFields dataHolderTwoFields=new DataHolderTwoFields(venueName.substring(0, (venueName.length() > maxLengthOfVenueName ? maxLengthOfVenueName : venueName.length()))+":",  String.valueOf(cursor.getString(1)));
				dataHolderTwoFields.setId(venueName); //Want this for click event.
				dataHolderTwoFieldsList.add(dataHolderTwoFields);
			}
			if (rollUp){
				break;
			}
		}
		cursor.close();
//		String venueName=cursor.getString(0).trim();
//		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Venue most shot:",venueName.substring(0,(venueName.length()>maxLengthOfVenueName?maxLengthOfVenueName:venueName.length()))+":"+String.valueOf(cursor.getString(1))));
	}


public static String getSubStringForField(String stringToShorten,int maxLength){
	int stringLength=stringToShorten.length();
	return (stringToShorten.length()>maxLength)?stringToShorten.substring(0,maxLength):stringToShorten;
}

public static String formatStatData(Map<String,Integer> stringIntegerMap){
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

	public List<DataHolderTwoFields> getDataHolderTwoFieldsList() {
		return dataHolderTwoFieldsList;
	}

	public static String[] formatStatDataForTwoColumnsArray(Map<String,Integer> stringIntegerMap){

		//First get max value
		int maxValue=maxLengthOfValueFromMap(stringIntegerMap);

		int maxValueLength=String.valueOf(maxValue).length();

		if (AppConstant.DEBUG) Log.d(new Object() { }.getClass().getEnclosingClass()+">","Max value:"+maxValue);
		if (AppConstant.DEBUG) Log.d(new Object() { }.getClass().getEnclosingClass()+">","Max value length:"+maxValueLength);



		String [] dataArray=new String[stringIntegerMap.size()*2];

		if (AppConstant.DEBUG) Log.d(new Object() { }.getClass().getEnclosingClass()+">","data array length:"+dataArray.length);

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

	private static int maxLengthOfValueFromMap(Map<String, Integer> stringIntegerMap){
		Collection<Integer> c = stringIntegerMap.values();
		return Collections.max(c);
	}
}
