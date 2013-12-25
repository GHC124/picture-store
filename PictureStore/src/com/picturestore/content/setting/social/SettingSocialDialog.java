package com.picturestore.content.setting.social;

import picturestore.common.social.SocialListener;
import picturestore.common.util.DebouncedOnClickListener;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.picturestore.BaseApplication;
import com.picturestore.R;
import com.picturestore.prefs.UserPreferences;
import com.picturestore.profile.ProfileManager;

public class SettingSocialDialog extends Dialog {

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
	private final SocialListener mSettingSocialListener;

	public SettingSocialDialog(Context context, Fragment fragment,
			SocialListener listener) {
		super(context);
		setContentView(R.layout.content_setting_social);
		getWindow().setBackgroundDrawable(new BitmapDrawable());

		mContext = context;
		mFragment = fragment;
		mSettingSocialListener = listener;

		mLlSocialFacebook = (LinearLayout) findViewById(R.id.ps_llSetting_Social_Facebook);
		mLlSocialTwitter = (LinearLayout) findViewById(R.id.ps_llSetting_Social_Twitter);
		mTvSocialFacebook = (TextView) findViewById(R.id.ps_tvSetting_Social_Facebook);
		mTvSocialTwitter = (TextView) findViewById(R.id.ps_tvSetting_Social_Twitter);
		mTvSocialRemoveFacebook = (TextView) findViewById(R.id.ps_tvSetting_Social_RemoveFacebook);
		mTvSocialRemoveTwitter = (TextView) findViewById(R.id.ps_tvSetting_Social_RemoveTwitter);
		mTvSocialAddFacebook = (TextView) findViewById(R.id.ps_tvSetting_Social_AddFacebook);
		mTvSocialAddTwitter = (TextView) findViewById(R.id.ps_tvSetting_Social_AddTwitter);

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

		View close = findViewById(R.id.ps_tvSetting_Social_Close);
		close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		prepareSocialData();
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
			if (mSettingSocialListener != null) {
				mSettingSocialListener.onLogin(type, success);
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
			if (mSettingSocialListener != null) {
				mSettingSocialListener.onLogout(type, success);
			}
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
