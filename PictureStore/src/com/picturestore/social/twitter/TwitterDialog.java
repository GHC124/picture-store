package com.picturestore.social.twitter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.picturestore.R;

@SuppressLint("SetJavaScriptEnabled")
public class TwitterDialog extends Dialog {
	private static TwitterDialog mInstance;

	private final float[] DIMENSIONS_LANDSCAPE = { 460, 460 };
	private WebView mWebView;
	private LinearLayout mContent;
	private TextView mTitle;
	private boolean mDialogRunning = false;
	private TwitterDialogListener mDialogListener;
	private ProgressDialog mProgressDialog;

	private TwitterDialog(Context context, TwitterDialogListener dialogListener) {
		super(context);
		mDialogListener = dialogListener;

		mContent = new LinearLayout(getContext());
		mContent.setOrientation(LinearLayout.VERTICAL);

		setUpTitle();
		setUpWebView();

		final float scale = getContext().getResources().getDisplayMetrics().density;
		addContentView(mContent, new FrameLayout.LayoutParams(
				(int) (DIMENSIONS_LANDSCAPE[0] * scale + 0.5f),
				(int) (DIMENSIONS_LANDSCAPE[1] * scale + 0.5f)));
	}

	public static TwitterDialog getInstance(Context context,
			TwitterDialogListener dialogListener) {
		if (mInstance == null) {
			mInstance = new TwitterDialog(context, dialogListener);
		}
		return mInstance;
	}

	/**
	 * Show login dialog. Should call this method instead of show()
	 */
	public void showLogin(String url) {
		if (mInstance != null) {
			show();
			mWebView.loadUrl(url);
		}
	}

	private void showProgress() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(getContext());
			mProgressDialog.setMessage(getContext().getString(
					R.string.display_loading));
		}
		mProgressDialog.show();
	}

	private void hideProgress() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	private void setUpTitle() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Drawable icon = getContext().getResources().getDrawable(
				R.drawable.ps_icon_twitter);

		mTitle = new TextView(getContext());

		mTitle.setText("Twitter");
		mTitle.setTextColor(Color.WHITE);
		mTitle.setTypeface(Typeface.DEFAULT_BOLD);
		mTitle.setBackgroundColor(0xFFbbd7e9);
		mTitle.setPadding(4 + 5, 4, 4, 4);
		mTitle.setCompoundDrawablePadding(4 + 5);
		mTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

		mContent.addView(mTitle);
	}

	private void setUpWebView() {
		mWebView = new WebView(getContext());
		mWebView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mWebView.setWebViewClient(new TwitterWebViewClient());
		mWebView.setVerticalScrollBarEnabled(true);
		mWebView.setHorizontalScrollBarEnabled(true);		
		mWebView.setScrollContainer(true);
		mWebView.setScrollbarFadingEnabled(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setUseWideViewPort(true);		
		
		mContent.addView(mWebView);
	}

	private class TwitterWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains("ghc://twitt")) {
				Bundle bundle = new Bundle();
				bundle.putString("url", url);
				mDialogListener.onComplete(bundle);

				TwitterDialog.this.dismiss();
			} else {
				view.loadUrl(url);
			}
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			Bundle bundle = new Bundle();
			bundle.putInt("code", errorCode);
			bundle.putString("description", description);
			mDialogListener.onError(bundle);
			TwitterDialog.this.dismiss();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			showProgress();
			mDialogRunning = true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			String title = mWebView.getTitle();
			if (title != null && title.length() > 0) {
				mTitle.setText(title);
			}
			mDialogRunning = false;
			hideProgress();
		}

	}

	@Override
	protected void onStop() {
		mDialogRunning = false;
		super.onStop();
	}

	public void onBackPressed() {
		if (!mDialogRunning) {
			TwitterDialog.this.dismiss();
		}
	}
}
