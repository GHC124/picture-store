package com.picturestore.content.hot;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;

import com.android.volley.toolbox.NetworkImageView;
import com.picturestore.BaseListAdapter;
import com.picturestore.BaseViewHolder;
import com.picturestore.R;
import com.picturestore.common.util.PictureStoreImageDownloader;

public class HotDataAdapter extends ArrayAdapter<HotDataItem> implements
		BaseListAdapter {
	private Context mContext;
	private Fragment mFragment;
	private int mResourceId;
	private int mLayoutSize;
	private boolean mInflatView;
	private Bitmap mDefaultCoverBitmap;

	public HotDataAdapter(Context context, Fragment fragment, int resourceId) {
		super(context, resourceId);

		mContext = context;
		mFragment = fragment;
		mResourceId = resourceId;
		mLayoutSize = (int) context.getResources().getDimension(
				R.dimen.ps_dimen_width_14);
		mInflatView = true;
		
		mDefaultCoverBitmap = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.ps_default);
	}

	@Override
	public int getItemLayoutSize() {
		return mLayoutSize;
	}

	@Override
	public boolean isInflatView() {
		return mInflatView;
	}

	@Override
	public void setInflatView(boolean inflatView) {
		mInflatView = inflatView;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final boolean inflatView = mInflatView;
		if (convertView == null) {
			if (inflatView) {
				LayoutInflater inflater = ((Activity) mContext)
						.getLayoutInflater();
				convertView = inflater.inflate(mResourceId, parent, false);
			} else {
				convertView = new View(mContext);
				convertView.setLayoutParams(new LayoutParams(mLayoutSize,
						mLayoutSize));
			}
			HotDataViewHolder dataViewHolder = new HotDataViewHolder();

			if (inflatView) {
				dataViewHolder.mImageView = (NetworkImageView) convertView
						.findViewById(R.id.ps_imgHot_ListItem);
			}

			convertView.setTag(dataViewHolder);
		}
		HotDataViewHolder dataViewHolder = (HotDataViewHolder) convertView
				.getTag();
		dataViewHolder.mInflatView = inflatView;
		if (inflatView) {
			HotDataItem dataItem = getItem(position);
			PictureStoreImageDownloader.download(dataItem.getImage(),
					dataViewHolder.mImageView);
		}
		return convertView;
	}

	private static class HotDataViewHolder implements BaseViewHolder {
		private boolean mInflatView;
		private NetworkImageView mImageView;

		@Override
		public boolean isInflatView() {
			return mInflatView;
		}

		@Override
		public void setInflatView(boolean inflatView) {
			mInflatView = inflatView;
		}
	}

	public interface HotDataListener {
		void onClick(int position, HotDataItem dataItem);
	}

}
