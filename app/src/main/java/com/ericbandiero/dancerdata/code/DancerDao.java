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
import android.util.Log;
import android.widget.Toast;

import com.ericbandiero.dancerdata.activities.ExpandListSubclass;
import com.ericbandiero.dancerdata.dagger.DanceApp;
import com.ericbandiero.librarymain.UtilsShared;
import com.ericbandiero.librarymain.activities.Lib_Expandable_Activity;
import com.ericbandiero.librarymain.basecode.ControlStatsActivityBuilder;
import com.ericbandiero.librarymain.basecode.ControlStatsAdapterBuilder;
import com.ericbandiero.librarymain.data_classes.DataHolderTwoFields;
import com.ericbandiero.librarymain.data_classes.Lib_ExpandableDataWithIds;
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

import javax.inject.Inject;
import javax.inject.Named;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
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

	public static final String SQL_DANCERS_BY_DANCE_PIECES = "Select " +
			DancerDao.CODE +
			"," +
			DancerDao.FIRST_NAME +
			"," +
			DancerDao.LAST_NAME +
			",count(distinct " +
			DancerDao.DANCE_CODE + ") as cnt" +
			" from info group by 1,2,3,"+
			DancerDao.CODE + " order by cnt desc";

	public static final String SQL_VENUE_BY_MOST_DANCE_PIECES="Select "+
			DancerDao.VENUE+
			",count(distinct "+
			DancerDao.DANCE_CODE+") as cnt from info group by "+
			DancerDao.VENUE +" order by cnt desc";

	public static final String SQL_GIGS_BY_YEAR="Select "+ "strftime('%Y',"+DancerDao.PERF_DATE+") as year,"+
		"count(distinct "+DancerDao.PERF_CODE+")" +
		" from info" +
		" group by "+"strftime('%Y',"+DancerDao.PERF_DATE+")" +
		" order by year desc";

	public static final String SQL_VENUE_BY_PERFORMANCE_SHOOTS="Select "+
		DancerDao.VENUE+
		",count(distinct "+
		DancerDao.PERF_DATE+"||"+DancerDao.PERF_CODE+") as cnt from info group by "+
		DancerDao.VENUE +
		" having cnt>1 order by cnt desc";

	//Import location for data file that user needs to have
	private static final String WORKING_DATA_FOLDER = "/DancerData";
	private static final String DANCER_DATA_INPUT_FILE = "/dancers.txt";
	private static final long serialVersionUID = 8631832636106174063L;
	public static final int INT_MAX_FIELD_LENGTH = 30;
	public static final String SELECT_ALL_VENUE_DATA = "select PerfDate as _id,PerfDate,PerfDesc,Venue,Dance_Code,Perf_Code from Info group by PerfDate,Venue,Perf_Code order by PerfDate desc";

	private static List<Lib_ExpandableDataWithIds> listPerformances = new ArrayList<>();

	// Database fields
	private SQLiteDatabase database;
	private final SqlHelper dbHelper;

	//New comment 3
	private final Context context;

	private Context activityContext;

	@Inject
	SharedPreferences sharedPreferences;

	@Inject
	@Named(HandleAChildClick.GET_DANCE_DETAIL_FROM_CLICK)
	HandleAChildClick handleAChildClick;

	@Inject
	ControlStatsAdapterBuilder controlStatsAdapterBuilder;

	@Inject
	@Named(AppConstant.DAG_CONTROLLER_VENUE_BY_PERFORM_SHOOTS)
	ControlStatsActivityBuilder controlStatsActivityBuilderVenueCounts;

/*
	This would be lazy injection - we would need to call get.
	Since this isn't high cost we can build it right away.
	@Inject
	@Named(AppConstant.DAG_CONTROLLER_VENUE_BY_DANCE)
	Provider <ControlStatsActivityBuilder> controlStatsActivityBuilderVenueDances;
*/

	@Inject
	@Named(AppConstant.DAG_CONTROLLER_VENUE_BY_DANCE)
	ControlStatsActivityBuilder controlStatsActivityBuilderVenueDances;

	@Inject
	@Named(AppConstant.DAG_CONTROLLER_GIGS_PER_YEAR)
	ControlStatsActivityBuilder controlStatsActivityGigsByYear;

	@Inject
	@Named(AppConstant.DAG_CONTROLLER_DANCER_COUNT)
	ControlStatsActivityBuilder controlStatsActivityDancersByWorks;

	private Cursor cursorRxJava;

	private Disposable disposable;

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

		//todo Make test easier with a variable?
		//To test with emulator change condition below to be true. And change flag in readfile
		if (1==1 || checkIfInputFileExists()) {
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
			checkDataIsOpen();
			database.execSQL("delete from " + tableNameToDrop);
			database.close();
		} catch (SQLiteException e) {
			if (AppConstant.DEBUG)
				Log.e(this.getClass().getSimpleName() + ">", "Could not delete from table:" + e.getMessage());
		}
	}


	public Single<Cursor> runRawQueryCursor(String sql){
		checkDataIsOpen();
		return Single.fromCallable(() -> {
			System.out.println("Thread we are running on from runRawQueryCursor:" + Thread.currentThread().getName());
			return database.rawQuery(sql, null);
		})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}


	public void runRawQueryWithRxJava(String sql, IProcessCursorAble iProcessCursorAble) {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName() + ">", "Sql passed in:" + sql);
		checkDataIsOpen();
		Observable<Cursor> cursorObservable = Observable.fromCallable(() -> {
			System.out.println("Thread we are running on from rxRawQueryWithRxJava:" + Thread.currentThread().getName());
			return database.rawQuery(sql, null);
		})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());

		cursorObservable.subscribe(cursor -> {
			iProcessCursorAble.processCursor(cursor);
		});
		cursorObservable.unsubscribeOn(Schedulers.io());
	}

	public Observable<List<DataHolderTwoFields>> getListDataHolderTwoFieldsFromCursorWithRxJava(String sqlParam, IProcessCursorToDataHolderList processCursorToDataHolderList) {
		checkDataIsOpen();
		return Observable.fromCallable(() -> {
			System.out.println("Thread we are running on from getListDataHolderTwoFieldsFromCursorWithRxJava:" + Thread.currentThread().getName());
			return database.rawQuery(sqlParam, null);
		}).map(new Function<Cursor, List<DataHolderTwoFields>>() {

			/**
			 * Apply some calculation to the input value and return some other value.
			 *
			 * @param cursor the input value
			 * @return the output value
			 * @throws Exception on error
			 */
			@Override
			public List<DataHolderTwoFields> apply(Cursor cursor) throws Exception {
				return processCursorToDataHolderList.createListFromCursor(cursor);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	public Cursor runRawQueryMainThread(String sql){
		Cursor cursor=null;

		try {
			if (database == null || !database.isOpen()) {
				open();
			}
			//Sql cursor never comes back null
			cursor = database.rawQuery(sql, null);

			//if (cursor != null &cursor.isBeforeFirst()) {
			//Return true or fa;se = no error
			//cursor.moveToFirst();
			//}
			return cursor;
		}
		catch(SQLiteException ex)
		{
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Error!");
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Cursor is null?"+cursor==null?"Null":"Not null");
			//UtilsShared.AlertMessageSimple(AppConstant.CONTEXT, "Error getting data!", "Data error:" + ex.getMessage());
			return cursor;
		}
	}

	private void checkDataIsOpen() {
		try {
			if (database == null || !database.isOpen()) {
				open();
			}
		} catch (SQLiteException ex) {
			if (AppConstant.DEBUG)
				Log.d(this.getClass().getSimpleName() + ">", "Error:" + ex.getMessage());
		}
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
			//Test of from assets - set to true
			if (true) {
				//toastIt(context,"reading a processCursor file...",Toast.LENGTH_SHORT);
				//UtilsShared.alertMessageSimple(context, "Import Message", "Using processCursor data");
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
					if (AppConstant.DEBUG) Log.e(this.getClass().getSimpleName()+">","Error:"+e.getLocalizedMessage());
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
				if (AppConstant.DEBUG) Log.e(this.getClass().getSimpleName()+">","Error:"+e.getLocalizedMessage());
				if (AppConstant.DEBUG) Log.e(this.getClass().getSimpleName()+">","Error:"+e.getLocalizedMessage());
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		Calendar end = Calendar.getInstance();
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
		UtilsShared.alertMessageSimple(activityContext, "Database import results.",
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

	private void prepDataPerformance(String performanceCode,List<Lib_ExpandableDataWithIds> listData) {
		if (AppConstant.DEBUG) Log.d(new Object() {
		}.getClass().getEnclosingClass() + ">", "Performance code passed in:" + performanceCode);
		if (AppConstant.DEBUG) Log.d(new Object() {
		}.getClass().getEnclosingClass() + ">", "Start time performance data:" + new Date().toString());

		//List<Lib_ExpandableDataWithIds> listData = new ArrayList<>();

		//We already have gotten the full list once

		if (performanceCode.equals("-1") & !listPerformances.isEmpty()) {
			startPerformanceActivityNew(listPerformances);
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","We already cached data - not re-running query");
			return;
		}

		String whereClause = !performanceCode.equals("-1") ? " where Perf_Code =" + performanceCode : "";

		Single<Cursor> single = runRawQueryCursor("select PerfDate as _id," +
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

		single.subscribeWith(new DisposableSingleObserver<Cursor>() {
			/**
			 * Notifies the SingleObserver with a single item and that the {@link Single} has finished sending
			 * push-based notifications.
			 * <p>
			 * The {@link Single} will not call this method if it calls {@link #onError}.
			 *
			 * @param cursor the item emitted by the Single
			 */
			@Override
			public void onSuccess(Cursor cursor) {
				SortedSet<String> performances = new TreeSet<>(Collections.<String>reverseOrder());

				//First get venues
				while (cursor.moveToNext()) {
					performances.add(cursor.getString(1) + ":" + cursor.getString(2)+":"+cursor.getString(6));
					Lib_ExpandableDataWithIds lib_expandableDataWithIds = new Lib_ExpandableDataWithIds(cursor.getString(1) + ":" + cursor.getString(2)+":"+cursor.getString(6), cursor.getString(5));
					lib_expandableDataWithIds.setAnyObject(cursor.getString(4));//Dance code
					//listData.add(new Lib_ExpandableDataWithIds(cursor.getString(3), cursor.getString(1) + "---" + cursor.getString(2)));
					listData.add(lib_expandableDataWithIds);
					//if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName() + ">", "Data performance:" + cursor.getString(1));
				}


				for (String performance : performances) {
					listData.add(new Lib_ExpandableDataWithIds(performance));
				}

				cursor.close();
				if (AppConstant.DEBUG) Log.d(new Object() {
				}.getClass().getEnclosingClass() + ">", "End time performance data:" + new Date().toString());

				//Saving entire list for first time.
				if (performanceCode.equals("-1") & listPerformances.isEmpty()) {
					listPerformances = listData;
				}
				startPerformanceActivityNew(listData);
				dispose();
			}

			/**
			 * Notifies the SingleObserver that the {@link Single} has experienced an error condition.
			 * <p>
			 * If the {@link Single} calls this method, it will not thereafter call {@link #onSuccess}.
			 *
			 * @param e the exception encountered by the Single
			 */
			@Override
			public void onError(Throwable e) {
			if (AppConstant.DEBUG) Log.e(this.getClass().getSimpleName()+">",e.getMessage());
			dispose();
			}
		});
	}


	private void startPerformanceActivityNew(List<Lib_ExpandableDataWithIds> listData){
		if (AppConstant.DEBUG) Log.d(new Object() {
		}.getClass().getEnclosingClass() + ">", "Start time performance activity:" + new Date().toString());
		IPrepDataExpandableList prepareCursor = new PrepareCursorData(listData);
		Intent i = new Intent(context, ExpandListSubclass.class);
		i.putExtra(Lib_Expandable_Activity.EXTRA_TITLE, "Performances");
		i.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE, prepareCursor);
		i.putExtra(Lib_Expandable_Activity.EXTRA_INTERFACE_HANDLE_CHILD_CLICK, handleAChildClick);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
		if (AppConstant.DEBUG) Log.d(new Object() {
		}.getClass().getEnclosingClass() + ">", "End time performance activity called:" + new Date().toString());
	}

	public void getVenueData(Activity activity, HandleAChildClick handleAChildClickVenues) {
		final List<Lib_ExpandableDataWithIds>listVenues=new ArrayList<>();

		if (AppConstant.DEBUG)
			Log.d(this.getClass().getSimpleName() + ">", "Prepping venue data...");
		Single<Cursor> single = runRawQueryCursor(SELECT_ALL_VENUE_DATA);

		single.subscribeWith(new DisposableSingleObserver<Cursor>() {
			@Override
			public void onSuccess(Cursor cursor) {
				SortedSet<String> venues = new TreeSet<>();

				//First get venues
				while (cursor.moveToNext()) {
					venues.add(cursor.getString(3));
					Lib_ExpandableDataWithIds lib_expandableDataWithIds = new Lib_ExpandableDataWithIds(cursor.getString(3), cursor.getString(1) + "---" + cursor.getString(2));
					lib_expandableDataWithIds.setAnyObject(cursor.getString(5));
					//listData.add(new Lib_ExpandableDataWithIds(cursor.getString(3), cursor.getString(1) + "---" + cursor.getString(2)));
					listVenues.add(lib_expandableDataWithIds);
					if (AppConstant.DEBUG)
						Log.d(this.getClass().getSimpleName() + ">", "Data venue:" + cursor.getString(1));
				}

				for (String venue : venues) {
					listVenues.add(new Lib_ExpandableDataWithIds(venue));
				}
				cursor.close();
				Intent intent = new Intent(activity, ExpandListSubclass.class);
				IPrepDataExpandableList prepareCursor = new PrepareCursorData(listVenues);
				intent.putExtra(Lib_Expandable_Activity.EXTRA_TITLE, "Venues");
				intent.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE, prepareCursor);
				intent.putExtra(Lib_Expandable_Activity.EXTRA_INTERFACE_HANDLE_CHILD_CLICK, handleAChildClickVenues);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				dispose();
			}
			@Override
			public void onError(Throwable e) {
				dispose();
			}
		});
	}

	public void prepPerformanceActivity() {
		prepPerformanceActivity("-1");
	}

	public void prepPerformanceActivity(String performanceCode) {

		List<Lib_ExpandableDataWithIds> listData =new ArrayList<>();

		//The listData gets changed by the called method.
		prepDataPerformance(performanceCode,listData);
	}

	//TODO Try to make this all run in io thread.
	public void createIntentForPerformanceByVenueName(String venueName) {

		List<Lib_ExpandableDataWithIds> listData = new ArrayList<>();

		String whereClause = " where venue =" + Utility.quote(venueName);

		Single<Cursor> single = runRawQueryCursor("select PerfDate as _id," +
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

		single.subscribeWith(new DisposableSingleObserver<Cursor>() {
			@Override
			public void onSuccess(Cursor cursor) {
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

				IPrepDataExpandableList prepareCursor = new PrepareCursorData(listData);
				Intent i = new Intent(context, ExpandListSubclass.class);
			//	i.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE,iPrepDataExpandableList);
			//	i.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE,prepDataExpandableList);
				i.putExtra(Lib_Expandable_Activity.EXTRA_TITLE, "Performances");
				i.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE, prepareCursor);

				i.putExtra(Lib_Expandable_Activity.EXTRA_INTERFACE_HANDLE_CHILD_CLICK, handleAChildClick);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
				dispose();

			}
			@Override
			public void onError(Throwable e) {
				if (AppConstant.DEBUG) Log.e(this.getClass().getSimpleName()+">",e.getMessage());
			}
		});
	}

	private void startPerformanceActivity(){

	}
	public void getPerformanceForAVenue(String venueName) {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Getting performance for venue...");
		createIntentForPerformanceByVenueName(venueName);
		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//context.startActivity(intent);
	}

	public boolean isTableEmptyNew(Cursor cursor){
		boolean isEmpty;
//		if (cursor == null) {
//			return false;
//		}
		boolean wasMoved = cursor.moveToFirst();
		isEmpty = !wasMoved;
		cursor.close();
		return isEmpty;
	}

	public Context getActivityContext() {
		return activityContext;
	}

	public void setActivityContext(Context activityContext) {
		this.activityContext = activityContext;
	}

	public void runDancerCountsFromRxJava(Context context1) {
		disposable = getListDataHolderTwoFieldsFromCursorWithRxJava(SQL_DANCERS_BY_DANCE_PIECES, cursor -> {
			List<DataHolderTwoFields> list = new ArrayList<>();
			System.out.println("Running on thread:" + Thread.currentThread().getName());
			while (cursor.moveToNext()) {
				//System.out.println("Cursor field 1"+cursor.getString(1));
				String fieldOne = StatData.getSubStringForField(cursor.getString(2).trim() + "," + cursor.getString(1).trim(), INT_MAX_FIELD_LENGTH);
				DataHolderTwoFields dataHolderTwoFields = new DataHolderTwoFields(fieldOne, cursor.getString(3).trim());
				dataHolderTwoFields.setId(cursor.getString(0)); //Want this for click event.
				list.add(dataHolderTwoFields);
			}
			return list;
		}).subscribe(list -> {
			controlStatsActivityDancersByWorks.setDataHolderTwoFieldsList(list);
			UtilsShared.startStatActivity(context1,controlStatsActivityDancersByWorks,controlStatsAdapterBuilder);
			disposable.dispose();
		});
	}

	public void getMostPiecesShotAtVenue(Context contextParam) {
		int maxLengthOfVenueName = INT_MAX_FIELD_LENGTH;
		disposable= getListDataHolderTwoFieldsFromCursorWithRxJava(SQL_VENUE_BY_MOST_DANCE_PIECES, cursor -> {
			List<DataHolderTwoFields> list = new ArrayList<>();
			System.out.println("Running on thread:" + Thread.currentThread().getName());
			while (cursor.moveToNext()) {
				String venueName = cursor.getString(0).trim();

				DataHolderTwoFields dataHolderTwoFields = new DataHolderTwoFields(venueName.substring(0, (venueName.length() > maxLengthOfVenueName ? maxLengthOfVenueName : venueName.length())) + ":", String.valueOf(cursor.getString(1)));
				dataHolderTwoFields.setId(venueName); //Want this for click event.
				list.add(dataHolderTwoFields);
			}
			return list;
		}).subscribe(list -> {
			controlStatsActivityBuilderVenueDances.setDataHolderTwoFieldsList(list);
			UtilsShared.startStatActivity(contextParam,controlStatsActivityBuilderVenueDances,controlStatsAdapterBuilder);
			disposable.dispose();
		});
	}

	public void getGigsByYear(Context contextParam) {
		disposable= getListDataHolderTwoFieldsFromCursorWithRxJava(SQL_GIGS_BY_YEAR, cursor -> {
			List<DataHolderTwoFields> list = new ArrayList<>();
			System.out.println("Running on thread:" + Thread.currentThread().getName());
			while (cursor.moveToNext()) {
				list.add(new DataHolderTwoFields(cursor.getString(0),cursor.getString(1)));
			}
			return list;
		}).subscribe(list -> {
			controlStatsActivityGigsByYear.setDataHolderTwoFieldsList(list);
			UtilsShared.startStatActivity(contextParam,controlStatsActivityGigsByYear,controlStatsAdapterBuilder);
			disposable.dispose();
		});
	}

	public void getMostShotVenue(Context contextParam,boolean rollUp) {
		final int maxLengthOfVenueName=rollUp?10: INT_MAX_FIELD_LENGTH;
		//final HandleClickForRecyclerVenueOrDancer handleClickForRecyclerVenueOrDancer=new HandleClickForRecyclerVenueOrDancer(HandleClickForRecyclerVenueOrDancer.VENUE_COUNT);

		disposable= getListDataHolderTwoFieldsFromCursorWithRxJava(SQL_VENUE_BY_PERFORMANCE_SHOOTS,new IProcessCursorToDataHolderList() {
			@Override
			public List<DataHolderTwoFields> createListFromCursor(Cursor cursor) {
				List<DataHolderTwoFields> list = new ArrayList<>();
				System.out.println("Running on thread:" + Thread.currentThread().getName());
				while (cursor.moveToNext()) {
					if (rollUp){
						if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Waiting to use this...");
					}
					else {
						String venueName = cursor.getString(0).trim();
						DataHolderTwoFields dataHolderTwoFields = new DataHolderTwoFields(venueName.substring(0, (venueName.length() > maxLengthOfVenueName ? maxLengthOfVenueName : venueName.length())) + ":", String.valueOf(cursor.getString(1)));
						dataHolderTwoFields.setId(venueName); //Want this for click event.
						list.add(dataHolderTwoFields);
					}
					if (rollUp){
						break;
					}
				}

				return list;
			}
		}).subscribe(list -> {
			controlStatsActivityBuilderVenueCounts.setDataHolderTwoFieldsList(list);
			//controlStatsActivityBuilderVenueCounts.setHandleRecyclerViewClicks(handleClickForRecyclerVenueOrDancer);
			UtilsShared.startStatActivity(contextParam,controlStatsActivityBuilderVenueCounts,controlStatsAdapterBuilder);
			disposable.dispose();
		});
	}
}
