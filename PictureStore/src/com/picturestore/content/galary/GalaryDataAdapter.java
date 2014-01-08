package com.picturestore.content.galary;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.volley.toolbox.NetworkImageView;
import com.picturestore.R;
import com.picturestore.common.util.PictureStoreImageDownloader;
import com.picturestore.content.ItemDataItem;

public class GalaryDataAdapter extends ArrayAdapter<ItemDataItem> {
	private Context mContext;
	private Fragment mFragment;
	private int mResourceId;

	public GalaryDataAdapter(Context context, Fragment fragment, int resourceId) {
		super(context, resourceId);

		mContext = context;
		mFragment = fragment;
		mResourceId = resourceId;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(mResourceId, parent, false);

			GalaryDataViewHolder dataViewHolder = new GalaryDataViewHolder();
			dataViewHolder.mImageView = (NetworkImageView) convertView
					.findViewById(R.id.ps_imgGalary_ListItem);

			convertView.setTag(dataViewHolder);
		}
		GalaryDataViewHolder dataViewHolder = (GalaryDataViewHolder) convertView
				.getTag();
		ItemDataItem dataItem = getItem(position);
		PictureStoreImageDownloader.download(dataItem.getImage(),
				dataViewHolder.mImageView);

		return convertView;
	}

	private static class GalaryDataViewHolder {
		private NetworkImageView mImageView;

	}
}
