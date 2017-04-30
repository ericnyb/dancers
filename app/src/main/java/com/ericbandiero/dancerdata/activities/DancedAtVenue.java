package com.ericbandiero.dancerdata.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;

import com.ericbandiero.dancerdata.AppConstant;
import com.ericbandiero.dancerdata.code.DancerDao;


/**
 * Created by ${"Eric Bandiero"} on 3/31/2017.
 */

public class DancedAtVenue {
	static SQLiteDatabase db;
	final static String TAG="Perf";
	SimpleCursorAdapter mAdapter;
	Cursor cursor;

	public static void main(String[] args) {

		DancedAtVenue dancedAtVenue=new DancedAtVenue();
		dancedAtVenue.runner();
	}

	private void runner() {
		//SQLiteDatabase db = SQLiteDatabase.openDatabase(SqlHelper.SAMPLE_DB_NAME, null, 0);
		DancerDao dancerDao=new DancerDao(AppConstant.CONTEXT);
		Cursor cursor = dancerDao.runRawQuery("select PerfDate as _id,PerfDate,PerfDesc,Venue from Info group by PerfDate,Venue order by PerfDate desc");
		//this.cursor = db.rawQuery("select PerfDate as _id,PerfDate,PerfDesc,Venue from Info group by PerfDate,Venue order by PerfDate desc", null);
		this.cursor.moveToFirst();
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">", this.cursor.getString(0));

	}


}
