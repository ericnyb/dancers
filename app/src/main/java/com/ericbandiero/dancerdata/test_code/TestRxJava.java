package com.ericbandiero.dancerdata.test_code;

import android.util.Log;

import com.ericbandiero.dancerdata.code.AppConstant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Eric Bandiero on 11/24/2017.
 */

public class TestRxJava {
	private Disposable disposable;
	String test="1";
	public TestRxJava() {
		runner();
	}

	private void runner() {

		Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
															  @Override
															  public void subscribe(ObservableEmitter<String> e) throws Exception{
																  //Use onNext to emit each item in the stream//
																  e.onNext(testNetwork());
																  //Once the Observable has emitted all items in the sequence, call onComplete//
																  e.onComplete();
															  }
														  }
		);



		Observer<String> sub3=new Observer<String>() {
			/**
			 * Provides the Observer with the means of cancelling (disposing) the
			 * connection (channel) with the Observable in both
			 * synchronous (from within {@link #onNext(Object)}) and asynchronous manner.
			 *
			 * @param d the Disposable instance whose {@link Disposable#dispose()} can
			 *          be called anytime to cancel the connection
			 * @since 2.0
			 */
			@Override
			public void onSubscribe(Disposable d) {
				System.out.println("Subscribed...");
			}

			@Override
			public void onNext(String t) {
				System.out.println("Other observer on next:"+t);
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("We had an error:"+t.getMessage());
			}

			@Override
			public void onComplete() {
				System.out.println("Done with observable...");
				test="2";
				testValueShower(test);
			}
		};

		//disposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(s -> System.out.println("Result:"+s), s -> System.out.println(s), () -> System.out.println("Done"));
		observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(sub3);
		//System.out.println("Test value:"+test);
		//disposable.dispose();
	}

	private void testValueShower(String testp){
		System.out.println("Test value:"+testp);
	}
	private String testNetwork() throws IOException {
		String webPage = "", data = "";

		if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName() + ">", "Attempting network call made...");
			String link = "http://www.google.com";
			URL url = new URL(link);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((data = reader.readLine()) != null) {
				webPage += data + "\n";
			}
			return "We did a network call..";
	}
}
