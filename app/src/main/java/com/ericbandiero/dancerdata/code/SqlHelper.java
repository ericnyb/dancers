package com.ericbandiero.dancerdata.code;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.ericbandiero.dancerdata.AppConstant;
import com.ericbandiero.dancerdata.activities.AndroidDataActivity;
import com.ericbandiero.librarymain.UtilsShared;

import java.io.Serializable;

/**
 * Created by ${"Eric Bandiero"} on 4/12/2017.
 */

public class SqlHelper extends SQLiteOpenHelper implements Serializable{


	public static final String TABLE_INFO="Info";
	private static final String DATABASE_NAME="DancerData";
	private static final int DATABASE_VERSION = 5;

//	public static final String LAST_NAME = "LastName";
//	public static final String FIRST_NAME = "FirstName";
//	public static final String MIDDLE = "Middle";
//	public static final String VENUE = "Venue";
//	public static final String TITLE = "Title";
//	public static final String PERF_DATE = "PerfDate";
//	public static final String CODE = "Code";
//	public static final String CFIRST_NAME = "CFirstName";
//	public static final String CLAST_NAME = "CLastName";
//	public static final String CMIDDLE = "CMiddle";
//	public static final String DANCE_CODE = "DanceCode";
//	public static final String CHOR_CODE = "Chore_Code";
//	public static final String PERF_DESC = "PerfDesc";

	private Context context;

	public SqlHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context=context;
	}

	public SqlHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);

	}

	public SqlHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}

	/**
	 * This only gets called if the database does not exist
	 * @param sqLiteDatabase
	 */
	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Creating database for first time");
		/*
		sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "
				+ SqlHelper.TABLE_INFO
				+ " (Code INTEGER," +
				"Venue VARCHAR," +
				"PerfDesc VARCHAR," +
				"PerfDate DATETIME," +
				"Title VARCHAR," +
				"LastName VARCHAR," +
				"FirstName VARCHAR," +
				"Middle VARCHAR," +
				"CLastName VARCHAR," +
				"CFirstName VARCHAR," +
				"CMiddle VARCHAR," +
				"DanceCode INTEGER," +
				"Chore_Code INTEGER);");
		*/

		createSqlTable(sqLiteDatabase);

		UtilsShared.AlertMessageSimple(context,"New Database Created","You need to import data - see menu option.");
		//UtilsShared.toastIt(DancerDao.context,"New database created - you need to import data", Toast.LENGTH_LONG);

	}

	/**
	 * If we change the database version above this gets called - we don't have to call this manually
	 * @param sqLiteDatabase
	 * @param oldVersion
	 * @param newVersion
	 */
	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","old version:"+oldVersion+"--new version:"+newVersion);
		//Original
		if(newVersion>1){
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_INFO);
		//onCreate(sqLiteDatabase);
			createSqlTable(sqLiteDatabase);
		}
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//We really never want to downgrade - but we have for testing
		//super.onDowngrade(db, oldVersion, newVersion);
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Downgrade:"+"old version:"+oldVersion+"--new version:"+newVersion);
	}

	private void createSqlTable(SQLiteDatabase db) {
		// Code (People), venuename,perfDesc, perfdate, title, lastname, firstname, middle,
		// clastname, cfirstname, cmiddle, dpcode, choreocode
/*

		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ SqlHelper.TABLE_INFO
				+ " (Code INTEGER," +
				"Venue VARCHAR," +
				"PerfDesc VARCHAR," +
				"PerfDate DATETIME," +
				"Title VARCHAR," +
				"LastName VARCHAR," +
				"FirstName VARCHAR," +
				"Middle VARCHAR," +
				"CLastName VARCHAR," +
				"CFirstName VARCHAR," +
				"CMiddle VARCHAR," +
				"DanceCode INTEGER," +
				"Chore_Code INTEGER,"+
				"Perf_Code INTEGER);");

*/

		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ SqlHelper.TABLE_INFO
				+ " (Code INTEGER," +
				DancerDao.VENUE+" VARCHAR," +
				DancerDao.PERF_DESC+" VARCHAR," +
				DancerDao.PERF_DATE+" DATETIME," +
				DancerDao.TITLE+" VARCHAR," +
				DancerDao.LAST_NAME+" VARCHAR," +
				DancerDao.FIRST_NAME+" VARCHAR," +
				DancerDao.MIDDLE+" VARCHAR," +
				DancerDao.CLAST_NAME+" VARCHAR," +
				DancerDao.CFIRST_NAME+" VARCHAR," +
				DancerDao.CMIDDLE+" VARCHAR," +
				DancerDao.DANCE_CODE+" INTEGER," +
				DancerDao.CHOR_CODE+" INTEGER," +
				DancerDao.PERF_CODE+" INTEGER);");

		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","We created database...");
	}

}
