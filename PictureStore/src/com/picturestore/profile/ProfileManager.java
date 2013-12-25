package com.picturestore.profile;

import android.app.Activity;
import android.app.Fragment;
import android.util.Log;

import com.picturestore.BaseApplication;
import com.picturestore.common.social.SocialListener;
import com.picturestore.prefs.UserPreferences;
import com.picturestore.social.FacebookManager;
import com.picturestore.social.TwitterManager;

public class ProfileManager {

	private static final String TAG = ProfileManager.class.getSimpleName();
	private static final boolean DEBUG = BaseApplication.getInstance()
			.loggingEnabled();
	private static final int PROMPT_TWITTER_LOGIN = 1;
	private static final int PROMPT_FACEBOOK_LOGIN = 2;

	private final Activity mActivity;
	private final Fragment mFragment;
	private final SocialListener mListener;

	private ProfileManager(Activity activity, Fragment fragment,
			SocialListener listener) {
		mActivity = activity;
		mFragment = fragment;
		mListener = listener;
	}

	public static void initialize(Activity activity, Fragment fragment,
			SocialListener listener) {
		new ProfileManager(activity, fragment, listener).initialize(false);
	}

	public static boolean checkTwitterAuth(Activity activity,
			Fragment fragment, SocialListener listener) {
		final UserPreferences userPreferences = BaseApplication.getInstance()
				.getUserPreferences();
		if (userPreferences.getTwitter()) {
			return true;
		}
		new ProfileManager(activity, fragment, listener)
				.promptForTwitterLogin();
		return false;
	}

	public static boolean checkFacebookAuth(Activity activity,
			Fragment fragment, SocialListener listener) {
		final UserPreferences userPreferences = BaseApplication.getInstance()
				.getUserPreferences();
		if (userPreferences.getFacebook()) {
			return true;
		}
		new ProfileManager(activity, fragment, listener)
				.promptForFacebookLogin();
		return false;
	}

	public static void sendTwitterLogin(Activity activity, Fragment fragment,
			SocialListener listener) {
		new ProfileManager(activity, fragment, listener).sendMessage(
				PROMPT_TWITTER_LOGIN);
	}

	public static void sendFacebookLogin(Activity activity, Fragment fragment,
			SocialListener listener) {
		new ProfileManager(activity, fragment, listener).sendMessage(
				PROMPT_FACEBOOK_LOGIN);
	}

	public static boolean checkTwitterAuth(Activity activity) {
		final UserPreferences userPreferences = BaseApplication.getInstance()
				.getUserPreferences();
		if (userPreferences.getTwitter()) {
			return true;
		}
		return false;
	}

	public static boolean checkFacebookAuth(Activity activity) {
		final UserPreferences userPreferences = BaseApplication.getInstance()
				.getUserPreferences();
		if (userPreferences.getFacebook()) {
			return true;
		}
		return false;
	}

	public static void logoutFacebook(Activity activity, Fragment fragment, SocialListener listener) {
		new FacebookManager(activity, null, listener).unlink();
	}

	public static void logoutTwitter(Activity activity, Fragment fragment, SocialListener listener) {
		new TwitterManager(activity, null, listener).unlink();
	}

	private void initialize(boolean promptForLogin) {
		if (DEBUG) {
			Log.d(TAG, "initialize");
		}
	}

	private void promptForTwitterLogin() {
		LoginDialog.getInstance(mActivity, mFragment, mListener).showTwitter();
	}

	private void promptForFacebookLogin() {
		LoginDialog.getInstance(mActivity, mFragment, mListener).showFacebook();
	}

	private void promptForLogin() {
		LoginDialog.getInstance(mActivity, mFragment, mListener).show();
	}

	private void sendMessage(int action) {
		switch (action) {
		case PROMPT_FACEBOOK_LOGIN:
			new FacebookManager(mActivity, mActivity.getLoaderManager(),
					mListener).promptForLogin();
			break;
		case PROMPT_TWITTER_LOGIN:
			new TwitterManager(mActivity, mActivity.getLoaderManager(),
					mListener).promptForLogin();
			break;
		}
	}
}
