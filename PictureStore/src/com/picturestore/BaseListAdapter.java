package com.picturestore;

import android.widget.ListAdapter;

public interface BaseListAdapter extends ListAdapter {
	int getItemLayoutSize();
	boolean isInflatView();
	void setInflatView(boolean inflatView);
}
