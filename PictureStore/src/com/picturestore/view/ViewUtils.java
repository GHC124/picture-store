package com.picturestore.view;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class ViewUtils {
	public static void MesureView(View view, View parent) {
		LayoutParams params = view.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
		}
		int measuredWidth = 0;
		switch (params.width) {
		case ViewGroup.LayoutParams.MATCH_PARENT:
			if (parent != null) {
				measuredWidth = MeasureSpec.makeMeasureSpec(parent.getWidth(),
						MeasureSpec.EXACTLY);
			}
			break;
		case ViewGroup.LayoutParams.WRAP_CONTENT:
			measuredWidth = MeasureSpec.makeMeasureSpec(params.width,
					MeasureSpec.UNSPECIFIED);
			break;
		default:
			measuredWidth = MeasureSpec.makeMeasureSpec(params.width,
					MeasureSpec.AT_MOST);
			break;
		}
		int measuredHeight = 0;
		switch (params.height) {

		case ViewGroup.LayoutParams.MATCH_PARENT:
			if (parent != null) {
				measuredHeight = MeasureSpec.makeMeasureSpec(
						parent.getHeight(), MeasureSpec.EXACTLY);
			}
			break;
		case ViewGroup.LayoutParams.WRAP_CONTENT:
			measuredHeight = MeasureSpec.makeMeasureSpec(params.height,
					MeasureSpec.UNSPECIFIED);
			break;
		default:
			measuredHeight = MeasureSpec.makeMeasureSpec(params.height,
					MeasureSpec.AT_MOST);
			break;
		}
		view.measure(measuredWidth, measuredHeight);
	}
}
