package picturestore.common.social;

public interface SocialListener {
	int SOCIAL_FACEBOOK = 1;
	int SOCIAL_TWITTER = 2;
	void onLogin(int type, boolean success);
	void onLogout(int type, boolean success);
}
