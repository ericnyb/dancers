package com.ericbandiero.dancerdata.activities;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ericbandiero.dancerdata.AppConstant;
import com.ericbandiero.dancerdata.R;
import com.ericbandiero.dancerdata.code.DancerDao;
import com.ericbandiero.dancerdata.code.DataHolderTwoFields;
import com.ericbandiero.dancerdata.code.IHandleListViewClicks;
import com.ericbandiero.dancerdata.code.Lib_StatsAdapter;
import com.ericbandiero.librarymain.Lib_Base_ActionBarActivity;

import java.util.List;

public class Lib_StatsActivity extends Lib_Base_ActionBarActivity {

	public static final String EXTRA_HEADER="header_text";
	public static final String EXTRA_TITLE="title_text";
	public static final String EXTRA_DATA_HOLDER_TWO_FIELDS ="data_holder";
	public static final String EXTRA_DATA_CLICK_COMMAND ="click_command";


	private ListView listView;
	private TextView textViewHeader;
	private DancerDao dancerDao;
	private IHandleListViewClicks clicks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Data Viewer");
		setContentView(R.layout.lib_activity_stats);
		listView = (ListView) findViewById(R.id.lib_listViewStats);
		textViewHeader=(TextView)findViewById(R.id.lib_textViewStatDataHeader);

		String stringExtraTitle = getIntent().getStringExtra(EXTRA_TITLE);
		String stringExtraHeader = getIntent().getStringExtra(EXTRA_HEADER);
		List<DataHolderTwoFields> dataHolderTwoFields= (List<DataHolderTwoFields>) getIntent().getSerializableExtra(EXTRA_DATA_HOLDER_TWO_FIELDS);
		clicks=(IHandleListViewClicks) getIntent().getSerializableExtra(EXTRA_DATA_CLICK_COMMAND);

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

		Lib_StatsAdapter libStatsAdapter =new Lib_StatsAdapter(dataHolderTwoFields,this);

		listView.setAdapter(libStatsAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
									int position, long id) {

				if (clicks!=null){
					clicks.handleClicks(parent,v,position,id);
				}
				else
				{
					if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","No click handler was passed in.");
				}
			}
		});


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
}
