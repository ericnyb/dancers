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

/** This will handle our clicks
 * Created by ${"Eric Bandiero"} on 4/12/2017.
 */

public class HandleAChildClick implements IHandleChildClicksExpandableIds,Serializable{
	private static final long serialVersionUID = -8044555321202914886L;
	@Inject
	DancerDao dancerDao;

	public static final String GET_PERFORMANCE_FROM_CLICK ="performance_from_click";
	public static final String GET_DANCE_DETAIL_FROM_CLICK ="dance_detail_from_click";

	private final String handleWhat;

	public HandleAChildClick(String handleWhat) {
		this.handleWhat = handleWhat;
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Click handle for:"+handleWhat);
	}

	@Override
	public void handleClicks(Context context, Lib_ExpandableDataWithIds group, Lib_ExpandableDataWithIds child) {
		DanceApp.app().basicComponent().inject(this);
		switch (handleWhat){
			case GET_DANCE_DETAIL_FROM_CLICK:
				if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Getting dance detail from a click");
				DetailActivity.setDance_id(Integer.parseInt(child.getAnyObject().toString()));
				Intent intent = new Intent(context, DetailActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				break;
			case GET_PERFORMANCE_FROM_CLICK:
				if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Getting a performance from a click");
				dancerDao.prepPerformanceActivity(child.getAnyObject().toString());
				//intentPerformance.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				//context.startActivity(intentPerformance);
				break;
		}

	}
}
