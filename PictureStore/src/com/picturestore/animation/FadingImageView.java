package com.picturestore.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class FadingImageView extends NetworkImageView {
	private AlphaAnimation mFadeInAnimation;
	private AlphaAnimation mFadeOutAnimation;
	
	public FadingImageView(Context context) {
		super(context);
	}

	public FadingImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FadingImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	{
		mFadeInAnimation = new AlphaAnimation(0.0f,  1.0f);
		mFadeInAnimation.setDuration(1000);
		mFadeOutAnimation = new AlphaAnimation(1.0f,  0.0f);
		mFadeOutAnimation.setDuration(1000);
	}

	@Override
	public void setImageUrl(String url, ImageLoader imageLoader) {
		// Fade out
		if(getDrawable() != null){
			mFadeInAnimation.cancel();
			startAnimation(mFadeOutAnimation);
		}
		super.setImageUrl(url, imageLoader);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		
		mFadeOutAnimation.cancel();
		startAnimation(mFadeInAnimation);
	}	
}
