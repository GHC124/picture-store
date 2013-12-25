package com.picturestore.content.hot;

import java.util.List;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.Window;

import com.android.volley.toolbox.NetworkImageView;
import com.picturestore.R;
import com.picturestore.common.util.PictureStoreImageDownloader;

public class HotDetailDialog extends Dialog {
	private Context mContext;
	private Fragment mFragment;
	private List<HotDataItem> mListData;
	private NetworkImageView mImageView;
	private ProgressDialog mProgressDialog;
	private int mCurrentPosition;

	public HotDetailDialog(Context context, Fragment fragment,
			List<HotDataItem> data, int position) {
		super(context);

		mContext = context;
		mFragment = fragment;

		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new BitmapDrawable());

		setContentView(R.layout.content_hot_detail);

		mImageView = (NetworkImageView) findViewById(R.id.ps_imgHot_Detail_Item);

		mListData = data;
		if (mListData != null) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage(mContext
					.getString(R.string.display_loading));
			mProgressDialog.show();

			populateData(position);
		}

		View previous = findViewById(R.id.ps_tvHot_Detail_Previous);
		View next = findViewById(R.id.ps_tvHot_Detail_Next);
		previous.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loadPrevious();
			}
		});
		next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loadNext();
			}
		});

		View close = findViewById(R.id.ps_tvHot_Detail_Close);
		close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	protected void loadNext() {
		if (mCurrentPosition + 1 < mListData.size()) {
			mCurrentPosition++;
			PictureStoreImageDownloader.download(mListData
					.get(mCurrentPosition).getImage(), mImageView);
		}
	}

	protected void loadPrevious() {
		if (mCurrentPosition - 1 >= 0) {
			mCurrentPosition--;
			PictureStoreImageDownloader.download(mListData
					.get(mCurrentPosition).getImage(), mImageView);
		}
	}

	private void populateData(int position) {
		// Find current
		mCurrentPosition = position;
		
		PictureStoreImageDownloader.download(
				mListData.get(position).getImage(), mImageView);

		mProgressDialog.dismiss();
	}
}
