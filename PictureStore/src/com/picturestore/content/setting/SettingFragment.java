package com.picturestore.content.setting;

import picturestore.common.social.SocialListener;
import picturestore.common.util.DebouncedOnClickListener;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.picturestore.BaseApplication;
import com.picturestore.R;
import com.picturestore.content.IContentDetailView;
import com.picturestore.content.setting.social.SettingSocialDialog;
import com.picturestore.prefs.UserPreferences;
import com.picturestore.profile.ProfileManager;

public class SettingFragment implements IContentDetailView {
	private Context mContext;
	private Fragment mFragment;
	private Switch mSwSocialFacebook;
	private Switch mSwSocialTwitter;
	private TextView mTvSocialAddFacebook;
	private TextView mTvSocialAddTwitter;

	@Override
	public View getView(Context context, LayoutInflater inflater,
			ViewGroup container, Fragment fragment) {
		mContext = context;
		mFragment = fragment;

		View layout = inflater.inflate(R.layout.content_setting, null);
		mSwSocialFacebook = (Switch) layout
				.findViewById(R.id.ps_swSetting_Social_Facebook);
		mSwSocialTwitter = (Switch) layout
				.findViewById(R.id.ps_swSetting_Social_Twitter);
		mTvSocialAddFacebook = (TextView) layout
				.findViewById(R.id.ps_tvSetting_Social_AddFacebook);
		mTvSocialAddTwitter = (TextView) layout
				.findViewById(R.id.ps_tvSetting_Social_AddTwitter);

		View setting = layout.findViewById(R.id.ps_tvSetting_Social_Setting);
		setting.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new SettingSocialDialog(mContext, mFragment, mSocialListener)
						.show();
			}
		});

		prepareData();

		return layout;
	}

	// Read save data and populate to UI
	private void prepareData() {
		prepareSocialData();
	}

	private SocialListener mSocialListener = new SocialListener() {

		@Override
		public void onLogin(int type, boolean success) {
			if (success) {
				switch (type) {

				case SOCIAL_FACEBOOK:
					mTvSocialAddFacebook.setVisibility(View.INVISIBLE);
					mSwSocialFacebook.setVisibility(View.VISIBLE);
					break;

				case SOCIAL_TWITTER:
					mTvSocialAddTwitter.setVisibility(View.INVISIBLE);
					mSwSocialTwitter.setVisibility(View.VISIBLE);
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
					mSwSocialFacebook.setVisibility(View.INVISIBLE);
					break;

				case SOCIAL_TWITTER:
					mTvSocialAddTwitter.setVisibility(View.VISIBLE);
					mSwSocialTwitter.setVisibility(View.INVISIBLE);
					break;
				}
			}
		}
	};

	private void prepareSocialData() {
		final UserPreferences userPreferences = BaseApplication.getInstance()
				.getUserPreferences();

		mSwSocialFacebook
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							if (ProfileManager.checkFacebookAuth(
									mFragment.getActivity(), mFragment,
									mSocialListener)) {
								userPreferences.setSocialFacebook(true);
							}
						} else {
							userPreferences.setSocialFacebook(false);
						}
					}
				});
		mSwSocialTwitter
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							if (ProfileManager.checkTwitterAuth(
									mFragment.getActivity(), mFragment,
									mSocialListener)) {
								userPreferences.setSocialTwitter(true);
							}
						} else {
							userPreferences.setSocialTwitter(false);
						}
					}
				});

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

		if (userPreferences.getFacebook()) {
			mSwSocialFacebook.setVisibility(View.VISIBLE);
		} else {
			mTvSocialAddFacebook.setVisibility(View.VISIBLE);
		}

		if (userPreferences.getTwitter()) {
			mSwSocialTwitter.setVisibility(View.VISIBLE);

		} else {
			mTvSocialAddTwitter.setVisibility(View.VISIBLE);

		}
	}
}
