package com.picturestore.social;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
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
	public static final int FACEBOOK_ACTION_POST = 1;
	public static final String EXTRA_FACEBOOK_ACTION = "action";
	public static final String EXTRA_FACEBOOK_MESSAGE = "message";
	private static final boolean DEBUD = BaseApplication.getInstance()
			.loggingEnabled();

	// facebook errors per: http://fbdevwiki.com/wiki/Error_codes#FQL_Errors

	// { "error": {
	// "type": "OAuthException",
	// "message": "(#1500) The url you supplied is invalid",
	// "code": 1500
	// }
	// }
	private static final int FACEBOOK_INVALID_URL_ERROR = 1500;

	// { "error": {
	// "type":"OAuthException",
	// "message":"Error validating access token: User <abc> has not authorized application <def>.",
	// "error_subcode":458,
	// "code":190
	// }
	// }
	private static final int FACEBOOK_INVALID_ACCESS_TOKEN_ERROR = 190;

	private final Context mContext;
	private final LoaderManager mLoaderManager;
	private final UserPreferences mUserPrefs = BaseApplication.getInstance()
			.getUserPreferences();
	private final Facebook mFacebookClient = new Facebook(
			BaseConstants.FACEBOOK_APP_ID);
	private final SocialListener mSocialListener;
	private ProgressDialog mProgressDialog;
	private int mAction = -1;
	private String mMessage;

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

	public void share(Bundle bundle) {
		initDataFromExtras(bundle);
		initFacebookUserTokenAndSecret();
		doAction();
	}

	private void initDataFromExtras(Bundle extras) {
		if (extras != null) {
			if (extras.get(EXTRA_FACEBOOK_ACTION) != null) {
				mAction = extras.getInt(EXTRA_FACEBOOK_ACTION);
			}
			if (extras.get(EXTRA_FACEBOOK_MESSAGE) != null) {
				mMessage = extras.get(EXTRA_FACEBOOK_MESSAGE).toString();
			}
		}
	}

	private void initFacebookUserTokenAndSecret() {
		boolean valid = SessionStore.restore(mFacebookClient, mContext);
		if (!valid) {
			promptForLogin();
		}
	}

	private void doAction() {
		showProgress();
		if (mAction == FACEBOOK_ACTION_POST) {
			mLoaderManager.restartLoader(R.id.loader_social_facebook_post,
					null, this);
		}
	}

	private void showProgress() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage("Loading...");
		}
		mProgressDialog.show();
	}

	private void hideProgress() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
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
		case R.id.loader_social_facebook_post:
			loader = SocialLoader.postOnFacebook(mContext,
					mFacebookClient.getAccessToken(), mMessage);
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
					sendLoginMessage(name, id);
					if (mAction != -1) {
						doAction();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (result.getFailureReason() != null && DEBUD) {
				result.getFailureReason().printStackTrace();
			}
			mLoaderManager
					.destroyLoader(R.id.loader_social_get_facebook_username);
			break;
		case R.id.loader_social_facebook_post:
			if (result.isSuccessful()) {
				String responseValue = null;
				String exceptionType = null;
				int errorCode = -1;
				try {
					JSONObject jso = result.getData();
					responseValue = jso.getString("id");
					if (jso.has("error")) {
						JSONObject error = jso.getJSONObject("error");
						exceptionType = error.getString("type");
						errorCode = error.getInt("code");
					}
				} catch (JSONException e) {
					if (DEBUD) {
						e.printStackTrace();
					}
				}
				if (responseValue != null) {
					sendShareMessage(FACEBOOK_ACTION_POST, true, null);
				} else if (exceptionType != null
						&& exceptionType.equalsIgnoreCase("OAuthException")) {
					switch (errorCode) {
					case FACEBOOK_INVALID_URL_ERROR:
						// Note: on occasion we receive an invalid
						// poster url that we
						// attempt to post on facebook. sometimes
						// facebook accepts the
						// invalid url. sometimes it doesn't.

						// there is nothing we can do, other than
						// display the failure
						// to the user.
						sendShareMessage(FACEBOOK_ACTION_POST, false, null);
						break;
					case FACEBOOK_INVALID_ACCESS_TOKEN_ERROR:

						// 1) logout of facebook
						// 2) kick of facebook authentication
						// 3) dismiss "loading..." dialog
						// 4) return
						// 5) user has the ability to try and post
						// again.

						unlink();
						promptForLogin();
						break;
					default:
						// do nothing
						break;
					}
				}
			} else if (result.getFailureReason() != null && DEBUD) {
				result.getFailureReason().printStackTrace();
			}
			mLoaderManager.destroyLoader(R.id.loader_social_facebook_post);
			hideProgress();
			break;
		default:
			throw new IllegalArgumentException("Unknown loader id "
					+ loader.getId());
		}
	}

	@Override
	public void onLoaderReset(Loader<LoaderResult> arg0) {
	}

	private void sendLoginMessage(String name, String id) {
		if (mSocialListener != null) {
			mSocialListener.onLogin(SocialListener.SOCIAL_FACEBOOK, true);
		}
	}

	private void sendShareMessage(int action, boolean status, String message) {
		if (mSocialListener != null) {
			mSocialListener.onShare(SocialListener.SOCIAL_FACEBOOK, action,
					status);
		}
	}
}
