package com.ericbandiero.dancerdata.activities;

import android.content.Context;
import android.content.Intent;
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

public class HandleListClickForVenueCount implements IHandleListViewClicks,Serializable {

	@Inject
	DancerDao dancerDao;

	private static final long serialVersionUID = -5001699047268760417L;

	@Override
	public void handleClicks(AdapterView<?> adapterView, View view, int i, long l) {
		DanceApp.app().basicComponent().inject(this);
		DataHolderTwoFields dataHolderTwoFields= (DataHolderTwoFields) adapterView.getAdapter().getItem(i);
		dancerDao.getPerformanceForAVenue(dataHolderTwoFields.getId());
	}


}
