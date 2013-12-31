package com.picturestore.common.social;

public interface SocialListener {
	int SOCIAL_FACEBOOK = 1;
	int SOCIAL_TWITTER = 2;
	int FACEBOOK_ACTION_POST = 1;
	int TWITTER_ACTION_POST = 1;
	
	void onLogin(int type, boolean success);
	void onLogout(int type, boolean success);
	void onShare(int type, int action, boolean success);
}
