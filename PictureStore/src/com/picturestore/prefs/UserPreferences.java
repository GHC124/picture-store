package com.picturestore.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserPreferences {
	/** holds application shared preferences. */
	private final SharedPreferences mSharedPrefs;

	public UserPreferences(Context context) {
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public SharedPreferences getSharedPreferences() {
		return mSharedPrefs;
	}

	public boolean getTwitter() {
		return mSharedPrefs.getBoolean(UserPreferencesConstants.TWITTER, false);
	}

	public void setTwitter(boolean b) {
		mSharedPrefs.edit().putBoolean(UserPreferencesConstants.TWITTER, b)
				.apply();
	}

	public void setTwitterUserName(String userName) {
		mSharedPrefs
				.edit()
				.putString(UserPreferencesConstants.TWITTER_USER_USERNAME,
						userName).apply();
	}

	public String getTwitterUserName() {
		return mSharedPrefs.getString(
				UserPreferencesConstants.TWITTER_USER_USERNAME, null);
	}

	public void setTwitterUserId(String id) {
		mSharedPrefs.edit()
				.putString(UserPreferencesConstants.TWITTER_USER_ID, id)
				.apply();
	}

	public void setTwitterUserSecret(String secret) {
		mSharedPrefs
				.edit()
				.putString(UserPreferencesConstants.TWITTER_USER_SECECT, secret)
				.apply();
	}

	public void setTwitterUserToken(String token) {
		mSharedPrefs.edit()
				.putString(UserPreferencesConstants.TWITTER_USER_TOKEN, token)
				.apply();
	}

	public String getTwitterUserToken() {
		return mSharedPrefs.getString(
				UserPreferencesConstants.TWITTER_USER_TOKEN, null);
	}

	public String getTwitterUserSecret() {
		return mSharedPrefs.getString(
				UserPreferencesConstants.TWITTER_USER_SECECT, null);
	}

	public void setTwitterRequestToken(String token) {
		mSharedPrefs
				.edit()
				.putString(UserPreferencesConstants.TWITTER_REQUEST_TOKEN,
						token).apply();

	}

	public void setTwitterRequestSecret(String tokenSecret) {
		mSharedPrefs
				.edit()
				.putString(UserPreferencesConstants.TWITTER_REQUEST_SECRET,
						tokenSecret).apply();
	}

	public String getTwitterRequestToken() {
		return mSharedPrefs.getString(
				UserPreferencesConstants.TWITTER_REQUEST_TOKEN, null);
	}

	public String getTwitterRequestSecret() {
		return mSharedPrefs.getString(
				UserPreferencesConstants.TWITTER_REQUEST_SECRET, null);
	}

	public boolean getFacebook() {
		return mSharedPrefs
				.getBoolean(UserPreferencesConstants.FACEBOOK, false);
	}

	public void setFacebook(boolean facebook) {
		mSharedPrefs.edit()
				.putBoolean(UserPreferencesConstants.FACEBOOK, facebook)
				.apply();
	}

	public boolean getSocialFacebook() {
		return mSharedPrefs.getBoolean(
				UserPreferencesConstants.SETTING_SOCIAL_FACEBOOK, false);
	}

	public boolean getSocialTwitter() {
		return mSharedPrefs.getBoolean(
				UserPreferencesConstants.SETTING_SOCIAL_TWITTER, false);
	}

	public void setSocialFacebook(boolean facebook) {
		mSharedPrefs
				.edit()
				.putBoolean(UserPreferencesConstants.SETTING_SOCIAL_FACEBOOK,
						facebook).apply();
	}

	public void setSocialTwitter(boolean twitter) {
		mSharedPrefs
				.edit()
				.putBoolean(UserPreferencesConstants.SETTING_SOCIAL_TWITTER,
						twitter).apply();
	}

	public void setFacebookUserToken(String accessToken) {
		mSharedPrefs
				.edit()
				.putString(UserPreferencesConstants.FACEBOOK_USER_TOKEN,
						accessToken).apply();
	}

	public void setFacebookExpires(long accessExpires) {
		mSharedPrefs
				.edit()
				.putLong(UserPreferencesConstants.FACEBOOK_EXPIRES,
						accessExpires).apply();
	}

	public String getFacebookUserToken() {
		return mSharedPrefs.getString(
				UserPreferencesConstants.FACEBOOK_USER_TOKEN, null);
	}

	public long getFacebookExpires() {
		return mSharedPrefs.getLong(UserPreferencesConstants.FACEBOOK_EXPIRES,
				0L);
	}

	public void setFacebookUserName(String name) {
		mSharedPrefs
				.edit()
				.putString(UserPreferencesConstants.FACEBOOK_USER_USERNAME,
						name).apply();
	}

	public void setFacebookUserId(String id) {
		mSharedPrefs.edit()
				.putString(UserPreferencesConstants.FACEBOOK_USER_ID, id)
				.apply();
	}

	public String getFacebookUserName() {
		return mSharedPrefs.getString(
				UserPreferencesConstants.FACEBOOK_USER_USERNAME, null);
	}

	public String getFacebookUserId() {
		return mSharedPrefs.getString(
				UserPreferencesConstants.FACEBOOK_USER_ID, null);
	}

}
