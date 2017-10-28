package com.ericbandiero.dancerdata.code;

/**
 * Created by Eric Bandiero on 10/27/2017.
 */

public class DataHolderTwoFields {
	private String field;
	private String value;

	public DataHolderTwoFields(String field, String value) {
		this.field = field;
		this.value = value;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
