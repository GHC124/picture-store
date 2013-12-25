package com.picturestore.social;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.webkit.WebViewDatabase;

import com.picturestore.BaseApplication;
import com.picturestore.BaseConstants;
import com.picturestore.R;
import com.picturestore.common.social.SocialListener;
import com.picturestore.loader.LoaderResult;
import com.picturestore.prefs.UserPreferences;
import com.picturestore.social.facebook.DialogError;
import com.picturestore.social.facebook.Facebook;
import com.picturestore.social.facebook.Facebook.DialogListener;
import com.picturestore.social.facebook.FacebookError;
import com.picturestore.social.facebook.SessionStore;
import com.picturestore.social.facebook.Util;

public class FacebookManager implements
		LoaderManager.LoaderCallbacks<LoaderResult> {
	private final Context mContext;
	private final LoaderManager mLoaderManager;
	private final UserPreferences mUserPrefs = BaseApplication.getInstance()
			.getUserPreferences();
	private final Facebook mFacebookClient = new Facebook(
			BaseConstants.FACEBOOK_APP_ID);
	private final SocialListener mSocialListener;

	public FacebookManager(Context context, LoaderManager loaderManager,
			SocialListener socialListener) {
		mContext = context;
		mLoaderManager = loaderManager;
		mSocialListener = socialListener;
	}

	public boolean isLoggedIn() {
		return SessionStore.restore(mFacebookClient, mContext);
	}

	public void promptForLogin() {
		mFacebookClient.authorize((Activity) mContext, new String[] {
				"offline_access", "publish_stream", "user_likes" }, -1,
				new LoginDialogListener());
	}

	public void unlink() {
		clearFacebookUserPrefs();

		SessionStore.clear(mContext);

		Util.clearCookies(mContext);

		// need to purge webview database; otherwise, credentials (e-mail &
		// password) are populated in FB login dialog.
		WebViewDatabase wvdb = WebViewDatabase.getInstance(mContext);
		wvdb.clearUsernamePassword();

		sendLogoutMessage();
	}

	private void sendLogoutMessage() {
		if (mSocialListener != null) {
			mSocialListener.onLogout(SocialListener.SOCIAL_FACEBOOK, true);
		}
	}

	private final class LoginDialogListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {
			mUserPrefs.setFacebook(true);
			SessionStore.save(mFacebookClient, mContext);
			mLoaderManager.initLoader(R.id.loader_social_get_facebook_username,
					null, getFacebookManager());
		}

		@Override
		public void onFacebookError(FacebookError error) {
			handleError();
		}

		@Override
		public void onError(DialogError error) {
			handleError();
		}

		@Override
		public void onCancel() {
			handleError();
		}

		private void handleError() {
			clearFacebookUserPrefs();
			SessionStore.save(mFacebookClient, mContext);
		}
	}

	private void clearFacebookUserPrefs() {
		mUserPrefs.setFacebook(false);
		mUserPrefs.setFacebookUserToken(null);
		mUserPrefs.setFacebookUserId(null);
		mUserPrefs.setSocialFacebook(false);
	}

	private FacebookManager getFacebookManager() {
		return this;
	}

	@Override
	public Loader<LoaderResult> onCreateLoader(int id, Bundle args) {
		Loader<LoaderResult> loader;
		switch (id) {
		case R.id.loader_social_get_facebook_username:
			loader = SocialLoader.getFacebookUsername(mContext,
					mFacebookClient.isSessionValid(),
					mFacebookClient.getAccessToken());
			break;
		default:
			throw new IllegalAccessError("Unknown loader id " + id);
		}
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<LoaderResult> loader, LoaderResult result) {
		switch (loader.getId()) {
		case R.id.loader_social_get_facebook_username:
			if (result.isSuccessful()) {
				try {
					JSONObject jso = result.getData();
					String name = jso.getString("name");
					String id = jso.getString("id");
					mUserPrefs.setFacebookUserName(name);
					mUserPrefs.setFacebookUserId(id);
					sendMessage(name, id);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (result.getFailureReason() != null) {
				result.getFailureReason().printStackTrace();
			}
			mLoaderManager
					.destroyLoader(R.id.loader_social_get_facebook_username);
			break;
		default:
			throw new IllegalArgumentException("Unknown loader id "
					+ loader.getId());
		}
	}

	@Override
	public void onLoaderReset(Loader<LoaderResult> arg0) {
	}

	private void sendMessage(String name, String id) {
		if (mSocialListener != null) {
			mSocialListener.onLogin(SocialListener.SOCIAL_FACEBOOK, true);
		}
	}
}
