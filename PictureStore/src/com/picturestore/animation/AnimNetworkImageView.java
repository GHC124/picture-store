package com.picturestore.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.animation.Animation;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class AnimNetworkImageView extends NetworkImageView {
	private Animation mAnimation;
	private boolean mAnimReady;

	public AnimNetworkImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public AnimNetworkImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AnimNetworkImageView(Context context) {
		super(context);
	}

	public void setAnimation(Animation animation) {
		mAnimation = animation;
	}
	
	public void setAnimReady(boolean ready){
		mAnimReady = ready;
	}
	
	public void doAnimation(){
		if (mAnimation != null) {
			this.startAnimation(mAnimation);
		}
	}
	
	public void doAnimation(Animation animation){
		mAnimation = animation;
		if (mAnimation != null) {
			this.startAnimation(mAnimation);
		}
	}

	@Override
	public void setImageUrl(String url, ImageLoader imageLoader) {
		super.setImageUrl(url, imageLoader);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);

		if (mAnimReady && mAnimation != null) {
			this.startAnimation(mAnimation);
		}
	}

}
