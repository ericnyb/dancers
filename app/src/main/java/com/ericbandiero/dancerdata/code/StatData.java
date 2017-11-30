package com.ericbandiero.dancerdata.code;

import android.database.Cursor;
import android.util.Log;

import com.ericbandiero.dancerdata.dagger.DaggerTestObjectComponent;
import com.ericbandiero.dancerdata.dagger.TestObjectComponent;
import com.ericbandiero.librarymain.data_classes.DataHolderTwoFields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Eric Bandiero on 10/25/2017.
 */

public class StatData {

	DancerDao dancerDao;

	Map<String,Integer> dataMap=new LinkedHashMap<>();

	List<DataHolderTwoFields> dataHolderTwoFieldsList=new ArrayList<>();

	//@Inject
	//TestDaggerObject testDaggerObject;

	private TestObjectComponent testObjectComponent;

	public StatData(DancerDao sqLiteDatabase) {
		//DanceApp.app().testObjectComponent().inject(this);
		DaggerTestObjectComponent.create().inject(this);
		//testObjectComponent.inject(this);
		//if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Test object use:"+testDaggerObject.getName());
		dancerDao=sqLiteDatabase;
	}

	public List<DataHolderTwoFields> runStats() {
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
		return dataHolderTwoFieldsList;
	}

	public List<DataHolderTwoFields> runVenueStats() {
		dataHolderTwoFieldsList.clear();
		getMostShotVenue(false);
		return dataHolderTwoFieldsList;
	}

	public List<DataHolderTwoFields> runVenueMostPiecesStats() {
		dataHolderTwoFieldsList.clear();
		getMostPiecesShotAtVenue();
		return dataHolderTwoFieldsList;
	}

	public List<DataHolderTwoFields> runGigsByYear() {
		dataHolderTwoFieldsList.clear();
		getGigsByYear();
		return dataHolderTwoFieldsList;
	}

	public List<DataHolderTwoFields> runDancersCountByWorks() {
		dataHolderTwoFieldsList.clear();
		getDancerByWorks();
		return dataHolderTwoFieldsList;
	}

	private void getGigsByYear() {
		Cursor cursor = dancerDao.runRawQuery("Select "+ "strftime('%Y',"+DancerDao.PERF_DATE+") as year," +
				"count(distinct "+DancerDao.PERF_CODE+")" +
				" from info" +
				" group by "+"strftime('%Y',"+DancerDao.PERF_DATE+")" +
				" order by year desc");
		while (cursor.moveToNext()){
			dataHolderTwoFieldsList.add(new DataHolderTwoFields(cursor.getString(0),cursor.getString(1)));
		}
	}

	private void getDancerCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.CODE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Dancer count"+cursor.getCount());
		//dataMap.put("Dancers",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Dancers:",String.valueOf(cursor.getCount())));
	}


	private void getChoreographerCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.CHOR_CODE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Chore count"+cursor.getCount());
		//dataMap.put("Choreographers",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Choreographers:",String.valueOf(cursor.getCount())));
	}

	private void getVenueCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.VENUE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Venue count"+cursor.getCount());
		//dataMap.put("Venues",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Venues:",String.valueOf(cursor.getCount())));

	}

	private void getDanceWorksCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.DANCE_CODE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Dance pieces count"+cursor.getCount());
		//dataMap.put("Dance pieces",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Dance pieces:",String.valueOf(cursor.getCount())));

	}

	private void getPerformanceCount() {
		Cursor cursor = dancerDao.runRawQuery("Select distinct "+ DancerDao.PERF_CODE+" from info");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Performance count"+cursor.getCount());
		//dataMap.put("Performances",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Performances:",String.valueOf(cursor.getCount())));
	}

	private void getFirstAndLastPerformance() {
		Cursor cursor = dancerDao.runRawQuery("Select min ("+ DancerDao.PERF_DATE+") from info");
		cursor.moveToFirst();
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("First shoot:",String.valueOf(cursor.getString(0))));

		cursor = dancerDao.runRawQuery("Select max ("+ DancerDao.PERF_DATE+") from info");
		cursor.moveToFirst();
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Performance last shoot"+cursor.getString(0));
		//dataMap.put("Latest shoot",cursor.getCount());
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Last shoot:",String.valueOf(cursor.getString(0))));
	}

	private void getMostCommonName() {

		String sql="Select distinct "+DancerDao.FIRST_NAME +",count(distinct "+ DancerDao.CODE+"+"+DancerDao.FIRST_NAME+") from info group by "+DancerDao.FIRST_NAME +" order by 2 desc";
		Cursor cursor = dancerDao.runRawQuery(sql);

		if(cursor != null && cursor.moveToFirst()){
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Common first name:",String.valueOf(cursor.getString(0))+"("+String.valueOf(cursor.getString(1))+")"));
		cursor.close();
		}

		sql="Select distinct "+DancerDao.LAST_NAME +",count(distinct "+ DancerDao.CODE+"+"+DancerDao.LAST_NAME+") from info group by "+DancerDao.LAST_NAME +" order by 2 desc";
		cursor = dancerDao.runRawQuery(sql);

		if(cursor != null && cursor.moveToFirst()) {
			dataHolderTwoFieldsList.add(new DataHolderTwoFields("Common last name:", String.valueOf(cursor.getString(0)) + "(" + String.valueOf(cursor.getString(1)) + ")"));
			cursor.close();
		}
	}

	private void getSolos() {
		String sql="Select count(distinct "+ DancerDao.CODE+") as cnt from info group by "+DancerDao.DANCE_CODE +" having cnt=1";
		Cursor cursor = dancerDao.runRawQuery(sql);
		cursor.moveToFirst();
		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Solos:",String.valueOf(cursor.getCount())));
}

	private void getMostShotVenue(boolean rollUp) {
		final int maxLengthOfVenueName=rollUp?10:30;
		String sql="Select "+DancerDao.VENUE+",count(distinct "+ DancerDao.PERF_DATE+") as cnt from info group by "+DancerDao.VENUE +" having cnt>1 order by cnt desc";
		if (AppConstant.DEBUG) Log.d(new Object() { }.getClass().getEnclosingClass()+">","Sql:"+sql);
		Cursor cursor = dancerDao.runRawQuery(sql);

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

//		String venueName=cursor.getString(0).trim();
//		dataHolderTwoFieldsList.add(new DataHolderTwoFields("Venue most shot:",venueName.substring(0,(venueName.length()>maxLengthOfVenueName?maxLengthOfVenueName:venueName.length()))+":"+String.valueOf(cursor.getString(1))));
	}

private void getMostPiecesShotAtVenue(){
	if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Venues by dance pieces...");
	int maxLengthOfVenueName=30;
	String sql="Select "+DancerDao.VENUE+",count(distinct "+ DancerDao.DANCE_CODE+") as cnt from info group by "+DancerDao.VENUE +" order by cnt desc";
	if (AppConstant.DEBUG) Log.d(new Object() { }.getClass().getEnclosingClass()+">","Sql:"+sql);
	Cursor cursor = dancerDao.runRawQuery(sql);

	while (cursor.moveToNext()){
		String venueName=cursor.getString(0).trim();

		DataHolderTwoFields dataHolderTwoFields=new DataHolderTwoFields(venueName.substring(0, (venueName.length() > maxLengthOfVenueName ? maxLengthOfVenueName : venueName.length()))+":",  String.valueOf(cursor.getString(1)));
			dataHolderTwoFields.setId(venueName); //Want this for click event.
			dataHolderTwoFieldsList.add(dataHolderTwoFields);
		}
}

	private void getDancerByWorks() {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Dancers by dance pieces...");
		int maxLengthOfFieldOne=30;
		String sql="Select "+DancerDao.CODE+
				","+
				DancerDao.FIRST_NAME+
				","+
				DancerDao.LAST_NAME+
				",count(distinct "+
				DancerDao.DANCE_CODE+") as cnt" +
				" from info group by 1,2,3,"
				+DancerDao.CODE +" order by cnt desc";
		if (AppConstant.DEBUG) Log.d(new Object() { }.getClass().getEnclosingClass()+">","Sql:"+sql);
		Cursor cursor = dancerDao.runRawQuery(sql);

		while (cursor.moveToNext()){
			String fieldOne=getSubStringForField(cursor.getString(2).trim()+","+cursor.getString(1).trim(),maxLengthOfFieldOne);
			DataHolderTwoFields dataHolderTwoFields=new DataHolderTwoFields(fieldOne,cursor.getString(3).trim());
			dataHolderTwoFields.setId(cursor.getString(0)); //Want this for click event.
			dataHolderTwoFieldsList.add(dataHolderTwoFields);
		}
	}
private String getSubStringForField(String stringToShorten,int maxLength){
	int stringLength=stringToShorten.length();
	return (stringToShorten.length()>maxLength)?stringToShorten.substring(0,maxLength):stringToShorten;
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
		Collection<Integer> c = stringIntegerMap.values();
		return (int) Collections.max(c);
	}



}
