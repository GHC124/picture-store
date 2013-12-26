package com.picturestore.content.setting;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.picturestore.R;
import com.picturestore.common.net.data.MasterData;
import com.picturestore.content.IContentDetailView;

public class SettingFragment implements IContentDetailView {
	private Context mContext;
	private Fragment mFragment;
	private ProgressDialog mProgressDialog;

	@Override
	public View getView(Context context, LayoutInflater inflater,
			ViewGroup container, Fragment fragment, MasterData masterData) {
		mContext = context;
		mFragment = fragment;

		View layout = inflater.inflate(R.layout.content_setting, null);

		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog
				.setMessage(mContext.getString(R.string.display_loading));
		mProgressDialog.show();

		prepareData(layout);

		return layout;
	}

	// Read save data and populate to UI
	private void prepareData(View layout) {
		prepareSocialData(layout);

		mProgressDialog.dismiss();
	}

	private void prepareSocialData(View layout) {
		SettingSocialManager socialManager = new SettingSocialManager(mContext,
				mFragment);
		ViewGroup socialLayout = (ViewGroup) layout
				.findViewById(R.id.ps_rlSetting_Social);
		socialLayout.addView(socialManager.getView());
	}
}
