package com.ericbandiero.dancerdata.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ericbandiero.dancerdata.R;
import com.ericbandiero.dancerdata.code.AndroidUtility;
import com.ericbandiero.dancerdata.code.AppConstant;
import com.ericbandiero.dancerdata.code.DancerDao;
import com.ericbandiero.dancerdata.code.IProcessCursorAble;
import com.ericbandiero.dancerdata.code.SqlHelper;
import com.ericbandiero.dancerdata.dagger.DanceApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;


public class PredictActivity extends AppCompatActivity implements IProcessCursorAble {

	private static final String TAG = "PREDICT";
	private ListView listPredict;
	private TextView textViewChild;
	private TextView textViewRecord;
	//private Map<Integer, List<String>> mapData = new TreeMap<>();

	//Get the current year - we don't want to include those
	private final int currentYear = Calendar.getInstance().get(Calendar.YEAR);

	//Date formatter
	private final SimpleDateFormat df_MMddyyyyEEE = new SimpleDateFormat("MM-dd-yyyy (EEE)", Locale.US);
	private final SimpleDateFormat df_yyyymmdd = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	//Create a date
	private final Calendar calendar = Calendar.getInstance();
	@Inject
	DancerDao dancerDao;

	private Context activityContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_predict);
		//Dagger
		DanceApp.app().basicComponent().inject(this);
		activityContext = this;
		//noinspection ConstantConditions
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		listPredict = findViewById(R.id.listViewPredict);
		listPredict.setVisibility(View.INVISIBLE);
		textViewChild = findViewById(R.id.textViewChild);
		textViewRecord = findViewById(R.id.textViewRecordCountBase);

		//We use Monday as first day of week...want weekend to fall in same week.
		calendar.setFirstDayOfWeek(Calendar.MONDAY);

		//We use Thursday
		calendar.setMinimalDaysInFirstWeek(4);
		//dancerDao=new DancerDao(this);

			showData();
	}

	private void showData() {
		dancerDao.runRawQueryWithRxJava("Select distinct perfdate,perfdesc,'  ' as wdate,perfdate as _id from " + SqlHelper.MAIN_TABLE_NAME +
				" where strftime('%Y',Perfdate)<>'" + currentYear + "' order by strftime('%W',Perfdate)", this);
	}

	@SuppressLint("DefaultLocale")
	private void setUpDataFromCursor(Cursor cursor) {
			/*
		  Key=Week number of year. Values holds list of previous performances during that week number.
		 */
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Setting up prediction data..");
		//List of maps - each will hold date for header and child row
		List<HashMap<String, String>> fillMaps = new ArrayList<>();
		Map<String, List<String>> mapData2 = new TreeMap<>();

		try {
			Log.i(TAG, "Count:" + cursor.getCount());
		} catch (SQLiteException e) {
			Log.e(TAG, "Error" + e.getMessage());
		}

		textViewRecord.setText(String.format("%s%d", getString(R.string.results_text), cursor.getCount()));

		cursor.moveToFirst();

	            /* Check if at least one Result was returned. */
		if (cursor.isFirst()) {
			/* Loop through all Results */
			do {
				try {
					calendar.setTime(df_yyyymmdd.parse(cursor.getString(0)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
				//Log.d(TAG, "Week:" + weekOfYear + " Perf date:" + calendar.getTime());
				//    tempList.add("Week:"+weekOfYear+" Perf date:"+calendar.getTime());

				/*
				List<String> l = mapData.get(weekOfYear);
				if (l == null) {
					mapData.put(weekOfYear, l = new ArrayList<>());
				}

				l.add(cursor.getString(1));
				*/

				int year = Calendar.getInstance().get(Calendar.YEAR);
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.WEEK_OF_YEAR, weekOfYear);

				//Tricky bit of code year - try to pull list from map.
				//If we have entry we add to it - if not we create new entry, then add.
				List<String> l2 = mapData2.get(df_MMddyyyyEEE.format(calendar.getTime()));
				if (l2 == null) {
					mapData2.put(df_MMddyyyyEEE.format(calendar.getTime()), l2 = new ArrayList<>());
				}
				l2.add(cursor.getString(0) + ":" + cursor.getString(1));
				//End of additions to map.

			} while (cursor.moveToNext());
		}

		cursor.close();

		/*
		for (Map.Entry<Integer, List<String>> e : mapData.entrySet()) {
			*//* Log.d(TAG, "Key:" + e.getKey() + ": " + e.getValue()); *//*
		}
*/
		SimpleAdapter adapter;

		String[] from = new String[]{"Parent", "Child"};
		int[] to = new int[]{R.id.textViewHeader, R.id.textViewChild};

		//Clear out fillMaps from previous - try this instead
		fillMaps.clear();

		for (Map.Entry<String, List<String>> e : mapData2.entrySet()) {
			HashMap<String, String> map = new HashMap<>();
			//if (AppConstant.DEBUG)Log.d(this.getClass().getSimpleName() + ">", "Key:" + e.getKey());
			map.put("Parent", e.getKey() + e.getValue().size());
			map.put("Child", AndroidUtility.getStringFromList(e.getValue()));
			fillMaps.add(map);
		}

		adapter = new SimpleAdapter(activityContext, fillMaps, R.layout.expandertexts, from, to);
		listPredict.setAdapter(adapter);
		listPredict.setVisibility(View.VISIBLE);

		//listPredict.setOnItemClickListener(this);

		listPredict.setOnItemClickListener((parent, view, position, id) -> {
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Clicked parent...");
			// View parentView = (View) view.getParent();
			textViewChild = view.findViewById(R.id.textViewChild);

			//    String item = ((TextView) view).getText().toString();

			if (textViewChild.getVisibility() == View.VISIBLE) {
				textViewChild.setVisibility(View.GONE);
			} else {
				textViewChild.setVisibility(View.VISIBLE);
			}
		});

	}


	public void onClickHeader(View v) {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Header text clicked"+v.getClass().getName());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.predict, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dancerDao.close();
	}


	@Override
	public void processCursor(Cursor cursor) {
		setUpDataFromCursor(cursor);
	}
/*
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		System.out.println("Clicked on an item...");
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Clicked parent...");
		// View parentView = (View) view.getParent();
		textViewChild = view.findViewById(R.id.textViewChild);

		//    String item = ((TextView) view).getText().toString();

		if (textViewChild.getVisibility() == View.VISIBLE) {
			textViewChild.setVisibility(View.GONE);
		} else {
			textViewChild.setVisibility(View.VISIBLE);
		}
	}
	*/
}
