package com.picturestore.content.hot;

import java.util.List;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

import com.picturestore.R;
import com.picturestore.animation.AnimationFactory;
import com.picturestore.common.util.PictureStoreDownloadManager;
import com.picturestore.common.util.PictureStoreImageDownloader;
import com.picturestore.content.hot.animation.HotImageView;

public class HotDetailDialog extends Dialog {
	private Context mContext;
	private Fragment mFragment;
	private List<HotDataItem> mListData;
	private HotImageView mImageView;
	private RelativeLayout mRlItem;
	private ProgressDialog mProgressDialog;
	private int mCurrentPosition;	
	private Animation mFadeOutAnim;

	public HotDetailDialog(Context context, Fragment fragment,
			List<HotDataItem> data, int position) {
		super(context);

		mContext = context;
		mFragment = fragment;

		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new BitmapDrawable());
		getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

		setContentView(R.layout.content_hot_detail);

		mRlItem = (RelativeLayout) findViewById(R.id.ps_rlHot_Detail_Item);
		mImageView = (HotImageView) findViewById(R.id.ps_imgHot_Detail_Item1);
		
		mFadeOutAnim = AnimationFactory.FadeOutAnimation(500, null);

		View share = findViewById(R.id.ps_tvHot_Detail_Share);
		share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				HotDetailShareDialog shareDialog = new HotDetailShareDialog(
						mContext, mFragment);
				shareDialog.show();
			}
		});

		View download = findViewById(R.id.ps_tvHot_Detail_Download);
		download.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (PictureStoreDownloadManager
						.isDownloadManagerAvailable(mContext)) {
					HotDataItem dataItem = mListData.get(mCurrentPosition);
					int separate = dataItem.getImage().lastIndexOf("/");
					String fileName = dataItem.getImage();
					if (separate > 0) {
						fileName = fileName.substring(separate + 1);
					}
					// int point = fileName.indexOf(".");
					String title = fileName;
					// if (point > 0) {
					// title = fileName.substring(0, point);
					// }

					PictureStoreDownloadManager.getInstance().download(
							mContext, dataItem.getImage(), fileName, title, "");
				}
			}
		});

		final View previous = findViewById(R.id.ps_tvHot_Detail_Previous);
		final View next = findViewById(R.id.ps_tvHot_Detail_Next);
		previous.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loadPrevious(next, previous);
			}
		});
		next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loadNext(next, previous);
			}
		});

		final View close = findViewById(R.id.ps_tvHot_Detail_Close);
		close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		mListData = data;
		if (mListData != null) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage(mContext
					.getString(R.string.display_loading));
			mProgressDialog.show();

			populateData(position, next, previous);
		}
	}

	protected void loadNext(View next, View previous) {
		if (mListData == null) {
			return;
		}
		if (mCurrentPosition + 1 < mListData.size()) {
			mCurrentPosition++;
			if (mCurrentPosition + 1 == mListData.size()) {
				if (next.getVisibility() != View.INVISIBLE) {
					next.setVisibility(View.INVISIBLE);
				}
			}
			if (mCurrentPosition > 0) {
				if (previous.getVisibility() != View.VISIBLE) {
					previous.setVisibility(View.VISIBLE);
				}
			}
			mImageView.startAnimation(mFadeOutAnim);
			mImageView.setAnimReady(false);

			PictureStoreImageDownloader.download(mListData
					.get(mCurrentPosition).getImage(), mImageView);

			HotDetailFilpAnimator flipAnimator = new HotDetailFilpAnimator(
					mRlItem, mImageView);

			flipAnimator.setAnimationListener(mAnimationListener);
			mRlItem.startAnimation(flipAnimator);
		}
	}

	protected void loadPrevious(View next, View previous) {
		if (mListData == null) {
			return;
		}
		if (mCurrentPosition - 1 >= 0) {
			mCurrentPosition--;
			if (mCurrentPosition + 1 < mListData.size()) {
				if (next.getVisibility() != View.VISIBLE) {
					next.setVisibility(View.VISIBLE);
				}
			}
			if (mCurrentPosition == 0) {
				if (previous.getVisibility() != View.INVISIBLE) {
					previous.setVisibility(View.INVISIBLE);
				}
			}
			mImageView.startAnimation(mFadeOutAnim);
			mImageView.setAnimReady(false);
			
			PictureStoreImageDownloader.download(mListData
					.get(mCurrentPosition).getImage(), mImageView);

			HotDetailFilpAnimator flipAnimator = new HotDetailFilpAnimator(
					mRlItem, mImageView);
			flipAnimator.setAnimationListener(mAnimationListener);
			flipAnimator.reverse();

			mRlItem.startAnimation(flipAnimator);
		}
	}

	private void populateData(int position, View next, View previous) {
		// Find current
		mCurrentPosition = position;
		if (mCurrentPosition + 1 == mListData.size()) {
			if (next.getVisibility() != View.INVISIBLE) {
				next.setVisibility(View.INVISIBLE);
			}
		}
		if (mCurrentPosition == 0) {
			if (previous.getVisibility() != View.INVISIBLE) {
				previous.setVisibility(View.INVISIBLE);
			}
		}
		
		mImageView.setAnimReady(true);
		PictureStoreImageDownloader.download(
				mListData.get(position).getImage(), mImageView);

		mProgressDialog.dismiss();
	}

	private AnimationListener mAnimationListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			mRlItem.setEnabled(false);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			mRlItem.setEnabled(true);
		}
	};
}
