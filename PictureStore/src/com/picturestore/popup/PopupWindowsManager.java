package com.picturestore.popup;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.picturestore.R;
import com.picturestore.view.ViewUtils;

public class PopupWindowsManager extends PopupWindows {
	private final Context mContext;
	private final View mAnchor;
	private final LayoutInflater mInflater;
	private boolean mIsFocusable;

	public PopupWindowsManager(Context context, View anchor) {
		super(context);

		mContext = context;
		mAnchor = anchor;
		mInflater = LayoutInflater.from(mContext);
	}

	public void showWindow(PopupWindowsTypes type) {
		if (!mAnchor.isShown()) {
			return;
		}
		preparePopupWindow(mIsFocusable);
		switch (type) {
		case MENU_MORE: {
			View layout = mInflater.inflate(R.layout.menu_more, null);
			setContentView(layout);
			int x, y;
			int[] location = new int[2];
			mAnchor.getLocationOnScreen(location);

			ViewUtils.MesureView(mMainView, null);

			int viewWidth = mMainView.getMeasuredWidth();
			int viewHeight = mMainView.getMeasuredHeight();

			x = location[0] + mAnchor.getWidth() - viewWidth - 8;
			y = location[1] - viewHeight - 10;

			mPopupWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY, x, y);
			break;
		}
		}		
	}
}
