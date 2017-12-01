package com.ericbandiero.dancerdata.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.ericbandiero.dancerdata.R;
import com.ericbandiero.dancerdata.code.AppConstant;
import com.ericbandiero.dancerdata.code.DancerDao;
import com.ericbandiero.dancerdata.code.DancerData;
import com.ericbandiero.dancerdata.code.HandleAChildClick;
import com.ericbandiero.dancerdata.code.ITest;
import com.ericbandiero.dancerdata.code.PrepareCursorData;
import com.ericbandiero.dancerdata.code.SqlHelper;
import com.ericbandiero.dancerdata.code.TestConcrete;
import com.ericbandiero.dancerdata.dagger.DanceApp;
import com.ericbandiero.dancerdata.test_code.TestRxJava;
import com.ericbandiero.librarymain.Lib_Base_ActionBarActivity;
import com.ericbandiero.librarymain.Lib_Expandable_Activity;
import com.ericbandiero.librarymain.Lib_StatsActivity;
import com.ericbandiero.librarymain.UtilsShared;
import com.ericbandiero.librarymain.basecode.ControlStatsActivityBuilder;
import com.ericbandiero.librarymain.basecode.ControlStatsAdapterBuilder;
import com.ericbandiero.librarymain.data_classes.Lib_ExpandableDataWithIds;
import com.ericbandiero.librarymain.interfaces.IPrepDataExpandableList;
import com.ericbandiero.librarymain.interfaces.IReturnDialogInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AndroidDataActivity extends Lib_Base_ActionBarActivity implements
		OnItemClickListener, OnCheckedChangeListener,ITest {

	private static final int ID_MENU_EXIT = 0;
	private static final String TAG = "Droid Dancer";

	//Test commit change.
	ArrayList<String> results = new ArrayList<>();
	//transient Button mSearchButton;
	transient EditText mInputEdit;
	transient RadioGroup mradiogroup;

	transient Button buttonPredict;

	//transient TextView textInfo;
	transient ListView listview;
	transient List<Integer> listOfDanceCode = new ArrayList<>();

	final Context context = this;
	transient private RadioButton radioButton;

	// Used for data collection - group by
	transient private String fieldToGroupBy;

	// Used for data collection - order by
	transient private String orderByFields;

	// List of fields to get
	transient List<String> listOfFieldsToGet = new ArrayList<>();

	// String to get data
	String sqlSearchString;

	//For parameters
	String[] selectionArgs;


	//Permission request integer
	public static final int PERMISSION_REQUEST_WRITE_STORAGE=0X1;

	@BindView(R.id.button_performances) Button mSearchButton;
	@BindView(R.id.textViewRecordCount1) TextView textInfo;


/*

	@Inject
	@Named("stats")
	ControlStatsActivityBuilder controlStatsActivityBuilder;
*/
//data access class
	@Inject
	DancerDao dancerDao;

	@Inject
	ControlStatsAdapterBuilder controlStatsAdapterBuilder;

	@Inject
	@Named(AppConstant.DAG_CONTROLLER_STATS)
	Provider <ControlStatsActivityBuilder> controlStatsActivityBuilder;

	@Inject
	@Named(AppConstant.DAG_CONTROLLER_VENUE_BY_PERFORM)
	Provider <ControlStatsActivityBuilder> controlStatsActivityBuilderVenueCounts;

	@Inject
	@Named(AppConstant.DAG_CONTROLLER_VENUE_BY_DANCE)
	Provider <ControlStatsActivityBuilder> controlStatsActivityBuilderVenueDances;

	@Inject
	@Named(AppConstant.DAG_CONTROLLER_GIGS_PER_YEAR)
	Provider <ControlStatsActivityBuilder> controlStatsActivityGigsByYear;

	@Inject
	@Named(AppConstant.DAG_CONTROLLER_DANCER_COUNT)
	Provider <ControlStatsActivityBuilder> controlStatsActivityDancersByWorks;

	@Inject
	@Named(HandleAChildClick.GET_DANCE_DETAIL_FROM_CLICK)
	HandleAChildClick handleAChildClickPerformance;

	@Inject
	@Named(HandleAChildClick.GET_PERFORMANCE_FROM_CLICK)
	HandleAChildClick handleAChildClickVenues;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		confirmExitOnBackPress=true;
		//Dagger
		DanceApp.app().basicComponent().inject(this);

		//We want a context that we can use
		dancerDao.setActivityContext(this);

		ButterKnife.bind(this);
		// getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		//mSearchButton = (Button) findViewById(R.id.button1);
		mInputEdit = findViewById(R.id.editText1);
		mradiogroup = findViewById(R.id.radioGroup1);
		radioButton = findViewById(R.id.radioDancer);
		//textInfo = (TextView) findViewById(R.id.textViewRecordCount1);
		listview = findViewById(R.id.listViewDancer);
		buttonPredict = findViewById(R.id.button_venues);

		//Ask for permissions to use the app
		askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,1);

		if (!AppConstant.WE_HAVE_DATA_IN_TABLE&&dancerDao.isTableEmpty(SqlHelper.MAIN_TABLE_NAME)){
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Table is empty!");
			UtilsShared.AlertMessageSimple(this,"New Database Created","You need to import data - see menu option.");
			AppConstant.WE_HAVE_DATA_IN_TABLE=false;
		}
		else{
			AppConstant.WE_HAVE_DATA_IN_TABLE=true;
		}

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

		//new TestRxJava();
	}

	// /End of main
/*

	@Override
	public void onBackPressed() {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName() + ">", "Back pressed!");
		UtilsShared.AlertMessageSimpleYesNo(this, "Confirm Exit App", "Do you want to exit the app?", new IReturnDialogInt() {
			@Override
			public void execute(int i) {
				if (i == AlertDialog.BUTTON_POSITIVE) {
					finish();
				}
			}
		});
	}
*/

	@Override
	protected void onRestart() {
		super.onRestart();
		mInputEdit.requestFocus();
		Log.i("Restarted", "restarted");
		// mInputEdit.setText("");
		if (!DetailActivity.getDancerdetailid().equals("-1")) {
			RadioButton b = findViewById(R.id.radioDancer);
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
			boolean myWorkingDirectory = dancerDao.createMyWorkingDirectory();
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Was directory created?"+myWorkingDirectory);
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
			case R.id.button_performances:
				intent = dancerDao.prepPerformanceActivity();
				break;
			case R.id.button_venues:
				if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName() + ">", "Clicked venue");

				//We call routine to create the data list.
				List<Lib_ExpandableDataWithIds> listData=dancerDao.prepDataVenue();

				//Intent i=new Intent(this, Lib_Expandable_Activity.class);
				intent = new Intent(this, ExpandListSubclass.class);

				IPrepDataExpandableList prepareCursor = new PrepareCursorData(listData);
				intent.putExtra(Lib_Expandable_Activity.EXTRA_TITLE, "Venue list");
				intent.putExtra(Lib_Expandable_Activity.EXTRA_DATA_PREPARE, prepareCursor);
				intent.putExtra(Lib_Expandable_Activity.EXTRA_INTERFACE_HANDLE_CHILD_CLICK, handleAChildClickVenues);

				//ITestParce t1= new My();
				//t1.doSomething();
				//intent.putExtra("test", t1);
				break;
			default:
				break;
		}
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getTitle() != null && item.getTitle().equals(getString(R.string.menu_stats))) {

			//TestRxJava  testRxJava=new TestRxJava();


			//ControlStatAdapter controlStatAdapter=new ControlStatAdapter();
			Intent statIntent=new Intent(this,Lib_StatsActivity.class);

			//These are for the activity
			statIntent.putExtra(Lib_StatsActivity.EXTRA_STATS_BUILDER, controlStatsActivityBuilder.get());

			//Builder is injected
			statIntent.putExtra(Lib_StatsActivity.EXTRA_DATA_STATS_ADAPTER_CONTROL_INTERFACE, controlStatsAdapterBuilder);

			//statIntent.putExtra(Lib_StatsActivity.EXTRA_DATA_STATS_ADAPTER_CONTROL_INTERFACE,(Serializable)new ControlStatAdapter());

			startActivity(statIntent);
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Stats picked");

		}

		if (item.getTitle() != null && item.getTitle().equals(getString(R.string.menu_dancer_counts))) {
			//ControlStatAdapter controlStatAdapter=new ControlStatAdapter();
			Intent statIntent=new Intent(this,Lib_StatsActivity.class);

			//These are for the activity
			statIntent.putExtra(Lib_StatsActivity.EXTRA_STATS_BUILDER, controlStatsActivityDancersByWorks.get());

			//Builder is injected
			statIntent.putExtra(Lib_StatsActivity.EXTRA_DATA_STATS_ADAPTER_CONTROL_INTERFACE, controlStatsAdapterBuilder);

			//statIntent.putExtra(Lib_StatsActivity.EXTRA_DATA_STATS_ADAPTER_CONTROL_INTERFACE,(Serializable)new ControlStatAdapter());

			startActivity(statIntent);
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Venue picked");

		}



		if (item.getTitle() != null && item.getTitle().equals(getString(R.string.menu_venue_by_performance))) {
			//ControlStatAdapter controlStatAdapter=new ControlStatAdapter();
			Intent statIntent=new Intent(this,Lib_StatsActivity.class);

			//These are for the activity
			statIntent.putExtra(Lib_StatsActivity.EXTRA_STATS_BUILDER, controlStatsActivityBuilderVenueCounts.get());

			//Builder is injected
			statIntent.putExtra(Lib_StatsActivity.EXTRA_DATA_STATS_ADAPTER_CONTROL_INTERFACE, controlStatsAdapterBuilder);

			//statIntent.putExtra(Lib_StatsActivity.EXTRA_DATA_STATS_ADAPTER_CONTROL_INTERFACE,(Serializable)new ControlStatAdapter());

			startActivity(statIntent);
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Venue picked");

		}

		//"Venue By Dance Piece Count"
		if (item.getTitle() != null && item.getTitle().equals(getString(R.string.menu_venue_by_dance_piece))) {
			//ControlStatAdapter controlStatAdapter=new ControlStatAdapter();
			Intent statIntent=new Intent(this,Lib_StatsActivity.class);

			//These are for the activity
			statIntent.putExtra(Lib_StatsActivity.EXTRA_STATS_BUILDER, controlStatsActivityBuilderVenueDances.get());

			//Builder is injected
			statIntent.putExtra(Lib_StatsActivity.EXTRA_DATA_STATS_ADAPTER_CONTROL_INTERFACE, controlStatsAdapterBuilder);

			//statIntent.putExtra(Lib_StatsActivity.EXTRA_DATA_STATS_ADAPTER_CONTROL_INTERFACE,(Serializable)new ControlStatAdapter());

			startActivity(statIntent);
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Venue picked");

		}

		if (item.getTitle() != null && item.getTitle().equals(getString(R.string.menu_gigs_by_year))) {
			//ControlStatAdapter controlStatAdapter=new ControlStatAdapter();
			Intent statIntent=new Intent(this,Lib_StatsActivity.class);

			//These are for the activity
			statIntent.putExtra(Lib_StatsActivity.EXTRA_STATS_BUILDER, controlStatsActivityGigsByYear.get());

			//Builder is injected
			statIntent.putExtra(Lib_StatsActivity.EXTRA_DATA_STATS_ADAPTER_CONTROL_INTERFACE, controlStatsAdapterBuilder);

			//statIntent.putExtra(Lib_StatsActivity.EXTRA_DATA_STATS_ADAPTER_CONTROL_INTERFACE,(Serializable)new ControlStatAdapter());

			startActivity(statIntent);
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Venue picked");

		}

		//Predictions
		if (item.getTitle().equals(getString(R.string.menu_prediction))) {
			Intent intent = new Intent(this, PredictActivity.class);
			startActivity(intent);
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
								//DancerDao dancerDao = new DancerDao(context);
								dancerDao.importData(context);

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
		menu.add(R.string.menu_stats);
		menu.add(R.string.menu_dancer_counts);
		menu.add(R.string.menu_gigs_by_year);
		menu.add(R.string.menu_venue_by_performance);
		menu.add(R.string.menu_venue_by_dance_piece);
		menu.add(R.string.menu_prediction);
		UtilsShared.removeMenuItems(menu, R.id.menu_item_lib_quit);
		//UtilsShared.removeMenuItems(menu,88);

		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","In on destroy...");
	}

	private void getDataAndShowIt(String dataToGet) {

		Cursor c;
		// Get rid of previous results
		results.clear();
		listOfDanceCode.clear();

		setDataSearch();

		String fieldsToGet[] = listOfFieldsToGet
				.toArray(new String[listOfFieldsToGet.size()]);

		SQLiteDatabase	db=dancerDao.getDataBaseRead();

		c = db.query("Info", fieldsToGet, sqlSearchString, selectionArgs,
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
				textInfo.setText(getString(R.string.results_text) + i);
			} else {
				results.add("No Data Found!");
			}
		}
		if (c != null) {
			c.close();
		}
	}

	private void setDataSearch() {

		int buttonId = radioButton.getId();

		String userSearchText = mInputEdit.getText().toString().trim()
				.toUpperCase();


		switch (buttonId) {
			case R.id.radioDancer:
				Log.i(TAG, "dancer");
				listOfFieldsToGet = new ArrayList<>(Arrays.asList(
						DancerDao.LAST_NAME, DancerDao.FIRST_NAME,
						DancerDao.TITLE, DancerDao.VENUE, DancerDao.PERF_DATE,
						DancerDao.DANCE_CODE, DancerDao.CHOR_CODE));

				if (DetailActivity.dancerdetailid.equals("-1")) {

					sqlSearchString = DancerData
							.getUpperSearch(DancerDao.LAST_NAME)
							+ " LIKE ?";
					selectionArgs=new String[] {userSearchText+'%'};

				} else {
					mInputEdit.setText("");
					sqlSearchString = DancerData.getUpperSearch(DancerDao.CODE)
							+ "=?";
					selectionArgs=new String[] {DetailActivity.dancerdetailid};

					DetailActivity.dancerdetailid = "-1";
				}

				fieldToGroupBy = "Code,LastName,FirstName,Title,Venue,PerfDate,Dance_Code";
				orderByFields = "LastName,FirstName,PerfDate Desc";
				break;
			case R.id.radioVenue:
				listOfFieldsToGet = new ArrayList<>(
						Collections.singletonList(DancerDao.VENUE));
				sqlSearchString = DancerData.getUpperSearch(DancerDao.VENUE)
						+ " LIKE ?";
				selectionArgs=new String[] {userSearchText+'%'};
				fieldToGroupBy = DancerDao.VENUE;
				orderByFields = DancerDao.VENUE;
				Log.i(TAG, "Venue");
				break;

			case R.id.radioPeople:
				listOfFieldsToGet = new ArrayList<>(Arrays.asList(
						DancerDao.CLAST_NAME, DancerDao.CFIRST_NAME, DancerDao.TITLE, DancerDao.VENUE, DancerDao.PERF_DATE,
						DancerDao.DANCE_CODE, DancerDao.CHOR_CODE));
				sqlSearchString = DancerData.getUpperSearch(DancerDao.CLAST_NAME)
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
		Log.i(TAG + " search str", sqlSearchString);
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
		ListView listView = findViewById(R.id.listViewDancer);
		listView.setAdapter(new ArrayAdapter<>(this,
				android.R.layout.simple_list_item_1, results));
		listView.setTextFilterEnabled(true);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
							int position, long row) {
		Log.d(TAG, listOfDanceCode.toString());
		if (radioButton.getId() == R.id.radioDancer || radioButton.getId() == R.id.radioPeople) {
			DetailActivity.setDance_id(listOfDanceCode.get(position));
			Intent intent = new Intent(this, DetailActivity.class);
			startActivity(intent);
		}
		else{
			String venueToGet=adapterView.getAdapter().getItem(position).toString();
			String venueName=venueToGet.substring(venueToGet.indexOf(":")+1).trim();
			dancerDao.getPerformanceForAVenue(venueName);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		radioButton = findViewById(checkedId);
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

