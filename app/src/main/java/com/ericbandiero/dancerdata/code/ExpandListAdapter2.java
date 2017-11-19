package com.ericbandiero.dancerdata.code;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ericbandiero.librarymain.adapters.Lib_ExpandableListAdapter_With_Ids;
import com.ericbandiero.librarymain.data_classes.Lib_ExpandableDataWithIds;

import java.util.List;
import java.util.Map;

/**
 * Created by Eric Bandiero on 4/19/2017.
 */

public class ExpandListAdapter2 extends Lib_ExpandableListAdapter_With_Ids {
	public ExpandListAdapter2(Context context, List<Lib_ExpandableDataWithIds> listDataGroup, Map<Lib_ExpandableDataWithIds, List<Lib_ExpandableDataWithIds>> listDataChild) {
		super(context, listDataGroup, listDataChild);
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		super.getGroupView(groupPosition, isExpanded, convertView, parent);
		return convertView;
	}
}
