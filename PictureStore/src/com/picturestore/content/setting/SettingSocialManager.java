package com.picturestore.content.setting;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.picturestore.BaseApplication;
import com.picturestore.R;
import com.picturestore.common.social.SocialListener;
import com.picturestore.common.util.DebouncedOnClickListener;
import com.picturestore.prefs.UserPreferences;
import com.picturestore.profile.ProfileManager;

public class SettingSocialManager {

	private View mLayout;
	private LinearLayout mLlSocialFacebook;
	private LinearLayout mLlSocialTwitter;
	private TextView mTvSocialFacebook;
	private TextView mTvSocialTwitter;
	private TextView mTvSocialRemoveFacebook;
	private TextView mTvSocialRemoveTwitter;
	private TextView mTvSocialAddFacebook;
	private TextView mTvSocialAddTwitter;
	private final Context mContext;
	private final Fragment mFragment;

	public SettingSocialManager(Context context, Fragment fragment) {

		mContext = context;
		mFragment = fragment;

		mLayout = LayoutInflater.from(context).inflate(
				R.layout.content_setting_social, null);

		mLlSocialFacebook = (LinearLayout) mLayout
				.findViewById(R.id.ps_llSetting_Social_Facebook);
		mLlSocialTwitter = (LinearLayout) mLayout
				.findViewById(R.id.ps_llSetting_Social_Twitter);
		mTvSocialFacebook = (TextView) mLayout
				.findViewById(R.id.ps_tvSetting_Social_Facebook);
		mTvSocialTwitter = (TextView) mLayout
				.findViewById(R.id.ps_tvSetting_Social_Twitter);
		mTvSocialRemoveFacebook = (TextView) mLayout
				.findViewById(R.id.ps_tvSetting_Social_RemoveFacebook);
		mTvSocialRemoveTwitter = (TextView) mLayout
				.findViewById(R.id.ps_tvSetting_Social_RemoveTwitter);
		mTvSocialAddFacebook = (TextView) mLayout
				.findViewById(R.id.ps_tvSetting_Social_AddFacebook);
		mTvSocialAddTwitter = (TextView) mLayout
				.findViewById(R.id.ps_tvSetting_Social_AddTwitter);

		mTvSocialAddFacebook.setOnClickListener(new DebouncedOnClickListener() {

			@Override
			public void onDebouncedClick(View v) {
				ProfileManager.sendFacebookLogin(mFragment.getActivity(),
						mFragment, mSocialListener);
			}
		});

		mTvSocialAddTwitter.setOnClickListener(new DebouncedOnClickListener() {

			@Override
			public void onDebouncedClick(View v) {
				ProfileManager.sendTwitterLogin(mFragment.getActivity(),
						mFragment, mSocialListener);
			}
		});

		mTvSocialRemoveFacebook
				.setOnClickListener(new DebouncedOnClickListener() {

					@Override
					public void onDebouncedClick(View v) {
						ProfileManager.logoutFacebook(mFragment.getActivity(),
								mFragment, mSocialListener);
					}
				});

		mTvSocialRemoveTwitter
				.setOnClickListener(new DebouncedOnClickListener() {

					@Override
					public void onDebouncedClick(View v) {
						ProfileManager.logoutTwitter(mFragment.getActivity(),
								mFragment, mSocialListener);
					}
				});		

		prepareSocialData();
	}

	public View getView() {
		return mLayout;
	}

	private SocialListener mSocialListener = new SocialListener() {

		@Override
		public void onLogin(int type, boolean success) {
			if (success) {
				final UserPreferences userPreferences = BaseApplication
						.getInstance().getUserPreferences();
				switch (type) {

				case SOCIAL_FACEBOOK:
					mTvSocialAddFacebook.setVisibility(View.INVISIBLE);
					mLlSocialFacebook.setVisibility(View.VISIBLE);
					mTvSocialFacebook.setText(userPreferences
							.getFacebookUserName());
					break;

				case SOCIAL_TWITTER:
					mTvSocialAddTwitter.setVisibility(View.INVISIBLE);
					mLlSocialTwitter.setVisibility(View.VISIBLE);
					mTvSocialTwitter.setText(userPreferences
							.getTwitterUserName());
					break;
				}
			}
		}

		@Override
		public void onLogout(int type, boolean success) {
			if (success) {
				switch (type) {

				case SOCIAL_FACEBOOK:
					mTvSocialAddFacebook.setVisibility(View.VISIBLE);
					mLlSocialFacebook.setVisibility(View.INVISIBLE);
					mTvSocialFacebook.setText("");
					break;

				case SOCIAL_TWITTER:
					mTvSocialAddTwitter.setVisibility(View.VISIBLE);
					mLlSocialTwitter.setVisibility(View.INVISIBLE);
					mTvSocialTwitter.setText("");
					break;
				}
			}
		}

		@Override
		public void onShare(int type, int action, boolean success) {
			
		}
	};

	private void prepareSocialData() {
		final UserPreferences userPreferences = BaseApplication.getInstance()
				.getUserPreferences();

		if (userPreferences.getFacebook()) {
			mLlSocialFacebook.setVisibility(View.VISIBLE);
			mTvSocialFacebook.setText(userPreferences.getFacebookUserName());
		} else {
			mTvSocialAddFacebook.setVisibility(View.VISIBLE);
		}

		if (userPreferences.getTwitter()) {
			mLlSocialTwitter.setVisibility(View.VISIBLE);
			mTvSocialTwitter.setText(userPreferences.getTwitterUserName());
		} else {
			mTvSocialAddTwitter.setVisibility(View.VISIBLE);
		}
	}
}
