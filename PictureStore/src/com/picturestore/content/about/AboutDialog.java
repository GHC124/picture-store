package com.picturestore.content.about;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.Window;

import com.picturestore.R;

public class AboutDialog extends Dialog {
	private Context mContext;
	private Fragment mFragment;

	public AboutDialog(Context context, Fragment fragment) {
		super(context);

		mContext = context;
		mFragment = fragment;
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new BitmapDrawable());
		getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

		setContentView(R.layout.content_about);
		
		final View close = findViewById(R.id.ps_imgAbout_Close);
		close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}
