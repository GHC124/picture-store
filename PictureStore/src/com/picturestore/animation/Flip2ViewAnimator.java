package com.picturestore.animation;

import android.view.View;

public class Flip2ViewAnimator extends FlipAnimator {
	private View fromView;
	private View toView;
	private boolean visibilitySwapped;

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
		super(centerX, centerY);

		this.fromView = fromView;
		this.toView = toView;
	}

	@Override
	public void reverse() {
		super.reverse();

		View temp = toView;
		toView = fromView;
		fromView = temp;
	}

	@Override
	public void reachMidPoint() {
		if (!visibilitySwapped) {
			fromView.setVisibility(View.GONE);
			toView.setVisibility(View.VISIBLE);
			visibilitySwapped = true;
		}
	}
}
