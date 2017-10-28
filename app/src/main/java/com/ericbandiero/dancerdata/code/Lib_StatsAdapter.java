package com.ericbandiero.dancerdata.code;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ericbandiero.dancerdata.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric Bandiero on 10/26/2017.
 */

public class Lib_StatsAdapter extends BaseAdapter {

	private List<DataHolderTwoFields> dataHolderTwoFields=new ArrayList<>();

	private int maxLengthOfDataField;
	private int maxLengthOfDataValue;
	private int totalLengthOfFieldAndValue;

	public Context context;

	public Lib_StatsAdapter(List<DataHolderTwoFields> dataHolderTwoFields, Context context) {
		this.context = context;
		this.dataHolderTwoFields=dataHolderTwoFields;
		maxLengthFromDataHolderTwoFields(this.dataHolderTwoFields);
	}

	public class ViewHolder {
		TextView textViewDataField;
		TextView textViewDataValue;
	}

	/**
	 * How many items are in the data set represented by this Adapter.
	 *
	 * @return Count of items.
	 */
	@Override
	public int getCount() {
		return dataHolderTwoFields.size();
	}

	/**
	 * Get the data item associated with the specified position in the data set.
	 *
	 * @param position Position of the item whose data we want within the adapter's
	 *                 data set.
	 * @return The data at the specified position.
	 */
	@Override
	public Object getItem(int position) {
		return dataHolderTwoFields.get(position);
	}

	/**
	 * Get the row id associated with the specified position in the list.
	 *
	 * @param position The position of the item within the adapter's data set whose row id we want.
	 * @return The id of the item at the specified position.
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Get a View that displays the data at the specified position in the data set. You can either
	 * create a View manually or inflate it from an XML layout file. When the View is inflated, the
	 * parent View (GridView, ListView...) will apply default layout parameters unless you use
	 * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
	 * to specify a root view and to prevent attachment to the root.
	 *
	 * @param position    The position of the item within the adapter's data set of the item whose view
	 *                    we want.
	 * @param convertView The old view to reuse, if possible. Note: You should check that this view
	 *                    is non-null and of an appropriate type before using. If it is not possible to convert
	 *                    this view to display the correct data, this method can create a new view.
	 *                    Heterogeneous lists can specify their number of view types, so that this View is
	 *                    always of the right type (see {@link #getViewTypeCount()} and
	 *                    {@link #getItemViewType(int)}).
	 * @param parent      The parent that this view will eventually be attached to
	 * @return A View corresponding to the data at the specified position.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder viewHolder;

		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Parent name:"+parent.getClass().getName());
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Parent width:"+parent.getWidth());

		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.stats_grid_items, parent,false);
			// Configure view holder
			viewHolder = new ViewHolder();
			viewHolder.textViewDataField = (TextView) rowView.findViewById(R.id.lib_textViewStatDataField);
			viewHolder.textViewDataValue = (TextView) rowView.findViewById(R.id.lib_textViewStatDataValue);

			rowView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if ((position)==1){
			//viewHolder.textViewData.setBackgroundColor(Color.YELLOW);
		}

		String field=dataHolderTwoFields.get(position).getField();
		String value=dataHolderTwoFields.get(position).getValue();

		String fieldFormatted = String.format("%1$-" +maxLengthOfDataField + "s", field);
		String valueFormatted=String.format("%1$" +maxLengthOfDataValue + "s", value);

		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Length of string:"+fieldFormatted.length());
		viewHolder.textViewDataField.setText(fieldFormatted);
		viewHolder.textViewDataValue.setText(valueFormatted);

		return rowView;
	}

	private void maxLengthFromDataHolderTwoFields(List<DataHolderTwoFields> dataHolderTwoFields){

		int maxLengthField=0;
		int maxLengthValue=0;


		for (DataHolderTwoFields dataHolderTwoField : dataHolderTwoFields) {

			int lengthField=dataHolderTwoField.getField().length();
			int lengthValue=dataHolderTwoField.getValue().length();

			if (lengthField>=maxLengthField){
				maxLengthField=lengthField;
			}

			if (lengthValue>=maxLengthValue){
				maxLengthValue=lengthValue;
			}

		}
		maxLengthOfDataField =maxLengthField+1;
		maxLengthOfDataValue=maxLengthValue;
		totalLengthOfFieldAndValue=maxLengthField+maxLengthValue;

		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Max length field:"+ maxLengthOfDataField);
		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Max length value:"+ maxLengthOfDataValue);

	}
}
