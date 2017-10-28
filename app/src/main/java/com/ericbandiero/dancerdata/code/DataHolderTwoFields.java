package com.ericbandiero.dancerdata.code;

import java.io.Serializable;

/**
 * Created by Eric Bandiero on 10/27/2017.
 */

public class DataHolderTwoFields implements Serializable {
	private static final long serialVersionUID = -3664907858807961798L;
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

	@Override
	public String toString() {
		return "DataHolderTwoFields{" +
				"field='" + field + '\'' +
				", value='" + value + '\'' +
				'}';
	}
}
