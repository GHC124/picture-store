package com.picturestore.animation;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class AnimationFactory {
	public static Animation FadeInAnimation(long duration,
			AnimationListener listener) {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(duration);
		if (listener != null) {
			alphaAnimation.setAnimationListener(listener);
		}
		alphaAnimation.setFillAfter(true);
		return alphaAnimation;
	}

	public static Animation FadeOutAnimation(long duration,
			AnimationListener listener) {
		AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
		alphaAnimation.setDuration(duration);
		if (listener != null) {
			alphaAnimation.setAnimationListener(listener);
		}
		alphaAnimation.setFillAfter(true);
		return alphaAnimation;
	}

	public static Animation TranslateAnimation(float fromXDelta,
			float toXDelta, float fromYDelta, float toYDelta, long duration,
			AnimationListener listener) {
		TranslateAnimation translateAnimation = new TranslateAnimation(
				fromXDelta, toXDelta, fromYDelta, toYDelta);
		translateAnimation.setDuration(duration);
		if (listener != null) {
			translateAnimation.setAnimationListener(listener);
		}
		translateAnimation.setFillAfter(true);
		return translateAnimation;
	}

	public static Animation ScaleAnimation(float fromX, float toX, float fromY,
			float toY, long duration, AnimationListener listener) {
		ScaleAnimation scaleAnimation = new ScaleAnimation(fromX, toX, fromY,
				toY, Animation.RELATIVE_TO_PARENT, 0,
				Animation.RELATIVE_TO_PARENT, 0);
		scaleAnimation.setDuration(duration);
		if (listener != null) {
			scaleAnimation.setAnimationListener(listener);
		}
		scaleAnimation.setFillAfter(true);
		return scaleAnimation;
	}
}
