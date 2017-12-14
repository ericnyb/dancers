package com.ericbandiero.dancerdata.code;

import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;

import com.ericbandiero.dancerdata.activities.DetailActivity;
import com.ericbandiero.dancerdata.dagger.DanceApp;
import com.ericbandiero.librarymain.data_classes.DataHolderTwoFields;
import com.ericbandiero.librarymain.interfaces.IHandleRecyclerViewClick;

import java.io.Serializable;

import javax.inject.Inject;

/**
 * Created by Eric Bandiero on 12/14/2017.
 */

public class HandleClickForRecyclerVenueOrDancer implements IHandleRecyclerViewClick,Serializable {
	private static final long serialVersionUID = 8466680307604597342L;
	@Inject
	DancerDao dancerDao;

	public static final String VENUE_COUNT="venue";
	public static final String DANCER_COUNT="dancer";
	private String clickForWhat;

	public HandleClickForRecyclerVenueOrDancer(String clickForWhat) {
		this.clickForWhat = clickForWhat;
	}

	@Override
	public void onClick(View view, int i, Object object) {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Clicked new listener...");

		DanceApp.app().basicComponent().inject(this);
		DataHolderTwoFields dataHolderTwoFields=(DataHolderTwoFields) object;;
		switch (clickForWhat){
			case VENUE_COUNT:
				dancerDao.getPerformanceForAVenue(dataHolderTwoFields.getId());
				break;
			case DANCER_COUNT:
				System.out.println("Id for dancer:"+dataHolderTwoFields.getId());
				DetailActivity.setDancerdetailid(dataHolderTwoFields.getId().toString());
				NavUtils.navigateUpFromSameTask((Activity) view.getContext());
				break;
		}
	}
}

