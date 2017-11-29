package com.ericbandiero.dancerdata.activities;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.ericbandiero.dancerdata.code.AppConstant;
import com.ericbandiero.dancerdata.code.DancerDao;
import com.ericbandiero.dancerdata.dagger.DanceApp;
import com.ericbandiero.librarymain.data_classes.DataHolderTwoFields;
import com.ericbandiero.librarymain.interfaces.IHandleListViewClicks;

import java.io.Serializable;

import javax.inject.Inject;

public class HandleListClickForVenueCountOrDancerCount implements IHandleListViewClicks,Serializable {

	@Inject
	DancerDao dancerDao;

	public static final String VENUE_COUNT="venue";
	public static final String DANCER_COUNT="dancer";
	private String clickForWhat;
	private static final long serialVersionUID = -5001699047268760417L;

	public HandleListClickForVenueCountOrDancerCount(String clickForWhat) {
		this.clickForWhat = clickForWhat;
	}

	@Override
	public void handleClicks(AdapterView<?> adapterView, View view, int i, long l) {

		DanceApp.app().basicComponent().inject(this);
		DataHolderTwoFields dataHolderTwoFields=(DataHolderTwoFields) adapterView.getAdapter().getItem(i);;
		switch (clickForWhat){
			case VENUE_COUNT:
				dancerDao.getPerformanceForAVenue(dataHolderTwoFields.getId());
				break;
			case DANCER_COUNT:
				System.out.println("Id for dance:"+dataHolderTwoFields.getId());
			break;
		}

	}


}
