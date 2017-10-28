package com.ericbandiero.dancerdata.activities;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.ericbandiero.dancerdata.AppConstant;
import com.ericbandiero.dancerdata.R;
import com.ericbandiero.dancerdata.code.DancerDao;
import com.ericbandiero.dancerdata.code.DataHolderTwoFields;
import com.ericbandiero.dancerdata.code.StatData;
import com.ericbandiero.dancerdata.code.StatsAdapter;
import com.ericbandiero.librarymain.Lib_Base_ActionBarActivity;

import java.util.List;
import java.util.Map;

public class StatsActivity extends Lib_Base_ActionBarActivity {

	public static final String EXTRA_HEADER="header_text";
	public static final String EXTRA_TITLE="title_text";

	private ListView listView;
	private TextView textViewHeader;
	private DancerDao dancerDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Data Viewer");
		setContentView(R.layout.activity_stats);
		listView = (ListView) findViewById(R.id.listViewStats);
		textViewHeader=(TextView)findViewById(R.id.textViewStatDataHeader);

		String stringExtraTitle = getIntent().getStringExtra(EXTRA_TITLE);
		String stringExtraHeader = getIntent().getStringExtra(EXTRA_HEADER);

		if (stringExtraHeader==null){
			textViewHeader.setVisibility(View.GONE);
		}
		else{
			SpannableString content = new SpannableString(stringExtraHeader);
			content.setSpan(new UnderlineSpan(), 0, stringExtraHeader.length(), 0);
			textViewHeader.setText(content);
		}

		if (stringExtraTitle!=null){
			setTitle(stringExtraTitle);
		}

		dancerDao=new DancerDao(this);
		StatData statData=new StatData(dancerDao);
		List<DataHolderTwoFields> dataHolderTwoFields = statData.runStats();
		//String[] strings;
		//strings = StatData.formatStatDataForTwoColumnsArray(dataHolderTwoFields);
		//if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","data length:"+strings.length);

//		for (int i = 0; i < strings.length; i++) {
//			String s = strings[i];
//			if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","String:"+s);
//		}
		//UtilsShared.AlertMessageSimple(this,"Stats",string);

//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//				android.R.layout.simple_list_item_1, strings);

		StatsAdapter statsAdapter=new StatsAdapter(dataHolderTwoFields,this);

		listView.setAdapter(statsAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
