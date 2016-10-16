package com.example.ConditionalAlarmClock;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

	private static Context context;

	public void onCreate() {
		super.onCreate();
		MyApplication.context = getApplicationContext();
	}

	@Deprecated
	public static Context getAppContext() {
		return MyApplication.context;
	}
}
