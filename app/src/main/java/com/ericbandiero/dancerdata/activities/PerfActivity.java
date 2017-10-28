package com.ericbandiero.dancerdata.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ericbandiero.dancerdata.AppConstant;
import com.ericbandiero.dancerdata.R;
import com.ericbandiero.dancerdata.code.DancerDao;
import com.ericbandiero.dancerdata.code.StatData;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/*
 * We will use this to show performance and date information. 
 * Used to schedule future commitments.
 * Will use cursor adapter.
 * Will use custom layout file. 
 */

public class PerfActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
	//static SQLiteDatabase db;
	final static String TAG="Perf";
	SimpleCursorAdapter mAdapter; 	
	Cursor cursor;

	@BindView(R.id.listViewPerfs)
	ListView listviewperf;

	@BindView(R.id.textViewPerfInfo)
	TextView textviewinfo;

	DancerDao dancerDao;

	//Cool ButterKnife feature - cleaner code
	@OnClick(R.id.textViewPerfInfo)
	public void submit() {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Hi");
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_perf);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("Performances");



		ButterKnife.bind(this);
		//listviewperf=(ListView) findViewById(R.id.listViewPerfs);
		listviewperf.setOnItemClickListener(this);
		//textviewinfo=(TextView)findViewById(R.id.textViewPerfInfo);

		dancerDao=new DancerDao(this);
	}

	/**
	 * Dispatch onResume() to fragments.  Note that for better inter-operation
	 * with older versions of the platform, at the point of this call the
	 * fragments attached to the activity are <em>not</em> resumed.  This means
	 * that in some cases the previous state may still be saved, not allowing
	 * fragment transactions that modify the state.  To correctly interact
	 * with fragments in their proper state, you should instead override
	 * {@link #onResumeFragments()}.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		getDancersAtVenue();
		this.cursor=getData();
		if (this.cursor!=null) {
			showData();
			textviewinfo.setText("Performances: " + this.cursor.getCount());
			this.cursor.moveToFirst();
			Log.d(TAG, "Field length:" + this.cursor.getString(1).length());
		}
		else{
			onBackPressed();
		}
		//cursor.close();
	}

	private void showData() {
		android.support.v4.widget.SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, 
		        R.layout.threetexts, 
		        cursor, 
		        new String[] {"PerfDate","PerfDesc","Venue"},
		        new int[] { R.id.textViewRecordCount1,R.id.textView2,R.id.textView3},1);
		listviewperf.setAdapter(adapter);
		TextView tv=(TextView) findViewById(R.id.textViewRecordCount1);
	//	Log.d(TAG,"Text size"+tv.getText());

    }

	private void getDancersAtVenue(){
		//SQLiteDatabase db2= sqlHelper.getWritableDatabase();
		Cursor cursor1 = dancerDao.runRawQuery("select PerfDate as _id,PerfDate,PerfDesc,Venue from Info group by PerfDate,Venue order by PerfDate desc");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Cursor1 count:"+cursor1.getCount());
		SortedSet<String> setVenueNames=new TreeSet<>();

		while (cursor1.moveToNext()) {
			setVenueNames.add(cursor1.getString(cursor1.getColumnIndex("Venue")));
		}

		cursor1.close();

		for (String venueName : setVenueNames) {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Venue name:"+venueName);

		}


	}

	private Cursor getData() {

		Cursor cursor;
		cursor=dancerDao.runRawQuery("select PerfDate as _id,PerfDate,PerfDesc,Venue,Perf_Code from Info group by PerfDate,Venue,Perf_Code order by PerfDate desc");
		if (cursor==null){
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Cursor is null");
		}
		return cursor;
	}


//	public static void setDataBase(SQLiteDatabase database){
//		db=database;
//	}

    @Override
    protected void onStop() {
		if (cursor!=null) {
			cursor.close();
			dancerDao.close();
		}
        super.onStop();
    }

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

		cursor.moveToPosition(i);
		String dateOfPerformance=cursor.getString(1);
		String descOfPerformance=cursor.getString(2);

		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Date of perf:"+dateOfPerformance);

		String rawQuery="select PerfDate as _id,Title,Dance_Code from Info where PerfDate='"+dateOfPerformance+"' group by Title,Dance_Code order by PerfDate desc";
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Raw query:"+rawQuery);
		Cursor cursTest=dancerDao.runRawQuery(rawQuery);
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Records:"+cursTest.getCount());
		cursTest.close();
	}
}

