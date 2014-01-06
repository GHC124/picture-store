package com.picturestore.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class FlipAnimator extends Animation {
	protected Camera mCamera;
	protected float mCenterX;
	protected float mCenterY;
	protected boolean mForward = true;
	protected boolean mReachMidPoint = false;
	protected FlipAnimationListener mAnimationListener;

	/**
	 * Creates a 3D flip animation
	 */
	public FlipAnimator() {
		setDuration(1000);
		setFillAfter(false);
		setInterpolator(new AccelerateDecelerateInterpolator());
	}

	public void setCenter(int centerX, int centerY) {
		mCenterX = centerX;
		mCenterY = centerY;
	}

	public void setFlipAnimationListener(FlipAnimationListener animationListener) {
		setAnimationListener(animationListener);
		mAnimationListener = animationListener;
	}

	public void restore() {
		mForward = true;
		mReachMidPoint = false;
	}

	public void reverse() {
		mForward = false;
	}

	public boolean isReachMidPoint() {
		return mReachMidPoint;
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCenterX = width / 2;
		mCenterY = height / 2;
		mCamera = new Camera();
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		// Angle around the y-axis of the rotation at the given time. It is
		// calculated both in radians and in the equivalent degrees.
		final double radians = Math.PI * interpolatedTime;
		float degrees = (float) (180.0 * radians / Math.PI);

		// Once we reach the midpoint in the animation, we need to hide the
		// source view and show the destination view. We also need to change
		// the angle by 180 degrees so that the destination does not come in
		// flipped around
		if (interpolatedTime >= 0.5f) {
			degrees -= 180.0f;
			if (!mReachMidPoint) {
				if (mAnimationListener != null) {
					mAnimationListener.onReachMidPoint();
				}
				mReachMidPoint = true;
			}
		}

		if (mForward) {
			degrees = -degrees;
		}

		final Matrix matrix = t.getMatrix();
		mCamera.save();
		mCamera.translate(0.0f, 0.0f, (float) (210.0 * Math.sin(radians)));
		mCamera.rotateY(degrees);
		mCamera.getMatrix(matrix);
		mCamera.restore();

		matrix.preTranslate(-mCenterX, -mCenterY);
		matrix.postTranslate(mCenterX, mCenterY);
	}
}
