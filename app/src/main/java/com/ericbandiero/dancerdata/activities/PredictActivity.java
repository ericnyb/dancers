package com.ericbandiero.dancerdata.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ericbandiero.dancerdata.code.AndroidUtility;
import com.ericbandiero.dancerdata.R;
import com.ericbandiero.dancerdata.code.AppConstant;
import com.ericbandiero.dancerdata.dagger.DanceApp;
import com.ericbandiero.dancerdata.code.DancerDao;
import com.ericbandiero.dancerdata.code.SqlHelper;

import javax.inject.Inject;


public class PredictActivity extends AppCompatActivity {

	private static final String TAG ="PREDICT";
	ListView listPredict;
	TextView textviewChild;
	TextView textviewrecord;

	@Inject
	DancerDao dancerDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_predict);
		//Dagger
		DanceApp.app().basicComponent().inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		 listPredict= findViewById(R.id.listViewPredict);
	     textviewChild= findViewById(R.id.textViewChild);
	     textviewrecord= findViewById(R.id.textViewRecordCountBase);
		//dancerDao=new DancerDao(this);
		 listPredict.setOnItemClickListener(new OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view,
	                    int position, long id) {

	                // View parentView = (View) view.getParent();
	                 textviewChild = view.findViewById(R.id.textViewChild);
	               
	                //    String item = ((TextView) view).getText().toString();
	               
	                 if(textviewChild.getVisibility()==View.VISIBLE){
	                     textviewChild.setVisibility(View.GONE);
	                 }
	                 else{
	                     textviewChild.setVisibility(View.VISIBLE);
	                 }
	            }
	        });
		 
		try {
			showData();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showData() throws ParseException {

		/**
		 * Key=Week number of year. Values holds list of previous performances during that week number.
		 */
		Map<Integer,List<String>> mapData=new TreeMap<Integer,List<String>>();

		//Get the current year - we don't want to include those
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);

		/**
		 * List to hold the predicted date and frequence of past 
		 */
		Cursor cursor=dancerDao.runRawQuery("Select distinct perfdate,perfdesc,'  ' as wdate,perfdate as _id from "+ SqlHelper.MAIN_TABLE_NAME +
	                " where strftime('%Y',Perfdate)<>'"+currentYear+"' order by strftime('%W',Perfdate)");
	        try{
	        Log.i(TAG,"Count:"+    cursor.getCount());
	        }
	        catch(SQLiteException e){
	            Log.e(TAG,"Error"+    e.getMessage());
	        }

			textviewrecord.setText("Record count:"+cursor.getCount());
	        //Date formatter
	        SimpleDateFormat df_MMddyyyyEEE = new SimpleDateFormat("MM-dd-yyyy (EEE)", Locale.US);
	        SimpleDateFormat df_yyyymmdd = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
	        
	        //Create a date
	        Calendar calendar=Calendar.getInstance();
	       
	        //We use Monday as first day of week...want weekend to fall in same week.
	        calendar.setFirstDayOfWeek(Calendar.MONDAY);
	       
	        //We use Thursday
	        calendar.setMinimalDaysInFirstWeek(4);
	        
	        //List of maps - each will hold date for header and child row
	        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	        Map<String,List<String>> mapData2=new TreeMap<String,List<String>>();
	        
	        
	        List<String> tempList=new LinkedList<String>();
	        
	        if (cursor != null) {

	            cursor.moveToFirst();
	                                	       
	            /* Check if at least one Result was returned. */
	            if (cursor.isFirst()) {
	                /* Loop through all Results */
	                do {
	                    calendar.setTime(df_yyyymmdd.parse(cursor.getString(0)));
	                    int weekOfYear=calendar.get(Calendar.WEEK_OF_YEAR);
	                    Log.d(TAG,"Week:"+weekOfYear+" Perf date:"+calendar.getTime());
	                    tempList.add("Week:"+weekOfYear+" Perf date:"+calendar.getTime());
	                    List<String> l = mapData.get(weekOfYear);
	                        if (l == null){
	                            mapData.put(weekOfYear, l=new ArrayList<String>());
	                        }
	                        l.add(cursor.getString(1));
	                    int year=Calendar.getInstance().get(Calendar.YEAR);
                        calendar.set(Calendar.YEAR, year);
	                    calendar.set(Calendar.WEEK_OF_YEAR, weekOfYear);
	       	         
	       	            //Tricky bit of code year - try to pull list from map.
	                    //If we have entry we add to it - if not we create new entry, then add.
	                    List<String> l2 = mapData2.get(df_MMddyyyyEEE.format(calendar.getTime()));
	                    if (l2 == null){
	                        mapData2.put(df_MMddyyyyEEE.format(calendar.getTime()), l2=new ArrayList<String>());
	                    }
	                    l2.add(cursor.getString(0)+":"+cursor.getString(1));
	                   //End of additions to map.

	                } while (cursor.moveToNext());
	            } else {
	            	assert false;
	            }
	        }
	       
	        cursor.close();
	       
	        for (Map.Entry<Integer, List<String>> e : mapData.entrySet()){
	        	Log.d(TAG,"Key:"+e.getKey() + ": " + e.getValue());
	        }
	     
	       
	         SimpleAdapter adapter;
	        
	         
	         String[] from = new String[] {"Parent", "Child"};
	         int[] to = new int[] { R.id.textViewHeader, R.id.textViewChild};

 	         //Clear out fillmaps from previous - try this instead
	         fillMaps.clear();
	        
	         for (Map.Entry<String, List<String>> e : mapData2.entrySet()){
	              HashMap<String, String> map = new HashMap<String, String>();
				 if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Key:"+e.getKey());
	              map.put("Parent", e.getKey() + e.getValue().size());
	              map.put("Child", AndroidUtility.getStringFromList(e.getValue()));
	              fillMaps.add(map);
	         }
	         
	         adapter = new SimpleAdapter(this, fillMaps, R.layout.expandertexts, from, to);
	         listPredict.setAdapter(adapter);
	}

	   public void onClickHeader(View v) {
         Log.d(TAG,"Text clicked");
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
}
