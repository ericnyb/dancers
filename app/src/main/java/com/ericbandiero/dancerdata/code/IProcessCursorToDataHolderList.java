package com.ericbandiero.dancerdata.code;

import android.database.Cursor;

import com.ericbandiero.librarymain.data_classes.DataHolderTwoFields;

import java.util.List;

/**
 * Created by Eric Bandiero on 12/2/2017.
 */

public interface IProcessCursorToDataHolderList {
	List<DataHolderTwoFields> createListFromCursor(Cursor cursor);
}
