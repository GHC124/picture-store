package com.picturestore.profile;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.picturestore.R;
import com.picturestore.common.social.SocialListener;
import com.picturestore.common.util.DebouncedOnClickListener;

public final class LoginDialog extends Dialog {

	private static LoginDialog sInstance;

	private final Activity mActivity;
	private final SocialListener mSocialListener;
	private final Button mBtnFacebookLogin;
	private final Button mBtnTwitterLogin;
	private final Button mBtnFacebookLogout;
	private final Button mBtnTwitterLogout;

	public static LoginDialog getInstance(Context context, Fragment fragment,
			SocialListener listener) {
		if (sInstance == null) {
			sInstance = new LoginDialog(context, fragment, listener);
		}
		return sInstance;
	}

	public static void clearInstance() {
		sInstance = null;
	}

	private LoginDialog(final Context context, final Fragment fragment,
			final SocialListener listener) {
		super(context);
		mActivity = (Activity) context;
		mSocialListener = listener;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_login);

		LinearLayout mLlClose = (LinearLayout) findViewById(R.id.ps_llDialogLogin_Close);
		mLlClose.setOnClickListener(new DebouncedOnClickListener() {

			@Override
			public void onDebouncedClick(View v) {
				dismiss();
			}
		});

		mBtnFacebookLogin = (Button) findViewById(R.id.ps_btnDialogLogin_FacebookLogin);
		mBtnTwitterLogin = (Button) findViewById(R.id.ps_btnDialogLogin_TwitterLogin);
		mBtnFacebookLogout = (Button) findViewById(R.id.ps_btnDialogLogin_FacebookLogout);
		mBtnTwitterLogout = (Button) findViewById(R.id.ps_btnDialogLogin_TwitterLogout);

		mBtnFacebookLogin.setOnClickListener(new DebouncedOnClickListener() {

			@Override
			public void onDebouncedClick(View v) {
				if (!ProfileManager.checkFacebookAuth(mActivity)) {
					ProfileManager.sendFacebookLogin(mActivity, fragment,
							mSocialListener);
				}
			}
		});

		mBtnTwitterLogin.setOnClickListener(new DebouncedOnClickListener() {

			@Override
			public void onDebouncedClick(View v) {
				if (!ProfileManager.checkTwitterAuth(mActivity)) {
					ProfileManager.sendTwitterLogin(mActivity, fragment,
							mSocialListener);
				}
			}
		});

		mBtnFacebookLogout.setOnClickListener(new DebouncedOnClickListener() {

			@Override
			public void onDebouncedClick(View v) {
				if (ProfileManager.checkFacebookAuth(mActivity)) {
					ProfileManager.logoutFacebook(mActivity, fragment, mSocialListener);
					checkAuthStatus();
				}
			}
		});

		mBtnTwitterLogout.setOnClickListener(new DebouncedOnClickListener() {

			@Override
			public void onDebouncedClick(View v) {
				if (ProfileManager.checkTwitterAuth(mActivity)) {
					ProfileManager.logoutTwitter(mActivity, fragment, mSocialListener);
					checkAuthStatus();
				}
			}
		});

		getWindow().setBackgroundDrawable(new BitmapDrawable());
	}

	/**
	 * Update UI base on Login status
	 */
	public void checkAuthStatus() {
		if (ProfileManager.checkFacebookAuth(mActivity)) {
			mBtnFacebookLogin.setVisibility(View.GONE);
			mBtnFacebookLogout.setVisibility(View.VISIBLE);
		} else {
			activeButton(mBtnFacebookLogin);
			mBtnFacebookLogin.setVisibility(View.VISIBLE);
			mBtnFacebookLogout.setVisibility(View.GONE);
		}
		if (ProfileManager.checkTwitterAuth(mActivity)) {
			mBtnTwitterLogin.setVisibility(View.GONE);
			mBtnTwitterLogout.setVisibility(View.VISIBLE);
		} else {
			activeButton(mBtnTwitterLogin);
			mBtnTwitterLogin.setVisibility(View.VISIBLE);
			mBtnTwitterLogout.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		checkAuthStatus();
	}

	/**
	 * Only show Twitter login button
	 */
	public void showTwitter() {
		show();

		mBtnFacebookLogin.setVisibility(View.GONE);
		mBtnFacebookLogout.setVisibility(View.GONE);

		activeButton(mBtnTwitterLogin);
		mBtnTwitterLogin.setVisibility(View.VISIBLE);
		mBtnTwitterLogout.setVisibility(View.GONE);
	}

	/**
	 * Only show Facebook login button
	 */
	public void showFacebook() {
		show();

		mBtnTwitterLogin.setVisibility(View.GONE);
		mBtnTwitterLogout.setVisibility(View.GONE);

		activeButton(mBtnFacebookLogin);
		mBtnFacebookLogin.setVisibility(View.VISIBLE);
		mBtnFacebookLogout.setVisibility(View.GONE);
	}

	private void activeButton(Button button) {
		// 0% opacity
		button.setTextColor(button.getTextColors().withAlpha(255));
		button.setEnabled(true);
		button.setClickable(true);
	}
}
