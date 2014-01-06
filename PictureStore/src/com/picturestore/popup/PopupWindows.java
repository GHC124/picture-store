package com.picturestore.popup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class PopupWindows {
	protected Context mContext;
	protected PopupWindow mPopupWindow;
	protected View mMainView;
	protected Drawable mPopupBackground = null;
	protected WindowManager mPopupWindowManager;

	public PopupWindows(Context context) {
		mContext = context.getApplicationContext();
		mPopupWindow = new PopupWindow(context);
		mPopupWindow.setTouchInterceptor(mPopupTouchListener);

		mPopupWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
	}

	protected void preparePopupWindow(boolean focusable) {
		if (mPopupBackground != null) {
			mPopupWindow.setBackgroundDrawable(mPopupBackground);
		}
		
		mPopupWindow.setWidth(LayoutParams.WRAP_CONTENT);
		mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);

		if (focusable) {
			mPopupWindow.setFocusable(true);
		} else {
			mPopupWindow.setFocusable(false);
		}
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
	}

	public void setBackgroundDrawable(Drawable background) {
		mPopupBackground = background;
	}

	public void setContentView(View root) {
		mMainView = root;
		mPopupWindow.setContentView(root);
	}

	public View getContentView() {
		return mMainView;
	}

	public void setContentView(int layoutResID) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setContentView(inflater.inflate(layoutResID, null));
	}

	public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
		mPopupWindow.setOnDismissListener(listener);
	}

	private OnTouchListener mPopupTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent motionEvent) {
			boolean handled;
			if (handled = motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
				closePopupWindow();
			}
			return handled;
		}
	};

	public void closePopupWindow() {
		mPopupWindow.dismiss();
	}
}