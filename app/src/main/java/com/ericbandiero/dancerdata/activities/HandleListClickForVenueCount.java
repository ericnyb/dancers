package com.ericbandiero.dancerdata.activities;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.ericbandiero.dancerdata.code.AppConstant;
import com.ericbandiero.librarymain.data_classes.DataHolderTwoFields;
import com.ericbandiero.librarymain.interfaces.IHandleListViewClicks;

import java.io.Serializable;

public class HandleListClickForVenueCount implements IHandleListViewClicks,Serializable {

	private static final long serialVersionUID = -5001699047268760417L;

	@Override
	public void handleClicks(AdapterView<?> adapterView, View view, int i, long l) {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Test!");
		DataHolderTwoFields dataHolderTwoFields= (DataHolderTwoFields) adapterView.getAdapter().getItem(i);
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Venue id:"+dataHolderTwoFields.getId());
		DetailActivity.setDance_id(1733);
		Intent intent = new Intent(AppConstant.CONTEXT, DetailActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		AppConstant.CONTEXT.startActivity(intent);
	}
}
