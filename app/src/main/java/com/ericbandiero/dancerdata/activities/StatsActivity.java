package com.ericbandiero.dancerdata.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.ericbandiero.dancerdata.AppConstant;
import com.ericbandiero.dancerdata.R;
import com.ericbandiero.dancerdata.code.DancerDao;
import com.ericbandiero.dancerdata.code.StatData;
import com.ericbandiero.dancerdata.code.StatsAdapter;
import com.ericbandiero.librarymain.Lib_Base_ActionBarActivity;
import com.ericbandiero.librarymain.UtilsShared;

import java.util.Map;

public class StatsActivity extends Lib_Base_ActionBarActivity {

	GridView gridView;
	private DancerDao dancerDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Stats");
		setContentView(R.layout.activity_stats);
		gridView= (GridView) findViewById(R.id.statsGrid);

		dancerDao=new DancerDao(this);
		StatData statData=new StatData(dancerDao);
		Map<String, Integer> stringIntegerMap = statData.runStats();
		String[] strings;
		strings = StatData.formatStatDataForTwoColumnsArray(stringIntegerMap);
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","data length:"+strings.length);

		for (int i = 0; i < strings.length; i++) {
			String s = strings[i];
			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","String:"+s);
		}
		//UtilsShared.AlertMessageSimple(this,"Stats",string);

//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//				android.R.layout.simple_list_item_1, strings);

		StatsAdapter statsAdapter=new StatsAdapter(strings,this);

		gridView.setAdapter(statsAdapter);

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
									int position, long id) {
				if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">",v.toString());
				if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">",v.getClass().getName());
				if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Position:"+position);
				if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Row:"+position/2);

				String selectedItem = parent.getItemAtPosition(position).toString();
				if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">",selectedItem);
			//	Toast.makeText(getApplicationContext(),
			//			((TextView) v).getText(), Toast.LENGTH_SHORT).show();
			}
		});


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
}
