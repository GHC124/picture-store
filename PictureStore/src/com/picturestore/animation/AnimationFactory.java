package com.picturestore.animation;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class AnimationFactory {
	public static Animation FadeInAnimation(long duration, AnimationListener listener) {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(duration);
		alphaAnimation.setAnimationListener(listener);
		alphaAnimation.setFillAfter(false);
		alphaAnimation.setStartOffset(0);

		return alphaAnimation;
	}
	
	public static Animation FadeOutAnimation(long duration, AnimationListener listener) {
		AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
		alphaAnimation.setDuration(duration);
		alphaAnimation.setAnimationListener(listener);
		alphaAnimation.setFillAfter(false);
		alphaAnimation.setStartOffset(0);

		return alphaAnimation;
	}
}
