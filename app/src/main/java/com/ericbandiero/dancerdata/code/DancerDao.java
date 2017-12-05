package com.ericbandiero.dancerdata.code;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.ericbandiero.dancerdata.activities.ExpandListSubclass;
import com.ericbandiero.dancerdata.dagger.DanceApp;
import com.ericbandiero.librarymain.Lib_Expandable_Activity;
import com.ericbandiero.librarymain.UtilsShared;
import com.ericbandiero.librarymain.data_classes.Lib_ExpandableDataWithIds;
import com.ericbandiero.librarymain.interfaces.IHandleChildClicksExpandableIds;
import com.ericbandiero.librarymain.interfaces.IPrepDataExpandableList;
import com.ericbandiero.myframework.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.ericbandiero.librarymain.UtilsShared.toastIt;

/**
 * Routines to access database
 * Created by ${"Eric Bandiero"} on 4/10/2017.
 */


public class DancerDao implements Serializable {

	public static final String LAST_NAME = "LastName";
	public static final String FIRST_NAME = "FirstName";
	public static final String MIDDLE = "Middle";
	public static final String VENUE = "Venue";
	public static final String TITLE = "Title";
	public static final String PERF_DATE = "PerfDate";
	public static final String CODE = "Code";
	public static final String CFIRST_NAME = "CFirstName";
	public static final String CLAST_NAME = "CLastName";
	public static final String CMIDDLE = "CMiddle";
	public static final String DANCE_CODE = "Dance_Code";
	public static final String CHOR_CODE = "Chore_Code";
	public static final String PERF_DESC = "PerfDesc";
	public static final String PERF_CODE = "Perf_Code";

	//Import location for data file that user needs to have
	private static final String WORKING_DATA_FOLDER = "/DancerData";
	private static final String DANCER_DATA_INPUT_FILE = "/dancers.txt";
	private static final long serialVersionUID = 8631832636106174063L;

	private static List<Lib_ExpandableDataWithIds> listPerformances = new ArrayList<>();

	// Database fields
	private SQLiteDatabase database;
	private SqlHelper dbHelper;

	//New comment 3
	private Context context;

	private Context activityContext;

	@Inject
	SharedPreferences sharedPreferences;

	@Inject
	@Named(HandleAChildClick.GET_DANCE_DETAIL_FROM_CLICK)
	HandleAChildClick handleAChildClick;

	private Cursor cursorRxJava;

	public DancerDao(Context context) {
		//Setup
		this.context = context;
		//Dagger
		DanceApp.app().basicComponent().inject(this);

		dbHelper = new SqlHelper(context);
		//We call this to make sure onCreate gets called if database was never created
		dbHelper.getWritableDatabase();
	}

	public void open() throws SQLException {
		if (database == null || !database.isOpen()) {
			database = dbHelper.getWritableDatabase();
		}
	}

	public void close() {
		//dbHelper.close();
		database.close();
	}

	public SQLiteDatabase getDataBaseRead() {
		if (database == null || !database.isOpen()) {
			open();
		}
		return database;
	}

	public void importData(Context context_activity) {
		//TODO Add option to get from assets for testing
		activityContext = context_activity;

		if (checkIfInputFileExists()) {
			//open();
			deleteAllFromTable(SqlHelper.MAIN_TABLE_NAME);
			//dbHelper.createSqlTable();
			open();
			readDataFile(database);
			close();
		} else {
			String info = "You need to have the data file " + DANCER_DATA_INPUT_FILE +
					" in directory " + WORKING_DATA_FOLDER;

			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName() + ">", info);
			toastIt(context, info, Toast.LENGTH_LONG);
		}
	}

	private void deleteAllFromTable(String tableNameToDrop) {
		//Drop table
		try {
			if (database == null || !database.isOpen()) {
				open();
			}
			database.execSQL("delete from " + tableNameToDrop);
			database.close();
		} catch (SQLiteException e) {
			if (AppConstant.DEBUG)
				Log.e(this.getClass().getSimpleName() + ">", "Could not delete from table:" + e.getMessage());
		}
	}

	/*
		This isn't the greatest. We do run the query off the UI thread, but the block the UI thread by using blockingGet().
	 */
	public Cursor runRawQuery(String sql) {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName() + ">", "Sql passed in:" + sql);
		Cursor cursor = null;

		try {
			if (database == null || !database.isOpen()) {
				open();
			}

			Single<Cursor> ob = Single.fromCallable(new Callable<Cursor>() {
				@Override
				public Cursor call() throws Exception {
					System.out.println("Thread we are running on:" + Thread.currentThread().getName());
					return database.rawQuery(sql, null);
				}
			}).subscribeOn(Schedulers.io());

			cursorRxJava = ob.blockingGet();
			return cursorRxJava;
		} catch (SQLiteException ex) {
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName() + ">", "Error!");
			//UtilsShared.AlertMessageSimple(AppConstant.CONTEXT, "Error getting data!", "Data error:" + ex.getMessage());
			if (AppConstant.DEBUG)
				Log.d(this.getClass().getSimpleName() + ">", "Error getting data!" + "Data error:" + ex.getMessage());
			return cursorRxJava;
		}
	}

	public void runRawQueryWithRxJava(String sql, IProcessCursorAble iProcessCursorAble) {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Sql passed in:"+sql);
		try {
			if (database == null || !database.isOpen()) {
				open();
			}
		} catch (SQLiteException ex) {
				if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Error:"+ex.getMessage());
		}
		Observable<Cursor> cursorObservable = getCursor(sql).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
		cursorObservable.subscribe(cursor -> {
			iProcessCursorAble.processCursor(cursor);});
	}

	private void doSomethingWithCursor(Cursor c) {
		while (c.moveToNext()) {
			System.out.println("Dance last name:"+c.getString(c.getColumnIndex(DancerDao.LAST_NAME)));
		}
		c.close();
	}

	public Observable<Cursor> getCursor(String sql) {
		return Observable.defer(() -> {
			System.out.println(Thread.currentThread().getName());
			return Observable.just(database.rawQuery(sql, null));
		});
	}

	private void readDataFile(SQLiteDatabase db) {

		int permissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.READ_EXTERNAL_STORAGE);

		if (AppConstant.DEBUG)
			Log.d(this.getClass().getSimpleName() + ">", "Permission status:" + permissionCheck);

		if (permissionCheck == -1) {
			ActivityCompat.requestPermissions((Activity) context,
					new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
					1);
		}

		int cnt = 0;
		BufferedReader br = null;
		Calendar start = Calendar.getInstance();

		// *Don't hard code "/sdcard"
		File sdcard = Environment.getExternalStorageDirectory();

		InputStream dancerDataAssets = null;

		// Get the text file
		File file = new File(sdcard, WORKING_DATA_FOLDER
				+ DANCER_DATA_INPUT_FILE);

		if (!db.isOpen()) {
			open();
		}

		//Temp fix for AS bug reporting error.
		String values = " Values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		SQLiteStatement stmt = db.compileStatement("INSERT INTO "
				+ SqlHelper.MAIN_TABLE_NAME + values);

		db.beginTransaction();
		// Read text from file
		try {
			//Test of from assets
			if (false) {
				//toastIt(context,"reading a processCursor file...",Toast.LENGTH_SHORT);
				UtilsShared.AlertMessageSimple(context, "Import Message", "Using processCursor data");
				AssetManager am = context.getAssets();
				dancerDataAssets = am.open("dancers.txt", AssetManager.ACCESS_BUFFER);
				br = new BufferedReader(new InputStreamReader(dancerDataAssets));
			} else {
				br = new BufferedReader(new FileReader(file));
			}

			if (AppConstant.DEBUG)
				Log.d(this.getClass().getSimpleName() + ">", "File name being read:" + br.toString());
			String line;

			while ((line = br.readLine()) != null) {
				//Log.d(TAG,line);
				String[] columns = line.replace("'", "").replace("\\", "'")
						.split("\\^");
				//Log.d(TAG,"Length:"+columns.length);
				//Log.d(TAG,"Length:"+columns[11]);
				//Log.d(TAG,"Length:"+columns[12]);


				stmt.bindString(1, columns[0]);
				stmt.bindString(2, columns[1]);
				stmt.bindString(3, columns[2]);
				stmt.bindString(4, columns[3]);
				stmt.bindString(5, columns[4]);
				stmt.bindString(6, columns[5]);
				stmt.bindString(7, columns[6]);
				stmt.bindString(8, columns[7]);
				stmt.bindString(9, columns[8]);
				stmt.bindString(10, columns[9]);
				stmt.bindString(11, columns[10]);
				stmt.bindString(12, columns[11]);
				stmt.bindString(13, columns[12]);
				stmt.bindString(14, columns[13]);

				cnt++;

				try {
					stmt.execute();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			// You'll need to add proper error handling here
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		Calendar end = Calendar.getInstance();
		if (AppConstant.DEBUG)
			Log.d(this.getClass().getSimpleName() + ">", "Time start:" + start.getTime());
		if (AppConstant.DEBUG)
			Log.d(this.getClass().getSimpleName() + ">", "Time end  :" + end.getTime());

		String rowsAttempted = "Rows of data attempted:" + cnt;
		if (AppConstant.DEBUG)
			Log.d(this.getClass().getSimpleName() + ">", "Input file was imported!");
		if (AppConstant.DEBUG)
			Log.d(this.getClass().getSimpleName() + ">", "Rows of data attempted:" + cnt);
		Cursor cursor = db.rawQuery("select count(*) as cnt from "
				+ SqlHelper.MAIN_TABLE_NAME, null);
		cursor.moveToFirst();
		if (AppConstant.DEBUG)
			Log.d(this.getClass().getSimpleName() + ">", "Rows of data in database:" + cursor.getInt(0));
		String rowsImported = "Rows of data imported:" + cursor.getInt(0);
		cursor.close();
//		AndroidUtility.AlertMessageSimple(context, "Database import results.",
//				rowsAttempted + "\n" + rowsImported);
		UtilsShared.AlertMessageSimple(activityContext, "Database import results.",
				rowsAttempted + "\n" + rowsImported);
	}

	public boolean createMyWorkingDirectory() {
		// Get the name of the folder we want to create
		File folder = new File(Environment.getExternalStorageDirectory()
				+ DancerDao.WORKING_DATA_FOLDER);
		if (AppConstant.DEBUG)
			Log.d(this.getClass().getSimpleName() + ">", "Folder where we will directory:" + folder.getAbsolutePath().toLowerCase());
		boolean success = true;

		// If it doesn't exist we try to make it.
		if (!folder.exists()) {
			if (AppConstant.DEBUG)
				Log.d(this.getClass().getSimpleName() + ">", "Need to create directory:" + folder);
			int permissionCheck = ContextCompat.checkSelfPermission(context,
					Manifest.permission.WRITE_EXTERNAL_STORAGE);
			if (AppConstant.DEBUG)
				Log.d(this.getClass().getSimpleName() + ">", "Permission check:" + permissionCheck);

			if (permissionCheck == -1) {
				ActivityCompat.requestPermissions((Activity) context,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						1);
			}

			success = folder.mkdir();
		} else {
			if (AppConstant.DEBUG)
				Log.d(this.getClass().getSimpleName() + ">", "Folder already exists:" + folder.getAbsolutePath());
		}
		if (!success) {
			toastIt(context, "Folder " + DancerDao.WORKING_DATA_FOLDER + " cannot be created", Toast.LENGTH_LONG);
			if (AppConstant.DEBUG)
				Log.d(this.getClass().getSimpleName() + ">", "Directory " + folder + " could not be created");
		}
		return success;
	}

	private boolean checkIfInputFileExists() {
		boolean fileExists = false;

		File file = new File(Environment.getExternalStorageDirectory()
				+ DancerDao.WORKING_DATA_FOLDER + DancerDao.DANCER_DATA_INPUT_FILE);
		if (file.exists()) {
			fileExists = true;
		} else {
			if (AppConstant.DEBUG)
				Log.d(this.getClass().getSimpleName() + ">", "File doesn't exist:" + file);
			toastIt(context, file + " doesn't exists", Toast.LENGTH_LONG);
		}

		return fileExists;
	}

	private List<Lib_ExpandableDataWithIds> prepDataPerformance(String performanceCode) {
		if (AppConstant.DEBUG) Log.d(new Object() {
		}.getClass().getEnclosingClass() + ">", "Performance code passed in:" + performanceCode);
		if (AppConstant.DEBUG) Log.d(new Object() {
		}.getClass().getEnclosingClass() + ">", "Start time:" + new Date().toString());

		List<Lib_ExpandableDataWithIds> listData = new ArrayList<>();

		//We already have gotten the full list once
		if (performanceCode.equals("-1") & !listPerformances.isEmpty()) {
			return listPerformances;
		}

		String whereClause = !performanceCode.equals("-1") ? " where Perf_Code =" + performanceCode : "";

		final Cursor cursor = runRawQuery(
				"select PerfDate as _id," +
						"PerfDate," +
						"PerfDesc," +
						"Venue," +
						"Dance_Code," +
						"title," +
						"Perf_Code" +
						" from Info " +
						whereClause +
						" group by Perf_Code,Dance_code " +
						" order by PerfDate desc");
		//this.cursor = db.rawQuery("select PerfDate as _id,PerfDate,PerfDesc,Venue from Info group by PerfDate,Venue order by PerfDate desc", null);


		SortedSet<String> performances = new TreeSet<>(Collections.<String>reverseOrder());

		//First get venues
		while (cursor.moveToNext()) {
			if (!performances.add(cursor.getString(1) + ":" + cursor.getString(2))) {
				if (AppConstant.DEBUG)
					Log.d(this.getClass().getSimpleName() + ">", "Duplicate:" + cursor.getString(2));
			}

			Lib_ExpandableDataWithIds lib_expandableDataWithIds = new Lib_ExpandableDataWithIds(cursor.getString(1) + ":" + cursor.getString(2), cursor.getString(5));
			lib_expandableDataWithIds.setAnyObject(cursor.getString(4));//Dance code
			//listData.add(new Lib_ExpandableDataWithIds(cursor.getString(3), cursor.getString(1) + "---" + cursor.getString(2)));
			listData.add(lib_expandableDataWithIds);
			if (AppConstant.DEBUG)
				Log.d(this.getClass().getSimpleName() + ">", "Data performance:" + cursor.getString(1));
		}


		for (String performance : performances) {
			listData.add(new Lib_ExpandableDataWithIds(performance));
		}

		cursor.close();
		if (AppConstant.DEBUG) Log.d(new Object() {
		}.getClass().getEnclosingClass() + ">", "End time:" + new Date().toString());

		//Saving entire list for first time.
		if (performanceCode.equals("-1") & listPerformances.isEmpty()) {
			listPerformances = listData;
		}

		return listData;

	}


	private List<Lib_ExpandableDataWithIds> prepDataPerformance() {
		return prepDataPerformance("-1");
	}

	public List<Lib_ExpandableDataWithIds> prepDataVenue() {
		if (AppConstant.DEBUG)
			Log.d(this.getClass().getSimpleName() + ">", "Prepping venue data...");
		final Cursor cursor = runRawQuery("select PerfDate as _id,PerfDate,PerfDesc,Venue,Dance_Code,Perf_Code from Info group by PerfDate,Venue,Perf_Code order by PerfDate desc");
		//this.cursor = db.rawQuery("select PerfDate as _id,PerfDate,PerfDesc,Venue from Info group by PerfDate,Venue order by PerfDate desc", null);

		List<Lib_ExpandableDataWithIds> listData = new ArrayList<>();

		SortedSet<String> venues = new TreeSet<>();

		//First get venues
		while (cursor.moveToNext()) {
			venues.add(cursor.getString(3));
			Lib_ExpandableDataWithIds lib_expandableDataWithIds = new Lib_ExpandableDataWithIds(cursor.getString(3), cursor.getString(1) + "---" + cursor.getString(2));
			lib_expandableDataWithIds.setAnyObject(cursor.getString(5));
			//listData.add(new Lib_ExpandableDataWithIds(cursor.getString(3), cursor.getString(1) + "---" + cursor.getString(2)));
			listData.add(lib_expandableDataWithIds);
			if (AppConstant.DEBUG)
				Log.d(this.getClass().getSimpleName() + ">", "Data venue:" + cursor.getString(1));
		}

		for (String venue : venues) {
			listData.add(new Lib_ExpandableDataWithIds(venue));
		}

		cursor.close();
		return listData;
	}

	public Intent prepPerformanceActivity() {
		return prepPerformanceActivity("-1");
	}

	public Intent prepPerformanceActivity(String performanceCode) {

		List<Lib_ExpandableDataWithIds> listData = prepDataPerformance(performanceCode);

		int size = 0;

		for (Lib_ExpandableDataWithIds lib_expandableDataWithIds : listData) {
			if (lib_expandableDataWithIds.getTextStringChild() == null) {
				size++;
			}
		}


		IPrepDataExpandableList prepareCursor = new PrepareCursorData(listData);

		//HandleAChildClick handleAChildClick = new HandleAChildClick(HandleAChildClick.PERFORMANCE_CLICK);

		IHandleChildClicksExpandableIds ih = new IHandleChildClicksExpandableIds() {
			@Override
			public void handleClicks(Context context, Lib_ExpandableDataWithIds lib_expandableDataWithIds, Lib_ExpandableDataWithIds lib_expandableDataWithIds1) {
				if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName() + ">", "Hello");
			}
		};

		//Intent i=new Intent(this, Lib_Expandable_Activity.class);
		Intent i = new Intent(context, ExpandListSubclass.class);
//			i.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE,iPrepDataExpandableList);
//			i.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE,prepDataExpandableList);
		i.putExtra(Lib_Expandable_Activity.EXTRA_TITLE, "Performances:" + size);

		i.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE, prepareCursor);

		i.putExtra(Lib_Expandable_Activity.EXTRA_INTERFACE_HANDLE_CHILD_CLICK, handleAChildClick);
		return i;
		//	i.putExtra(Lib_Expandable_Activity.EXTRA_INTERFACE_HANDLE_CHILD_CLICK, ih);
		//Test comment
		//Test 2
	}


	public Intent createIntentForPerformanceByVenueName(String venueName) {

		List<Lib_ExpandableDataWithIds> listData = new ArrayList<>();

		String whereClause = " where venue =" + Utility.quote(venueName);

		final Cursor cursor = runRawQuery(
				"select PerfDate as _id," +
						"PerfDate," +
						"PerfDesc," +
						"Venue," +
						"Dance_Code," +
						"title," +
						"Perf_Code" +
						" from Info " +
						whereClause +
						" group by Perf_Code,Dance_code " +
						" order by PerfDate desc");


		SortedSet<String> performances = new TreeSet<>(Collections.<String>reverseOrder());

		//First get venues
		while (cursor.moveToNext()) {
			if (!performances.add(cursor.getString(1) + ":" + cursor.getString(2))) {
				if (AppConstant.DEBUG)
					Log.d(this.getClass().getSimpleName() + ">", "Duplicate:" + cursor.getString(2));
			}

			Lib_ExpandableDataWithIds lib_expandableDataWithIds = new Lib_ExpandableDataWithIds(cursor.getString(1) + ":" + cursor.getString(2), cursor.getString(5));
			lib_expandableDataWithIds.setAnyObject(cursor.getString(4));//Dance code
			//listData.add(new Lib_ExpandableDataWithIds(cursor.getString(3), cursor.getString(1) + "---" + cursor.getString(2)));
			listData.add(lib_expandableDataWithIds);
			if (AppConstant.DEBUG)
				Log.d(this.getClass().getSimpleName() + ">", "Data performance:" + cursor.getString(1));
		}


		for (String performance : performances) {
			listData.add(new Lib_ExpandableDataWithIds(performance));
		}


		//==================
		//List<Lib_ExpandableDataWithIds> listData=prepDataPerformance(performanceCode);

		int size = 0;

		for (Lib_ExpandableDataWithIds lib_expandableDataWithIds : listData) {
			if (lib_expandableDataWithIds.getTextStringChild() == null) {
				size++;
			}
		}


		IPrepDataExpandableList prepareCursor = new PrepareCursorData(listData);

		//HandleAChildClick handleAChildClick = new HandleAChildClick(HandleAChildClick.PERFORMANCE_CLICK);

		IHandleChildClicksExpandableIds ih = new IHandleChildClicksExpandableIds() {
			@Override
			public void handleClicks(Context context, Lib_ExpandableDataWithIds lib_expandableDataWithIds, Lib_ExpandableDataWithIds lib_expandableDataWithIds1) {
				if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName() + ">", "Hello");
			}
		};

		//Intent i=new Intent(this, Lib_Expandable_Activity.class);
		Intent i = new Intent(context, ExpandListSubclass.class);
//			i.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE,iPrepDataExpandableList);
//			i.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE,prepDataExpandableList);
		i.putExtra(Lib_Expandable_Activity.EXTRA_TITLE, "Performances:" + size);

		i.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE, prepareCursor);

		i.putExtra(Lib_Expandable_Activity.EXTRA_INTERFACE_HANDLE_CHILD_CLICK, handleAChildClick);
		return i;
		//	i.putExtra(Lib_Expandable_Activity.EXTRA_INTERFACE_HANDLE_CHILD_CLICK, ih);
		//Test comment
		//Test 2
	}

	public void getPerformanceForAVenue(String venueName) {
		Intent intent = createIntentForPerformanceByVenueName(venueName);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public boolean isTableEmpty(String table_name) {
		if (AppConstant.DEBUG)
			Log.d(this.getClass().getSimpleName() + ">", "Checking if table " + table_name + " is empty.");
		boolean isEmpty;
		Cursor cursor = runRawQuery("Select count(*) from " + table_name);
		if (cursor == null) {
			return false;
		}
		cursor.moveToFirst();
		isEmpty = cursor.getInt(0) == 0;
		cursor.close();
		return isEmpty;
	}

	public Context getActivityContext() {
		return activityContext;
	}

	public void setActivityContext(Context activityContext) {
		this.activityContext = activityContext;
	}
}
