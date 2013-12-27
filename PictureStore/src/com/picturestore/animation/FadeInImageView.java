package com.picturestore.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class FadeInImageView extends NetworkImageView {
	private AlphaAnimation mFadeInAnimation;
	
	public FadeInImageView(Context context) {
		super(context);
	}

	public FadeInImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FadeInImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	{
		mFadeInAnimation = new AlphaAnimation(0.0f,  1.0f);
		mFadeInAnimation.setDuration(1000);
	}

	@Override
	public void setImageUrl(String url, ImageLoader imageLoader) {
		super.setImageUrl(url, imageLoader);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		
		this.startAnimation(mFadeInAnimation);
	}	
}
