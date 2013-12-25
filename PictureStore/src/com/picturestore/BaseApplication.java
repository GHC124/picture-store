package com.picturestore;

import android.app.Application;
import android.content.Context;
import android.util.AndroidRuntimeException;

import com.picturestore.prefs.UserPreferences;

public class BaseApplication extends Application {
	private static BaseApplication INSTANCE;
	private UserPreferences mUserPreferences;
	private boolean mLoggingEnabled;
	
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		INSTANCE = this;
	}
	
	public static BaseApplication getInstance(){
		if (INSTANCE == null) {
			throw new AndroidRuntimeException("Application was not initialized");
		}
		return INSTANCE;
	}
	
	public UserPreferences getUserPreferences(){
		if(mUserPreferences == null){
			mUserPreferences = new UserPreferences(this);
		}
		return mUserPreferences;
	}
	
	public void enableLog() {
		mLoggingEnabled = true;
	}
	
	public boolean loggingEnabled() {
		return mLoggingEnabled;
	} 
}
