package com.ericbandiero.dancerdata.activities;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.ericbandiero.dancerdata.*;
import com.ericbandiero.dancerdata.code.AppConstant;
import com.ericbandiero.dancerdata.code.IProcessCursor;
import com.ericbandiero.dancerdata.dagger.DanceApp;
import com.ericbandiero.dancerdata.code.DancerDao;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import javax.inject.Inject;

public class DetailActivity extends AppCompatActivity implements OnItemClickListener,IProcessCursor{

	private static final String TAG = "DetailActivity";
	private TextView txtviewVenue;
	private TextView txtviewDate;
	private TextView txtviewTitle;
	Set<String> setchoreos= new LinkedHashSet<>();
	Set<String> setdancers= new LinkedHashSet<>();
	Set<String> setids= new LinkedHashSet<>();
	
	public static String dancerdetailid="-1";
	Cursor cursorDanceInfo;


	public static String getDancerdetailid() {
		return dancerdetailid;
	}


	public static void setDancerdetailid(String dancerdetailid) {
        Log.i("Set id",dancerdetailid);
		DetailActivity.dancerdetailid = dancerdetailid;
	}


	List<String>listDancers= new ArrayList<>();
	
	public static int dance_id;
	//SQLiteDatabase database;
	@Inject
	DancerDao dancerDao;
	private TextView txtviewChoreos;
	ListView listviewdancers;
	public static void setDance_id(int dance_id) {
		DetailActivity.dance_id = dance_id;
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		//Dagger
		DanceApp.app().basicComponent().inject(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setchoreos.clear();
		setdancers.clear();
		listDancers.clear();
		txtviewVenue= findViewById(R.id.textViewVenue);
		txtviewDate= findViewById(R.id.textViewDate);
		txtviewTitle= findViewById(R.id.textViewTitle);
		txtviewChoreos= findViewById(R.id.textViewChoreos);
		listviewdancers= findViewById(R.id.listViewDancer);
		listviewdancers.setOnItemClickListener(this);
		
		
		// Show the Up button in the action bar.
	//	setupActionBar();
		//SqlHelper sqlHelper=new SqlHelper(this);

		//sql_database=sqlHelper.getWritableDatabase();

		//sql_database=DancerData.getSqldatabase();

//		ListAdapter listadapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listDancers);
//		listviewdancers.setAdapter(listadapter);
//		 


		Log.d(TAG,"Dance code getting data for:"+dance_id);
			
		String sqlString="select * from Info where "+ DancerDao.DANCE_CODE+"="+dance_id+
				 " order by "+ DancerDao.LAST_NAME+", "+ DancerDao.FIRST_NAME+", "+
				 DancerDao.CLAST_NAME+", "+ DancerDao.CFIRST_NAME;
		 
		//Cursor c=sql_database.rawQuery("select * from Info where "+DancerData.DANCE_CODE+"="+dance_id,null);
		dancerDao.runRawQueryWithRxJava(sqlString,this);


	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.venue, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
				if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","User pressed up button!");
			//NavUtils.navigateUpFromSameTask(this);
				onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long row_id) {
		// TODO Auto-generated method stub
	Log.i(TAG,"Position:"+position);	
	Log.i(TAG,"Id:"+row_id);
	Log.i(TAG,"Setid:"+ new ArrayList<>(setids).get(position));
	DetailActivity.dancerdetailid= new ArrayList<>(setids).get(position);
		cursorDanceInfo.close();
		NavUtils.navigateUpFromSameTask(this);
	finish();
	}

	private void setUpDataFromCursor(Cursor cursor){
		cursorDanceInfo=cursor;
		cursorDanceInfo.moveToFirst(); // it's very important to do this action otherwise
		// your Cursor object did not get work

		Log.d(TAG,"Dance id:"+ DetailActivity.dance_id);

		/* Check if at least one Result was returned. */
		if (cursorDanceInfo != null) {

			cursorDanceInfo.moveToFirst(); // it's very important to do this action otherwise
			// your Cursor object did not get work
			Log.i(TAG,cursorDanceInfo.getColumnName(0));
			/* Check if at least one Result was returned. */
			if (cursorDanceInfo.isFirst()) {
				int i = 0;
				/* Loop through all Results */
				txtviewVenue.setText("Venue: "+cursorDanceInfo.getString( cursorDanceInfo.getColumnIndex(DancerDao.VENUE)));
				txtviewDate.setText("Date: "+cursorDanceInfo.getString( cursorDanceInfo.getColumnIndex(DancerDao.PERF_DATE)));
				txtviewTitle.setText("Title: "+cursorDanceInfo.getString( cursorDanceInfo.getColumnIndex(DancerDao.TITLE)));
				do {
					setdancers.add(cursorDanceInfo.getString( cursorDanceInfo.getColumnIndex(DancerDao.FIRST_NAME))+" "+
							cursorDanceInfo.getString( cursorDanceInfo.getColumnIndex(DancerDao.LAST_NAME)));

					setchoreos.add(cursorDanceInfo.getString( cursorDanceInfo.getColumnIndex(DancerDao.CFIRST_NAME))+" "+
							cursorDanceInfo.getString( cursorDanceInfo.getColumnIndex(DancerDao.CLAST_NAME)));

					setids.add(cursorDanceInfo.getString( cursorDanceInfo.getColumnIndex(DancerDao.CODE)));

				} while (cursorDanceInfo.moveToNext());
				txtviewChoreos.setText("Choreographers: "+setchoreos.toString());
				listDancers.addAll(setdancers);
				cursorDanceInfo.close();
			}

			//This we moved here so that the data is already prepared.
			ListAdapter listadapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listDancers);
			listviewdancers.setAdapter(listadapter);

		}
	}

	@Override
	public void test(Cursor cursor) {
		setUpDataFromCursor(cursor);
	}
}
