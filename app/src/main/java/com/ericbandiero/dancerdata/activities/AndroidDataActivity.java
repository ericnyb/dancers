package com.ericbandiero.dancerdata.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.ericbandiero.dancerdata.AppConstant;
import com.ericbandiero.dancerdata.R;
import com.ericbandiero.dancerdata.code.ControlStatAdapter;
import com.ericbandiero.dancerdata.code.DancerDao;
import com.ericbandiero.dancerdata.code.DancerData;
import com.ericbandiero.dancerdata.code.HandleAChildClick;
import com.ericbandiero.dancerdata.code.ITest;
import com.ericbandiero.dancerdata.code.PrepareCursorData;
import com.ericbandiero.dancerdata.code.StatData;
import com.ericbandiero.dancerdata.code.TestConcrete;
import com.ericbandiero.librarymain.Lib_Base_ActionBarActivity;
import com.ericbandiero.librarymain.Lib_Expandable_Activity;
import com.ericbandiero.librarymain.Lib_StatsActivity;
import com.ericbandiero.librarymain.UtilsShared;
import com.ericbandiero.librarymain.adapters.Lib_StatsAdapter;
import com.ericbandiero.librarymain.basecode.HandleListViewClicksStats;
import com.ericbandiero.librarymain.data_classes.Lib_ExpandableDataWithIds;
import com.ericbandiero.librarymain.interfaces.IControlStatAdapter;
import com.ericbandiero.librarymain.interfaces.IHandleChildClicksExpandableIds;
import com.ericbandiero.librarymain.interfaces.IHandleListViewClicks;
import com.ericbandiero.librarymain.interfaces.IPrepDataExpandableList;
import com.ericbandiero.librarymain.interfaces.ITestParce;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AndroidDataActivity extends Lib_Base_ActionBarActivity implements
		OnItemClickListener, OnCheckedChangeListener,Serializable,ITest {
	private static final int ID_MENU_EXIT = 0;
	private static final String TAG = "Droid Dancer";

	//Test commit change.
	ArrayList<String> results = new ArrayList<String>();
	//transient Button mSearchButton;
	transient EditText mInputEdit;
	transient RadioGroup mradiogroup;

	transient Button buttonPredict;

	//transient TextView textInfo;
	transient ListView listview;
	transient List<Integer> listOfDanceCode = new ArrayList<Integer>();

	final Context context = this;
	transient private RadioButton radioButton;

	// Used for data collection - group by
	transient private String fieldToGroupBy;

	// Used for data collection - order by
	transient private String orderByFields;

	// List of fields to get
	transient List<String> listOfFieldsToGet = new ArrayList<String>();

	// String to get data
	String sqlSeacrhString;

	//For parameterss
	String[] selectionArgs;

	//data access class
	DancerDao dancerDao;

	//Permission request integer
	public static final int PERMISSION_REQUEST_WRITE_STORAGE=0X1;

	@BindView(R.id.button1) Button mSearchButton;
	@BindView(R.id.textViewRecordCount1) TextView textInfo;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//We want a context that we can use
		AppConstant.CONTEXT = this;

		ButterKnife.bind(this);
		// getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		//mSearchButton = (Button) findViewById(R.id.button1);
		mInputEdit = (EditText) findViewById(R.id.editText1);
		mradiogroup = (RadioGroup) findViewById(R.id.radioGroup1);
		radioButton = (RadioButton) findViewById(R.id.radioDancer);
		//textInfo = (TextView) findViewById(R.id.textViewRecordCount1);
		listview = (ListView) findViewById(R.id.listViewDancer);
		buttonPredict = (Button) findViewById(R.id.button2);
		dancerDao = new DancerDao(context);

		//Ask for permissions to use the app
		askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,1);

		// First thing - make sure we have a directory
		//We will call this from the ask callback
		//dancerDao.createMyWorkingDirectory();

		//get rid of it temp
		/*
		File folder = new File(Environment.getExternalStorageDirectory()
				+ "/DancerData");
		folder.delete();
		*/

		listview.setOnItemClickListener(this);

		mradiogroup.setOnCheckedChangeListener(this);

		// EditText SearchEditText =(EditText)findViewById(R.id.editText1);

		mInputEdit.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				Log.i(TAG, "event:" + arg2);
				if (arg1 == EditorInfo.IME_ACTION_GO) {
					dataRunner();
				}

				return false;
			}

		});
	}

	// /End of main


	@Override
	protected void onRestart() {
		super.onRestart();
		mInputEdit.requestFocus();
		Log.i("Restarted", "restarted");
		// mInputEdit.setText("");
		if (!DetailActivity.getDancerdetailid().equals("-1")) {
			RadioButton b = (RadioButton) findViewById(R.id.radioDancer);
			b.setChecked(true);

			//radioButton.setId(R.id.radioDancer);
			dataRunner();
		}
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}


	private void askForPermission(String permission, Integer requestCode) {
		if (ContextCompat.checkSelfPermission(AndroidDataActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(AndroidDataActivity.this, permission)) {

				//This is called if user has denied the permission before
				//In this case I am just asking the permission again
				ActivityCompat.requestPermissions(AndroidDataActivity.this, new String[]{permission}, requestCode);

			} else {

				ActivityCompat.requestPermissions(AndroidDataActivity.this, new String[]{permission}, requestCode);
			}
		} else {
			//We have permission - we can do this.
			dancerDao.createMyWorkingDirectory();
			//Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
		}
	}


	/**
	 * Callback for the result from requesting permissions. This method
	 * is invoked for every call on {@link #requestPermissions(String[], int)}.
	 * <p>
	 * <strong>Note:</strong> It is possible that the permissions request interaction
	 * with the user is interrupted. In this case you will receive empty permissions
	 * and results arrays which should be treated as a cancellation.
	 * </p>
	 *
	 * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
	 * @param permissions  The requested permissions. Never null.
	 * @param grantResults The grant results for the corresponding permissions
	 *                     which is either {@link PackageManager#PERMISSION_GRANTED}
	 *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
	 * @see #requestPermissions(String[], int)
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		//We only ask for write permissions so array is length 1
		if (requestCode==PERMISSION_REQUEST_WRITE_STORAGE&grantResults[0]==PackageManager.PERMISSION_DENIED){
			UtilsShared.AlertMessageSimple(this,"Needed permissions were denied","You will not be able to use the application.");
		}
		else{
			//We can do this
			dancerDao.createMyWorkingDirectory();
		}
	}

	private void dataRunner() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mInputEdit.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);

		String dataSearch = mInputEdit.getText().toString();
		getDataAndShowIt(dataSearch);
		displayResultList();
		Log.i(TAG, mInputEdit.getText().toString());
	}

	public void onRadioButtonClick(View v) {
		radioButton = (RadioButton) v;
		Log.i(TAG, radioButton.getText().toString());
	}

	// This method is called at button click because we assigned the name to the
	// "On Click property" of the button
	// TODO Find another use for this button - will use keyboard go instead.
	public void myClickHandler(View view) {
		// dataRunner();

		Log.d(TAG, view.getClass().getName());
		Intent intent = null;
		int id = view.getId();

		switch (id) {
			case R.id.button1:
				intent = new Intent(this, PerfActivity.class);
				break;
			case R.id.button2:
				intent = new Intent(this, PredictActivity.class);
				break;
			default:
				break;
		}
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getTitle() != null && item.getTitle().equals("Stats")) {
			DancerDao dancerDao=new DancerDao(this);
			StatData statData=new StatData(dancerDao);
			//ControlStatAdapter controlStatAdapter=new ControlStatAdapter();
			Intent statIntent=new Intent(this,Lib_StatsActivity.class);

			//These are for the activity
			statIntent.putExtra(Lib_StatsActivity.EXTRA_TITLE,"Shoot Information");
			statIntent.putExtra(Lib_StatsActivity.EXTRA_HEADER,"Stats");
			statIntent.putExtra(Lib_StatsActivity.EXTRA_DATA_STATS_HEADER_BACK_COLOR, ContextCompat.getColor(context, R.color.PaleTurquoise));
			statIntent.putExtra(Lib_StatsActivity.EXTRA_DATA_HOLDER_TWO_FIELDS, (Serializable) statData.runStats());
			statIntent.putExtra(Lib_StatsActivity.EXTRA_DATA_LIST_VIEW_CLICK_COMMAND_INTERFACE,(Serializable)new HandleTestClick());

			//This is for the adapter
			statIntent.putExtra(Lib_StatsActivity.EXTRA_DATA_STATS_ADAPTER_CONTROL_INTERFACE,(Serializable)new ControlStatAdapter());

			startActivity(statIntent);
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Stats picked");

		}

		if (item.getTitle() != null && item.getTitle().equals("Venue Data")) {
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName() + ">", "Clicked venue");

			//We call routine to create the data list.
			List<Lib_ExpandableDataWithIds> listData=dancerDao.prepDataVenue();

			//Intent i=new Intent(this, Lib_Expandable_Activity.class);
			Intent i = new Intent(this, ExpandListSubclass.class);



			IPrepDataExpandableList prepareCursor = new PrepareCursorData(listData);

			HandleAChildClick handleAChildClick = new HandleAChildClick(HandleAChildClick.VENUE_CLICK);
			//HandleAChildClick handleAChildClick = new HandleAChildClick(this);

			IHandleChildClicksExpandableIds ih=new IHandleChildClicksExpandableIds(){
				@Override
				public void handleClicks(Context context, Lib_ExpandableDataWithIds lib_expandableDataWithIds, Lib_ExpandableDataWithIds lib_expandableDataWithIds1) {
				if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Hello");
				}
			};

//			i.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE,iPrepDataExpandableList);
//			i.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE,prepDataExpandableList);
			i.putExtra(Lib_Expandable_Activity.EXTRA_TITLE, "Venue list");

			i.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE, prepareCursor);

			i.putExtra(Lib_Expandable_Activity.EXTRA_INTERFACE_HANDLE_CHILD_CLICK, handleAChildClick);
		//	i.putExtra(Lib_Expandable_Activity.EXTRA_INTERFACE_HANDLE_CHILD_CLICK, ih);




//			TestConcrete t1=new TestConcrete(){
//				@Override
//				public void doSomething(){
//					System.out.println("Yowser!");
//				}
//
//		};

			ITestParce t1= new My();


			t1.doSomething();
			i.putExtra("test", t1);

			startActivity(i);
		}

		if (item.getTitle() != null && item.getTitle().equals("Performance Data")) {
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName() + ">", "Clicked performance");
/*

			//SQLiteDatabase db = SQLiteDatabase.openDatabase(SqlHelper.SAMPLE_DB_NAME, null, 0);
			//DancerDao dancerDao = new DancerDao(AppConstant.CONTEXT);
			final Cursor cursor = dancerDao.runRawQuery(
					"select PerfDate as _id," +
							"PerfDate," +
							"PerfDesc," +
							"Venue," +
							"Dance_Code," +
							"title," +
							"Perf_Code" +
							" from Info group by Perf_Code,Dance_code order by PerfDate desc");
			//this.cursor = db.rawQuery("select PerfDate as _id,PerfDate,PerfDesc,Venue from Info group by PerfDate,Venue order by PerfDate desc", null);

			List<Lib_ExpandableDataWithIds> listData = new ArrayList<>();

			SortedSet<String> performances = new TreeSet<>(Collections.<String>reverseOrder());

			//First get venues
			while (cursor.moveToNext()) {
				if (!performances.add(cursor.getString(1)+":"+cursor.getString(2))){
					if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Duplicate:"+cursor.getString(2));
				}

				Lib_ExpandableDataWithIds lib_expandableDataWithIds=new Lib_ExpandableDataWithIds(cursor.getString(1)+":"+cursor.getString(2), cursor.getString(5));
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
*/

			List<Lib_ExpandableDataWithIds> listData=dancerDao.prepDataPerformance();

			int size=0;

			for (Lib_ExpandableDataWithIds lib_expandableDataWithIds : listData) {
				if (lib_expandableDataWithIds.getTextStringChild()==null){
					size++;
				}
			}

			//Intent i=new Intent(this, Lib_Expandable_Activity.class);
			Intent i = new Intent(this, ExpandListSubclass.class);

			IPrepDataExpandableList prepareCursor = new PrepareCursorData(listData);

			HandleAChildClick handleAChildClick = new HandleAChildClick(HandleAChildClick.PERFORMANCE_CLICK);

			IHandleChildClicksExpandableIds ih=new IHandleChildClicksExpandableIds() {
				@Override
				public void handleClicks(Context context, Lib_ExpandableDataWithIds lib_expandableDataWithIds, Lib_ExpandableDataWithIds lib_expandableDataWithIds1) {
					if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Hello");
				}
			};


//			i.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE,iPrepDataExpandableList);
//			i.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE,prepDataExpandableList);
			i.putExtra(Lib_Expandable_Activity.EXTRA_TITLE, "Performances:"+size);

			i.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE, prepareCursor);

			i.putExtra(Lib_Expandable_Activity.EXTRA_INTERFACE_HANDLE_CHILD_CLICK, handleAChildClick);
			//	i.putExtra(Lib_Expandable_Activity.EXTRA_INTERFACE_HANDLE_CHILD_CLICK, ih);
			//Test comment
			//Test 2
			startActivity(i);
		}



		// check selected menu item
		if (item.getTitle() != null && item.getTitle().equals("Import Data")) {
			if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
				this.loadData();
			}
				else
			{
				UtilsShared.AlertMessageSimple(this,"Permissions Information","You need to go into settings and grant Write to storage permissions.");
			}

			return true;
		}

		return false;
	}

	private void loadData() {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder
				.setTitle("This will erase old data and import new data.");

		// set dialog message
		alertDialogBuilder
				.setMessage("Click yes to import.")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								//	if (checkIfInputFileExists()) {
								DancerDao dancerDao = new DancerDao(context);
								dancerDao.importData();

								//dropTable();
								//createSqlTable();

								//readFile3();
								//	}
							}
						})
				.setNegativeButton("Cancel Import",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

		// Comment out for now - we have the data we need for testing

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		// the menu option text is defined in resources
		//menu.add(R.string.aboutOption);

		// it is better to use final variables for IDs than constant values
		// menu.add(Menu.NONE,1,Menu.NONE,"Exit");

		// get the MenuItem reference
		//MenuItem item = menu.add(Menu.NONE, ID_MENU_EXIT, Menu.NONE,R.string.exitOption);
		// set the shortcut
		//item.setShortcut('5', 'x');

		// the menu option text is defined as constant String
		menu.add("Import Data");
		menu.add("Venue Data");
		menu.add("Performance Data");
		menu.add("Stats");

		UtilsShared.removeMenuItems(menu, R.id.menu_item_lib_quit);
		//UtilsShared.removeMenuItems(menu,88);

		return true;
	}

	private void getDataAndShowIt(String datatoget) {

		Cursor c;
		// Get rid of previous results
		results.clear();
		listOfDanceCode.clear();

		setDataSearch();

		String fieldsToGet[] = listOfFieldsToGet
				.toArray(new String[listOfFieldsToGet.size()]);

		SQLiteDatabase	db=dancerDao.getDataBaseRead();

		c = db.query("Info", fieldsToGet, sqlSeacrhString, selectionArgs,
				fieldToGroupBy, null, orderByFields);

		if (c != null) {

			c.moveToFirst();

			/* Check if at least one Result was returned. */
			if (c.isFirst()) {
				int i = 0;
				/* Loop through all Results */
				do {
					i++;
					results.add("" + i + ": " + getColumnsFromSqliteColumn(c));
				} while (c.moveToNext());
				textInfo.setText("Records found:" + i);
			} else {
				results.add("No Data Found!");
			}
		}
		c.close();
	}

	private void setDataSearch() {

		int buttonId = radioButton.getId();

		String userSearchText = mInputEdit.getText().toString().trim()
				.toUpperCase();
		;

		switch (buttonId) {
			case R.id.radioDancer:
				Log.i(TAG, "dancer");
				listOfFieldsToGet = new ArrayList<String>(Arrays.asList(
						DancerDao.LAST_NAME, DancerDao.FIRST_NAME,
						DancerDao.TITLE, DancerDao.VENUE, DancerDao.PERF_DATE,
						DancerDao.DANCE_CODE, DancerDao.CHOR_CODE));

				if (DetailActivity.dancerdetailid.equals("-1")) {

					sqlSeacrhString = DancerData
							.getUpperSearch(DancerDao.LAST_NAME)
							+ " LIKE ?";
					selectionArgs=new String[] {userSearchText+'%'};

				} else {
					mInputEdit.setText("");
					sqlSeacrhString = DancerData.getUpperSearch(DancerDao.CODE)
							+ "=?";
					selectionArgs=new String[] {DetailActivity.dancerdetailid};

					DetailActivity.dancerdetailid = "-1";
				}

				fieldToGroupBy = "Code,LastName,Firstname,Title,Venue,PerfDate,Dance_Code";
				orderByFields = "LastName,FirstName,PerfDate Desc";
				break;
			case R.id.radioVenue:
				listOfFieldsToGet = new ArrayList<String>(
						Arrays.asList(DancerDao.VENUE));
				sqlSeacrhString = DancerData.getUpperSearch(DancerDao.VENUE)
						+ " LIKE ?";
				selectionArgs=new String[] {userSearchText+'%'};
				fieldToGroupBy = DancerDao.VENUE;
				orderByFields = DancerDao.VENUE;
				Log.i(TAG, "Venue");
				break;

			case R.id.radioPeople:
				listOfFieldsToGet = new ArrayList<String>(Arrays.asList(
						DancerDao.CLAST_NAME, DancerDao.CFIRST_NAME, DancerDao.TITLE, DancerDao.VENUE, DancerDao.PERF_DATE,
						DancerDao.DANCE_CODE, DancerDao.CHOR_CODE));
				sqlSeacrhString = DancerData.getUpperSearch(DancerDao.CLAST_NAME)
						+ " LIKE ?";
				selectionArgs=new String[] {userSearchText+'%'};
				fieldToGroupBy = DancerDao.CLAST_NAME + "," + DancerDao.CFIRST_NAME + "," + DancerDao.DANCE_CODE;
				orderByFields = "CLastName,CFirstName,PerfDate Desc";
				Log.i(TAG, "Venue");
				break;
			default:
				Log.i(TAG, "other");
				break;
		}

		Log.i(TAG + " fields", listOfFieldsToGet.toString());
		Log.i(TAG + " search str", sqlSeacrhString);
		Log.i(TAG + " group by", fieldToGroupBy);
		Log.i(TAG + " order by", orderByFields);
	}

	private String getColumnsFromSqliteColumn(Cursor cursor) {

		StringBuilder fields = new StringBuilder();

		for (int i = 0; i < cursor.getColumnCount(); i++) {
			if (!DancerData.isHidden(cursor.getColumnName(i))) {
				fields.append(cursor.getString(i));
				fields.append(", ");
			}
			if (cursor.getColumnName(i).equals("Dance_Code")) {
				listOfDanceCode.add(cursor.getInt(i));
			}
		}

		fields.setLength(fields.length() - 2);

		return fields.toString();

	}

	private void displayResultList() {
		ListView listView = (ListView) findViewById(R.id.listViewDancer);
		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, results));
		listView.setTextFilterEnabled(true);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterview, View view,
							int position, long row) {
		Log.d(TAG, listOfDanceCode.toString());
		if (radioButton.getId() == R.id.radioDancer || radioButton.getId() == R.id.radioPeople) {
			DetailActivity.setDance_id(listOfDanceCode.get(position));
			Intent intent = new Intent(this, DetailActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		radioButton = (RadioButton) findViewById(checkedId);
		Log.i(TAG, radioButton.getText().toString());
	}

	@Override
	public void test() {

	}
	private class My extends TestConcrete{

		@Override
		public void doSomething() {
			System.out.println("Subclass calling!");
		}
	}
}

class HandleTestClick implements IHandleListViewClicks,Serializable{

	private static final long serialVersionUID = -5001699047268760417L;

	@Override
	public void handleClicks(AdapterView<?> adapterView, View view, int i, long l) {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Test!");
	}
}