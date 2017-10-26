package com.ericbandiero.dancerdata.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ericbandiero.dancerdata.R;
import com.ericbandiero.librarymain.Lib_Base_ActionBarActivity;

public class StatsActivity extends Lib_Base_ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);
	}
}
