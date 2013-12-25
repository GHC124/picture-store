package com.picturestore.content;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.picturestore.common.net.data.MasterData;

public interface IContentDetailView {
	View getView(Context context, LayoutInflater inflater, ViewGroup container,
			Fragment fragment, MasterData masterData);
}
