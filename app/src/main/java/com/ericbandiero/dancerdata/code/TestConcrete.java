package com.ericbandiero.dancerdata.code;

import android.os.Parcel;
import android.os.Parcelable;

import com.ericbandiero.librarymain.interfaces.ITestParce;

/**
 * Created by Eric Bandiero on 4/26/2017.
 */

public class TestConcrete implements ITestParce,Parcelable {

	public TestConcrete() {

	}

	@Override
	public void doSomething() {
		System.out.println("Hi from parce data");
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {

	}

	private TestConcrete(Parcel in) {
			super();
	}


	public static final Parcelable.Creator<TestConcrete> CREATOR = new Parcelable.Creator<TestConcrete>() {
		public TestConcrete createFromParcel(Parcel in) {
			return new TestConcrete(in);
		}

		public TestConcrete[] newArray(int size) {
			return new TestConcrete[size];
		}
	};
}
