package com.ericbandiero.dancerdata.code;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ericbandiero.dancerdata.activities.DetailActivity;
import com.ericbandiero.dancerdata.dagger.DanceApp;
import com.ericbandiero.librarymain.data_classes.Lib_ExpandableDataWithIds;
import com.ericbandiero.librarymain.interfaces.IHandleChildClicksExpandableIds;

import java.io.Serializable;

import javax.inject.Inject;

/**
 * Created by ${"Eric Bandiero"} on 4/12/2017.
 */

public class HandleAChildClick implements IHandleChildClicksExpandableIds,Serializable{
	@Inject
	DancerDao dancerDao;

	public static final String VENUE_CLICK="venue";
	public static final String PERFORMANCE_CLICK="performance";

	private String handleWhat;

	public HandleAChildClick(String handleWhat) {
		this.handleWhat = handleWhat;
	}

	@Override
	public void handleClicks(Context context, Lib_ExpandableDataWithIds group, Lib_ExpandableDataWithIds child) {
		DanceApp.app().basicComponent().inject(this);
		switch (handleWhat){
			case PERFORMANCE_CLICK:
				if (AppConstant.DEBUG)
					Log.d(this.getClass().getSimpleName() + ">", "Group:" + group.getTextStringGroup());
				if (AppConstant.DEBUG)
					Log.d(this.getClass().getSimpleName() + ">", "child:" + child.getTextStringChild());
				if (AppConstant.DEBUG)
					Log.d(this.getClass().getSimpleName() + ">", "Dance piece code:" + child.getAnyObject());

				DetailActivity.setDance_id(Integer.parseInt(child.getAnyObject().toString()));
				Intent intent = new Intent(context, DetailActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				break;
			case VENUE_CLICK:
				if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Child info:"+child.getAnyObject());
				if (AppConstant.DEBUG)
					Log.d(this.getClass().getSimpleName() + ">", "child:" + child.getTextStringChild());
				Intent intentPerformance = dancerDao.prepPerformanceActivity(child.getAnyObject().toString());
				intentPerformance.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intentPerformance);
				break;
		}

	}
}
