package com.ericbandiero.dancerdata.activities;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.ericbandiero.dancerdata.code.AppConstant;
import com.ericbandiero.librarymain.interfaces.IHandleListViewClicks;

import java.io.Serializable;

public class HandleTestClick implements IHandleListViewClicks,Serializable {

	private static final long serialVersionUID = -5001699047268760417L;

	@Override
	public void handleClicks(AdapterView<?> adapterView, View view, int i, long l) {
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Test!");
	}
}
