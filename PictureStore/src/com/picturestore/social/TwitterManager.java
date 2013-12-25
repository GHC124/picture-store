package com.picturestore.social;

import android.app.LoaderManager;
import android.content.Context;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebViewDatabase;

import com.picturestore.BaseApplication;
import com.picturestore.common.social.SocialListener;
import com.picturestore.prefs.UserPreferences;
import com.picturestore.social.twitter.Twitter;

public class TwitterManager {
	private final UserPreferences mUserPrefs = BaseApplication.getInstance()
			.getUserPreferences();
	private final Twitter mTwitter;
	private final Context mContext;
	private final SocialListener mSocialListener;

	public TwitterManager(Context context, LoaderManager loaderManager,
			SocialListener listener) {
		mContext = context;
		mSocialListener = listener;
		mTwitter = new Twitter(context, loaderManager, listener);
	}

	public boolean isLoggedIn() {
		return mUserPrefs.getTwitter();
	}

	public void promptForLogin() {
		mTwitter.authorize();
	}

	public void unlink() {
		mUserPrefs.setTwitter(false);
		mUserPrefs.setTwitterUserName(null);
		mUserPrefs.setTwitterUserId(null);
		mUserPrefs.setTwitterUserSecret(null);
		mUserPrefs.setTwitterUserToken(null);
		mUserPrefs.setSocialTwitter(false);

		// Edge case: an illegal state exception is thrown if an instance of
		// CookieSyncManager has not be created. CookieSyncManager is normally
		// created by a WebKit view, but this might happen if you start the
		// app, restore saved state, and click logout before running a UI
		// dialog in a WebView -- in which case the app crashes
		@SuppressWarnings("unused")
		CookieSyncManager cookieSyncMngr = CookieSyncManager
				.createInstance(mContext);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();

		// need to purge webview database; otherwise, credentials (e-mail &
		// password) are populated in FB login dialog.
		WebViewDatabase wvdb = WebViewDatabase.getInstance(mContext);
		wvdb.clearUsernamePassword();

		// send intent to let Second Screen know logout has completed
		sendLogoutMessage();
	}

	private void sendLogoutMessage() {
		if (mSocialListener != null) {
			mSocialListener.onLogout(SocialListener.SOCIAL_TWITTER, true);
		}
	}

	public void share(Bundle bundle) {
		mTwitter.share(bundle);
	}
}
