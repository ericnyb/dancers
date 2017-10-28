package com.ericbandiero.dancerdata.code;

import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Eric Bandiero on 10/28/2017.
 */

public interface IHandleListViewClicks {
	void handleClicks(AdapterView<?> parent, View v,
					  int position, long id);
}
