package com.ericbandiero.dancerdata.code;

import android.database.Cursor;
import android.util.Log;

import com.ericbandiero.dancerdata.AppConstant;
import com.ericbandiero.librarymain.data_classes.Lib_ExpandableDataWithIds;
import com.ericbandiero.librarymain.interfaces.IPrepDataExpandableList;

import java.util.ArrayList;
import java.util.List;

/**
 * We pass in a list that is set up
 * The ExpandListActivity will call the prepData which will just return the list we pass in
 * Created by ${"Eric Bandiero"} on 4/14/2017.
 */

public class PrepareCursorData implements IPrepDataExpandableList {

	List<Lib_ExpandableDataWithIds> list;

	public PrepareCursorData(List <Lib_ExpandableDataWithIds> c){
		this.list=c;
	}

	/**
	 * Return the list that we passed in - we do no work on it.
	 * @return
	 */
	@Override
	public List<Lib_ExpandableDataWithIds> prepData() {
/*

		List<Lib_ExpandableDataWithIds> listData=new ArrayList<>();

		List<Lib_ExpandableDataWithIds>listOfData=new ArrayList<>();

		listOfData.add(new Lib_ExpandableDataWithIds("PMT"));


		listOfData.add(new Lib_ExpandableDataWithIds("Bridge"));
		listOfData.add(new Lib_ExpandableDataWithIds("Bridge","Amiti"));
		listOfData.add(new Lib_ExpandableDataWithIds("Bridge","Amalgamate"));

		listOfData.add(new Lib_ExpandableDataWithIds("Flamenco Latino"));
		listOfData.add(new Lib_ExpandableDataWithIds("PMT","Pavan"));
		listOfData.add(new Lib_ExpandableDataWithIds("Flamenco Latino","Basilio"));
		listOfData.add(new Lib_ExpandableDataWithIds("Flamenco Latino","Aurora"));
*/

		//return listOfData;
		return list;
	}
}