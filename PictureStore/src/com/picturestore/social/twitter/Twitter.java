package com.picturestore.social.twitter;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.picturestore.BaseApplication;
import com.picturestore.BaseConstants;
import com.picturestore.R;
import com.picturestore.common.social.SocialListener;
import com.picturestore.loader.LoaderResult;
import com.picturestore.prefs.UserPreferences;
import com.picturestore.social.SocialLoader;

public class Twitter implements LoaderManager.LoaderCallbacks<LoaderResult> {
	public static final String TWITTER_REQUEST_TOKEN_URL = "https://twitter.com/oauth/request_token";
	public static final String TWITTER_ACCESS_TOKEN_URL = "https://twitter.com/oauth/access_token";
	public static final String TWITTER_AUTHORIZE_URL = "https://twitter.com/oauth/authorize";

	private static final String TAG = Twitter.class.getSimpleName();

	// Login
	public static final String USER_TOKEN = "user_token";
	public static final String USER_SECRET = "user_secret";

	public static final String PROFILE_NAME = "twitter_name";

	private static final Uri CALLBACKURI = Uri.parse("ghc://twitt");
	private static final String TWITTER_LOGIN_SUCCESS_INTENT = "twitter_login_success";

	// Share
	public static final String EXTRA_TWITTER_ACTION = "action";
	public static final String EXTRA_TWITTER_USERNAME = "userName";
	public static final String EXTRA_TWITTER_MESSAGE = "message";
	public static final String EXTRA_TWITTER_TWEET_ID = "tweetId";

	private static final String TWITTER_SHARE_SUCCESS_INTENT = "twitter_share_success";

	private static final int TWITTER_NETWORK_SUCCESS = 100;
	private static final int TWITTER_NETWORK_FAILURE = 101;

	private static final int TWITTER_ACTION_REPLY = 1;
	private static final int TWITTER_ACTION_RETWEET = 2;
	private static final int TWITTER_ACTION_FAVORITE = 3;

	// TimeStamp of request
	public static String mTimestamp;

	private int mAction = 0;
	private String mTagId = null;
	private String mBuzzId = null;
	private String mUserName = null;
	private String mMessage = null;
	private String mTweetId = null;
	//
	private final UserPreferences mUserPrefs = BaseApplication.getInstance()
			.getUserPreferences();

	private OAuthConsumer mConsumer;
	private OAuthProvider mProvider;

	private boolean DEBUG = BaseApplication.getInstance().loggingEnabled();
	private final Context mContext;
	private final LoaderManager mLoaderManager;
	private final SocialListener mSocialListener;
	private ProgressDialog mProgressDialog;
	private boolean mIsTryGetTime = false;
	private int mPreviousAction = 0;
	private Bundle mPreviousBundle = null;

	public Twitter(Context context, LoaderManager loaderManager,
			SocialListener listener) {
		mConsumer = new TwitterHttpOAuthConsumer(
				BaseConstants.TWITTER_CONSUMER_KEY,
				BaseConstants.TWITTER_CONSUMER_SECRET);

		mProvider = new CommonsHttpOAuthProvider(TWITTER_REQUEST_TOKEN_URL,
				TWITTER_ACCESS_TOKEN_URL, TWITTER_AUTHORIZE_URL);
		mProvider.setOAuth10a(true);

		mContext = context;
		mLoaderManager = loaderManager;
		mSocialListener = listener;
		
		if (mTimestamp == null) {
			mTimestamp = String.valueOf(System.currentTimeMillis() / 1000);
		}
	}

	public void authorize() {
		showProgress();
		mLoaderManager.restartLoader(R.id.loader_social_retrieve_request_token,
				null, this);
	}

	public void share(Bundle bundle) {
		initTwitterUserTokenAndSecret();

		initDataFromExtras(bundle);

		doAction();
	}

	private void doAction() {
	}

	private TwitterDialogListener mDialogListener = new TwitterDialogListener() {

		@Override
		public void onError(Bundle bundle) {
			if (DEBUG) {
				int code = bundle.getInt("code");
				String description = bundle.getString("description");
				Log.d(TAG, "Retrieve request token FAIL. Code " + code + " - "
						+ description);
			}
		}

		@Override
		public void onComplete(Bundle bundle) {
			String url = bundle.getString("url");
			retrieveAccessToken(Uri.parse(url));
		}
	};

	private void initTwitterUserTokenAndSecret() {
		if (DEBUG) {
			Log.d(TAG, "Init Twitter Token");
		}
		mConsumer.setTokenWithSecret(mUserPrefs.getTwitterUserToken(),
				mUserPrefs.getTwitterUserSecret());
	}

	private void initDataFromExtras(Bundle extras) {
		if (extras != null) {
			if (extras.get(EXTRA_TWITTER_ACTION) != null) {
				mAction = extras.getInt(EXTRA_TWITTER_ACTION);
			}

			if (extras.get(EXTRA_TWITTER_USERNAME) != null) {
				mUserName = extras.get(EXTRA_TWITTER_USERNAME).toString();
			}

			if (extras.get(EXTRA_TWITTER_MESSAGE) != null) {
				mMessage = extras.get(EXTRA_TWITTER_MESSAGE).toString();
			}

			if (extras.get(EXTRA_TWITTER_TWEET_ID) != null) {
				mTweetId = extras.get(EXTRA_TWITTER_TWEET_ID).toString();
			}
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

	@Override
	public Loader<LoaderResult> onCreateLoader(int id, Bundle args) {
		Loader<LoaderResult> loader;
		switch (id) {
		case R.id.loader_social_retrieve_request_token:
			loader = SocialLoader.retrieveTwitterRequestToken(mContext,
					mConsumer, mProvider, CALLBACKURI.toString());
			break;
		case R.id.loader_social_retrieve_access_token:
			loader = SocialLoader.retrieveTwitterAccessToken(mContext,
					mConsumer, mProvider,
					args != null ? args.getString("verifier", "") : "");
			break;
		case R.id.loader_social_get_twitter_username:
			loader = SocialLoader.getTwitterUserInfo(mContext,
					mConsumer);
			break;
		case R.id.loader_social_twitter_time:
			loader = SocialLoader.retrieveTwitterTime(mContext);
			break;
		default:
			throw new IllegalArgumentException("Unknown loader id " + id);
		}
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<LoaderResult> loader, LoaderResult result) {
		if (!result.isSuccessful() && !mIsTryGetTime) {
			if (result.getFailureReason() != null) {
				result.getFailureReason().printStackTrace();
			}
			mIsTryGetTime = true;
			// Try to get twitter time
			if (DEBUG) {
				Log.d(TAG, "Try to get twitter time.");
			}
			showProgress();
			// Store previous action
			mPreviousAction = loader.getId();
			mLoaderManager.restartLoader(R.id.loader_social_twitter_time, null,
					this);

			return;
		}
		switch (loader.getId()) {

		case R.id.loader_social_retrieve_request_token:
			hideProgress();
			if (result.isSuccessful()) {

				String authUrl = result.getData();
				if (authUrl != null) {
					mUserPrefs.setTwitterRequestToken(mConsumer.getToken());
					mUserPrefs.setTwitterRequestSecret(mConsumer
							.getTokenSecret());
					TwitterDialog.getInstance(mContext, mDialogListener)
							.showLogin(authUrl);
				}
			} else if (result.getFailureReason() != null) {
				result.getFailureReason().printStackTrace();
			}

			mLoaderManager
					.destroyLoader(R.id.loader_social_retrieve_request_token);
			break;

		case R.id.loader_social_retrieve_access_token:
			if (result.isSuccessful()) {
				String token = mConsumer.getToken();
				String secret = mConsumer.getTokenSecret();

				if (!(TextUtils.isEmpty(token) && TextUtils.isEmpty(secret))) {
					mUserPrefs.setTwitter(true);
				}

				mUserPrefs.setTwitterUserToken(token);
				mUserPrefs.setTwitterUserSecret(secret);

				mLoaderManager.restartLoader(
						R.id.loader_social_get_twitter_username, null, this);
			} else if (result.getFailureReason() != null) {
				result.getFailureReason().printStackTrace();
				hideProgress();
			}
			mLoaderManager
					.destroyLoader(R.id.loader_social_retrieve_access_token);
			break;
		case R.id.loader_social_get_twitter_username:
			if (result.isSuccessful()) {
				try {
					JSONObject jso = result.getData();
					String name = jso.getString("name");
					String id = jso.getString("id_str");
					mUserPrefs.setTwitterUserName(name);
					mUserPrefs.setTwitterUserId(id);
					sendLoginMessage(name, id);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (result.getFailureReason() != null) {
				result.getFailureReason().printStackTrace();
			}
			hideProgress();
			mLoaderManager
					.destroyLoader(R.id.loader_social_get_twitter_username);
			break;
		case R.id.loader_social_twitter_time:
			if (result.isSuccessful()) {
				Long time = result.getData();
				if (time != null) {
					mTimestamp = String.valueOf(time);
					// Do previous action
					mLoaderManager.restartLoader(mPreviousAction,
							mPreviousBundle, this);
				} else if (DEBUG) {
					Log.d(TAG, "Fail to get twitter time!");
				}
			} else if (result.getFailureReason() != null) {
				result.getFailureReason().printStackTrace();
			}
			mLoaderManager.destroyLoader(R.id.loader_social_twitter_time);
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<LoaderResult> loader) {

	}

	private void sendLoginMessage(String name, String id) {
		if (mSocialListener != null) {
			mSocialListener.onLogin(SocialListener.SOCIAL_TWITTER, true);
		}
	}

	private void sendShareMessage(int action, int status, String message) {

	}

	private void retrieveAccessToken(Uri uri) {
		if (uri != null) {
			if (CALLBACKURI.getScheme().equals(uri.getScheme())) {
				if (DEBUG) {
					Log.e("TwitterOauth", "Response URI : " + uri.getPath());
					Log.e("TwitterOauth", "Response URI : " + uri.toString());
				}
				String token = mUserPrefs.getTwitterRequestToken();
				String secret = mUserPrefs.getTwitterRequestSecret();

				try {
					if (!(token == null || secret == null)) {
						mConsumer.setTokenWithSecret(token, secret);
					}
					String oauthtoken = uri
							.getQueryParameter(OAuth.OAUTH_TOKEN);
					String verifier = uri
							.getQueryParameter(OAuth.OAUTH_VERIFIER);

					if (oauthtoken != null) {
						showProgress();

						mPreviousBundle = new Bundle();
						mPreviousBundle.putString("verifier", verifier);
						mLoaderManager.restartLoader(
								R.id.loader_social_retrieve_access_token,
								mPreviousBundle, this);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Get error[]: [0] = message, [1] = code
	private String[] getTwitterError(JSONObject jsonObj) {
		String errorMessage = null;
		int errorCode = -1;

		if (jsonObj.has("errors")) {
			try {
				String errorStr = jsonObj.getString("errors");
				if (errorStr.indexOf("[") == 0) {
					JSONObject error = jsonObj.getJSONArray("errors")
							.getJSONObject(0);
					errorMessage = error.getString("message");
					errorCode = error.getInt("code");
				} else {
					errorMessage = errorStr;
					errorCode = 403;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (errorMessage != null && errorCode != -1) {
			return new String[] { errorMessage, String.valueOf(errorCode) };
		}
		return null;
	}

	class TwitterHttpOAuthConsumer extends CommonsHttpOAuthConsumer {
		private static final long serialVersionUID = 1L;

		public TwitterHttpOAuthConsumer(String consumerKey,
				String consumerSecret) {
			super(consumerKey, consumerSecret);
		}

		@Override
		protected String generateTimestamp() {
			return mTimestamp;
		}
	}
}
