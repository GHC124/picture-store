package com.picturestore.content.hot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.picturestore.BaseApplication;
import com.picturestore.R;
import com.picturestore.common.social.SocialListener;
import com.picturestore.common.util.DebouncedOnClickListener;
import com.picturestore.prefs.UserPreferences;
import com.picturestore.profile.ProfileManager;
import com.picturestore.social.FacebookManager;
import com.picturestore.social.TwitterManager;
import com.picturestore.social.twitter.Twitter;

public class HotDetailShareDialog extends Dialog {
	private static final int MAX_TWITTER_TEXT_COUNT = 140;

	private Context mContext;
	private Fragment mFragment;
	private Switch mSwSocialFacebook;
	private Switch mSwSocialTwitter;
	private TextView mTvSocialAddFacebook;
	private TextView mTvSocialAddTwitter;

	private TextView mTvCharacterCount;
	private TextView mTvCharacterOver;
	private EditText mEdtComment;
	private int mCharacterCount = MAX_TWITTER_TEXT_COUNT;
	private Button mBtnPublish;

	private boolean mShareFacebookComplete;
	private boolean mShareTwitterComplete;
	private StringBuilder mShareMessage = new StringBuilder();

	public HotDetailShareDialog(Context context, Fragment fragment) {
		super(context);

		mContext = context;
		mFragment = fragment;
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new BitmapDrawable());
		getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

		setContentView(R.layout.content_hot_detail_share);

		mBtnPublish = (Button) findViewById(R.id.ps_btHot_Share_Publish);
		mBtnPublish.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				publishPost();
			}
		});
		inactivePublish();

		mSwSocialFacebook = (Switch) findViewById(R.id.ps_swSetting_Social_Facebook);
		mSwSocialTwitter = (Switch) findViewById(R.id.ps_swSetting_Social_Twitter);
		mTvSocialAddFacebook = (TextView) findViewById(R.id.ps_tvSetting_Social_AddFacebook);
		mTvSocialAddTwitter = (TextView) findViewById(R.id.ps_tvSetting_Social_AddTwitter);

		mTvCharacterCount = (TextView) findViewById(R.id.ps_tvHot_Share_TextCount);
		mTvCharacterOver = (TextView) findViewById(R.id.ps_tvHot_Share_OverText);
		mEdtComment = (EditText) findViewById(R.id.ps_edtHot_Share_Comment);
		mEdtComment.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				checkTwitterText(s.length(), true);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
		mSwSocialFacebook
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						checkTwitterText(mEdtComment.length(), false);
					}
				});
		mSwSocialTwitter
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						checkTwitterText(mEdtComment.length(), false);
					}
				});

		final View close = findViewById(R.id.ps_tvHot_Share_Close);
		close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		mCharacterCount = MAX_TWITTER_TEXT_COUNT;
		mTvCharacterCount.setText(String.valueOf(mCharacterCount));

		prepareSocialData();
	}

	protected void publishPost() {
		mShareFacebookComplete = false;
		mShareTwitterComplete = false;
		mShareMessage.delete(0, mShareMessage.length());
		String message = mEdtComment.getText().toString();
		if (mSwSocialFacebook.isChecked()) {
			Bundle bundle = new Bundle();
			bundle.putInt(FacebookManager.EXTRA_FACEBOOK_ACTION,
					FacebookManager.FACEBOOK_ACTION_POST);
			bundle.putString(FacebookManager.EXTRA_FACEBOOK_MESSAGE, message);
			new FacebookManager(mContext, mFragment.getLoaderManager(),
					mSocialListener).share(bundle);
		}
		if (mSwSocialTwitter.isChecked()) {
			Bundle bundle = new Bundle();
			bundle.putInt(Twitter.EXTRA_TWITTER_ACTION,
					Twitter.TWITTER_ACTION_POST);
			bundle.putString(Twitter.EXTRA_TWITTER_MESSAGE, message);
			new TwitterManager(mContext, mFragment.getLoaderManager(),
					mSocialListener).share(bundle);
		}
	}

	protected void activePublish() {
		if (!mBtnPublish.isEnabled()) {
			// 0%
			mBtnPublish
					.setTextColor(mBtnPublish.getTextColors().withAlpha(255));
			mBtnPublish.setEnabled(true);
			mBtnPublish.setClickable(true);
		}
	}

	protected void inactivePublish() {
		if (mBtnPublish.isEnabled()) {
			// 50% opacity
			mBtnPublish
					.setTextColor(mBtnPublish.getTextColors().withAlpha(127));
			mBtnPublish.setEnabled(false);
			mBtnPublish.setClickable(false);
		}
	}

	private void checkTwitterText(int length, boolean truncat) {
		boolean allowPost = false;
		if (length >= 0) {
			allowPost = checkTwitterTextCount(MAX_TWITTER_TEXT_COUNT - length,
					truncat);
		}
		if (allowPost) {
			activePublish();
		} else {
			inactivePublish();
		}
	}

	/**
	 * Count twitter text length every time user type
	 * 
	 * @param count
	 *            - Current length
	 * @param truncat
	 *            - True if you want to remove character > 140th
	 * @return True if allow Post
	 */
	private boolean checkTwitterTextCount(int count, boolean truncat) {
		boolean isTwitter = mSwSocialTwitter.isChecked();
		boolean isFacebook = mSwSocialFacebook.isChecked();
		if (!isTwitter && !isFacebook) {
			if (mTvCharacterCount.getVisibility() != View.INVISIBLE) {
				mTvCharacterCount.setVisibility(View.INVISIBLE);
			}
			if (mTvCharacterOver.getVisibility() != View.GONE) {
				mTvCharacterOver.setVisibility(View.GONE);
			}
			return false;
		}
		boolean allowPost = true;
		if (!isTwitter) {
			if (mTvCharacterCount.getVisibility() != View.INVISIBLE) {
				mTvCharacterCount.setVisibility(View.INVISIBLE);
			}
			if (mTvCharacterOver.getVisibility() != View.GONE) {
				mTvCharacterOver.setVisibility(View.GONE);
			}
		} else {
			if (mTvCharacterCount.getVisibility() != View.VISIBLE) {
				mTvCharacterCount.setVisibility(View.VISIBLE);
			}
			if (count <= 0) {
				mTvCharacterCount.setText("0");
				if (truncat) {
					String text = mEdtComment.getText().toString();
					if (text != null && text.length() > MAX_TWITTER_TEXT_COUNT) {
						// Disable text listener
						mEdtComment.setText(text.substring(0,
								MAX_TWITTER_TEXT_COUNT));
						mEdtComment.setSelection(MAX_TWITTER_TEXT_COUNT);
						count = 0;
					}
				}
				String message = "";
				if (isFacebook) {
					message = mContext
							.getString(R.string.content_hot_share_dialog_textcount_over_all);
				} else {
					message = mContext
							.getString(R.string.content_hot_share_dialog_textcount_over_twitter);
				}
				mTvCharacterOver.setText(message);
				if (mTvCharacterOver.getVisibility() != View.VISIBLE) {
					mTvCharacterOver.setVisibility(View.VISIBLE);
				}
				// If count < 0, don't allow post
				if (count < 0) {
					allowPost = false;
				}
			} else {
				mTvCharacterCount.setText(String.valueOf(count));
				if (mTvCharacterOver.getVisibility() != View.GONE) {
					mTvCharacterOver.setVisibility(View.GONE);
				}
			}
		}
		return allowPost;
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

		@Override
		public void onShare(int type, int action, boolean success) {
			switch (type) {
			case SOCIAL_FACEBOOK:
				mShareFacebookComplete = true;
				if (mShareMessage.length() > 0) {
					mShareMessage.append(",");
				}
				if (success) {
					mShareMessage
							.append(mContext
									.getString(R.string.content_hot_share_facebook_success));
				} else {
					mShareMessage
							.append(mContext
									.getString(R.string.content_hot_share_facebook_unsuccess));
				}
				break;
			case SOCIAL_TWITTER:
				mShareTwitterComplete = true;
				if (mShareMessage.length() > 0) {
					mShareMessage.append(",");
				}
				if (success) {
					mShareMessage
							.append(mContext
									.getString(R.string.content_hot_share_twitter_success));
				} else {
					mShareMessage
							.append(mContext
									.getString(R.string.content_hot_share_twitter_unsuccess));
				}
				break;
			}
			boolean showAlert = false;
			if (mShareFacebookComplete && mSwSocialFacebook.isChecked()
					&& mShareTwitterComplete && mSwSocialTwitter.isChecked()) {
				showAlert = true;
			} else if (!mSwSocialFacebook.isChecked()
					&& mSwSocialTwitter.isChecked() && mShareTwitterComplete) {
				showAlert = true;
			} else if (mShareFacebookComplete && mSwSocialFacebook.isChecked()
					&& !mSwSocialTwitter.isChecked()) {
				showAlert = true;
			}
			if (showAlert) {
				AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
				alert.setTitle("Share");
				alert.setMessage(mShareMessage.toString());
				alert.setPositiveButton("Ok", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				alert.show();
			}
		}
	};

	private void prepareSocialData() {
		final UserPreferences userPreferences = BaseApplication.getInstance()
				.getUserPreferences();

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
			if (userPreferences.getSocialFacebook()) {
				mSwSocialFacebook.setChecked(true);
			} else {
				mSwSocialFacebook.setChecked(false);
			}
		} else {
			mTvSocialAddFacebook.setVisibility(View.VISIBLE);
		}

		if (userPreferences.getTwitter()) {
			mSwSocialTwitter.setVisibility(View.VISIBLE);
			if (userPreferences.getSocialTwitter()) {
				mSwSocialTwitter.setChecked(true);
			} else {
				mSwSocialTwitter.setChecked(false);
			}
		} else {
			mTvSocialAddTwitter.setVisibility(View.VISIBLE);

		}
	}

}
