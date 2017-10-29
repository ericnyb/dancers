package com.ericbandiero.dancerdata.code;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.ericbandiero.dancerdata.R;
import com.ericbandiero.librarymain.adapters.Lib_StatsAdapter;
import com.ericbandiero.librarymain.interfaces.IControlStatAdapter;

import java.io.Serializable;

/**
 * Created by Eric Bandiero on 10/29/2017.
 */

public class ControlStatAdapter implements IControlStatAdapter,Serializable {

	@Override
	public void customize(Context context, Lib_StatsAdapter lib_statsAdapter) {
		//lib_statsAdapter.setColorTextField(ContextCompat.getColor(context, R.color.LightSteelBlue));
		lib_statsAdapter.setColorTextValue(ContextCompat.getColor(context, R.color.LightSalmon));
		//lib_statsAdapter.setTotalBackColorTotalValue(ContextCompat.getColor(context, R.color.LightGreen));
	}
}
