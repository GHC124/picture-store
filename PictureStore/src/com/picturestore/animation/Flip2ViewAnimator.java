package com.picturestore.animation;

import android.view.View;
import android.view.animation.Animation;

public class Flip2ViewAnimator extends FlipAnimator {
	private View mFromView;
	private View mToView;
	private boolean mVisibilitySwapped;

	/**
	 * Creates a 3D flip animation between two views. If forward is true, its
	 * assumed that view1 is "visible" and view2 is "gone" before the animation
	 * starts. At the end of the animation, view1 will be "gone" and view2 will
	 * be "visible". If forward is false, the reverse is assumed.
	 * 
	 * @param fromView
	 *            First view in the transition.
	 * @param toView
	 *            Second view in the transition.
	 * @param centerX
	 *            The center of the views in the x-axis.
	 * @param centerY
	 *            The center of the views in the y-axis.
	 * @param forward
	 *            The direction of the animation.
	 */
	public Flip2ViewAnimator(View fromView, View toView, int centerX,
			int centerY) {
		super();

		mFromView = fromView;
		mToView = toView;

		setFlipAnimationListener(new FlipAnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onReachMidPoint() {
				if (!mVisibilitySwapped) {
					mFromView.setVisibility(View.GONE);
					mToView.setVisibility(View.VISIBLE);
					mVisibilitySwapped = true;
				}
			}
		});
	}

	@Override
	public void reverse() {
		super.reverse();

		View temp = mToView;
		mToView = mFromView;
		mFromView = temp;
	}
}
