package com.picturestore.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class FlipAnimator extends Animation {
	protected Camera camera;
    protected float centerX;
    protected float centerY;
    protected boolean forward = true;

    /**
     * Creates a 3D flip animation.
     * 
     * @param centerX The center of the views in the x-axis.
     * @param centerY The center of the views in the y-axis.
     * @param forward The direction of the animation.
     */
    public FlipAnimator(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;

        setDuration(1000);
        setFillAfter(true);
        setInterpolator(new AccelerateDecelerateInterpolator());
    }

    public void reverse() {
        forward = false;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        camera = new Camera();
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
        // flipped around. This is the main problem with SDK sample, it does not
        // do this.
        if (interpolatedTime >= 0.5f) {
            degrees -= 180.f;
            reachMidPoint();
        }

        if (forward){
            degrees = -degrees;
        }
        
        final Matrix matrix = t.getMatrix();

        camera.save();
        camera.translate(0.0f, 0.0f, (float) (150.0 * Math.sin(radians)));
        camera.rotateY(degrees);
        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }
    
    public void reachMidPoint(){
    	// Do nothing
    }
}
