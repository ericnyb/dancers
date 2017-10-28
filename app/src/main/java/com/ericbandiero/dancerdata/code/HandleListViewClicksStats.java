package com.ericbandiero.dancerdata.code;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.io.Serializable;

/**
 * This is a custom click listener
 * Created by Eric Bandiero on 10/28/2017.
 */

public class HandleListViewClicksStats implements IHandleListViewClicks,Serializable {

	private static final long serialVersionUID = 3660355694614613684L;

	@Override
	public void handleClicks(AdapterView<?> parent, View v, int position, long id) {
		if (com.ericbandiero.dancerdata.AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Parent parameter:"+parent.getClass().getName());
		if (com.ericbandiero.dancerdata.AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Parent is listView- count:"+parent.getCount());
		if (com.ericbandiero.dancerdata.AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","View parameter is layout:"+v.toString());
		//We could do something like this:
		/*
		((LinearLayout)v).getChildAt(1).setBackgroundColor(Color.GREEN);
		((LinearLayout)v).setBackgroundColor(Color.YELLOW);
		*/
		if (com.ericbandiero.dancerdata.AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","View layout name:"+v.getClass().getName());
		if (com.ericbandiero.dancerdata.AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Position:"+position);
		if (com.ericbandiero.dancerdata.AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Row:"+position/2);
		String selectedItem = parent.getItemAtPosition(position).toString();
		if (com.ericbandiero.dancerdata.AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Selected item:"+selectedItem);
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Concrete class of IHandleListViewClicks");
	}
}
