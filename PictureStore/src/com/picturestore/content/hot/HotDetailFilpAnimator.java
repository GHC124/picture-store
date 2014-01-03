package com.picturestore.content.hot;

import android.view.View;

import com.picturestore.animation.FlipAnimator;
import com.picturestore.content.hot.animation.HotImageView;

public class HotDetailFilpAnimator extends FlipAnimator {
	private View mView;
	private HotImageView mImageView;

	public HotDetailFilpAnimator(View view, HotImageView imageView) {
		super(view.getWidth() / 2, view.getHeight() / 2);

		setDuration(2500);

		mView = view;
		mImageView = imageView;
	}

	@Override
	public void reverse() {
		super.reverse();
	}

	@Override
	public void reachMidPoint() {
		if (mImageView.getBitmap() != null) {
			mImageView.doSetBitmap();
		} else {
			mImageView.setAnimReady(true);
		}
	}
}
