package com.picturestore;

import android.app.Application;
import android.content.Context;
import android.util.AndroidRuntimeException;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.picturestore.prefs.UserPreferences;
import com.picturestore.util.BitmapLruCache;

public class BaseApplication extends Application {
	/**
	 * 20% of the heap goes to image cache. Stored in kiloibytes.
	 */
	private static final int IMAGE_CACHE_SIZE_KB = (int) (Runtime.getRuntime()
			.maxMemory() / 1024 / 5);

	private static BaseApplication INSTANCE;
	private UserPreferences mUserPreferences;
	private boolean mLoggingEnabled;
	private ImageLoader mImageLoader;

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		INSTANCE = this;
	}

	public static BaseApplication getInstance() {
		if (INSTANCE == null) {
			throw new AndroidRuntimeException("Application was not initialized");
		}
		return INSTANCE;
	}

	public UserPreferences getUserPreferences() {
		if (mUserPreferences == null) {
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

	public ImageLoader getImageLoader() {
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(Volley.newRequestQueue(this),
					new BitmapLruCache(IMAGE_CACHE_SIZE_KB));
		}
		return mImageLoader;
	}
}
