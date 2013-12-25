package com.picturestore.content;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.picturestore.common.net.data.MasterData;
import com.picturestore.content.ContentDetailsViewFactory.MenuItem;

public class ContentDetailFragment extends Fragment {
	private MenuItem mItemEnum;
	private MasterData mMasterData;
	
	public ContentDetailFragment() {

	}

	public ContentDetailFragment(MenuItem tabEnum, MasterData masterData) {
		mItemEnum = tabEnum;
		mMasterData = masterData;
	}

	public MenuItem getEnum() {
		return mItemEnum;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mItemEnum == null) {
			// Default
			mItemEnum = MenuItem.HOT;
		}
		// return the matching view with item selected on ListView
		return ContentDetailsViewFactory.createView(getActivity(), inflater,
				container, mItemEnum, this, mMasterData);
	}
}
