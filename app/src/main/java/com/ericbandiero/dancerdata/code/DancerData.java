package com.ericbandiero.dancerdata.code;

import java.util.Arrays;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DancerData {

	private static final String TAG ="DancerData";

	public static SQLiteDatabase sqlDatabase;

	public static SQLiteDatabase getSqldatabase() {
		return sqlDatabase;
	}

	public static void setSqldatabase(SQLiteDatabase sqldatabase) {
		sqlDatabase = sqldatabase;
	}

	/**
	 * Our SQLLite table is case sensitive. We make searches on upper.
	 * 
	 * @param searchStringToWrap
	 *            Search text entry that we make upper case.
	 * @return Will return string "Upper(entrytext)".
	 */
	public static String getUpperSearch(String searchStringToWrap) {
		return "Upper(" + searchStringToWrap + ")";
	}

	/**
	 * We don't want these fields to show up when we show data.
	 * 
	 * @param fieldToHide Field name.
	 * @return True if a hidden field.
	 */
	public static boolean isHidden(String fieldToHide) {
		String[] hiddenfields = { DancerDao.CHOR_CODE };
		return Arrays.asList(hiddenfields).contains(fieldToHide);
	}


	/**
	 * SQLITE specific way to get the string version of the day of week.
	 * @return
	 */
	public static String getDayOfWeekString() {
		return ",case cast (strftime('%w', PerfDate) as integer)"
				+ " when 0 then 'Sunday   '" + "" + " when 1 then 'Monday   '"
				+ "" + " when 2 then 'Tuesday  '" + " when 3 then 'Wednesday'"
				+ " when 4 then 'Thursday '" + " when 5 then 'Friday   '"
				+ " else 'Saturday ' end as dayofweek ";

	}
	
	public static final void showSqlData(SQLiteDatabase database,String tablename){
		
		Cursor c=database.rawQuery("Select * from "+tablename +" where LastName='Stokes Shadle'",null );
		if (c != null) {

			c.moveToFirst(); // it's very important to do this action otherwise
								// your Cursor object did not get work

			/* Check if at least one Result was returned. */
			if (c.isFirst()) {
				
				int columnCount=c.getColumnCount();
				/* Loop through all Results */
				do {
					
					for (int i = 0; i < columnCount; i++) {
						Log.d(TAG,"Column:"+c.getColumnName(i)+" Value"+c.getString(i));
					}
					
				} while (c.moveToNext());

			} else {
				Log.d(TAG,"No data found!");
			}
		}
		if (c != null) {
			c.close();
		}

	}
}
