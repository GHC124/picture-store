package com.picturestore.content.hot.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.animation.Animation;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.picturestore.animation.AnimationFactory;

public class HotImageView extends NetworkImageView {
	private Bitmap mBitmap;
	private Animation mFadeInAnim;
	private boolean mAnimReady;

	public HotImageView(Context context) {
		super(context);
	}

	public HotImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public HotImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	{
		mBitmap = null;
		mFadeInAnim = AnimationFactory.FadeInAnimation(2500, null);
	}

	public void setAnimReady(boolean ready) {
		mAnimReady = ready;
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}

	@Override
	public void setImageUrl(String url, ImageLoader imageLoader) {
		super.setImageUrl(url, imageLoader);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		if (mAnimReady) {
			super.setImageBitmap(bm);
			startAnimation(mFadeInAnim);
		} else {
			mBitmap = bm;
		}
	}

	public void doSetBitmap() {
		super.setImageBitmap(mBitmap);
		startAnimation(mFadeInAnim);
	}
}
