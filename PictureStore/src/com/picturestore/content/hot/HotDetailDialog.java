package com.picturestore.content.hot;

import java.util.List;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.picturestore.R;
import com.picturestore.animation.AnimationFactory;
import com.picturestore.animation.FlipAnimationListener;
import com.picturestore.animation.FlipAnimator;
import com.picturestore.common.util.PictureStoreDownloadManager;
import com.picturestore.common.util.PictureStoreImageDownloader;

public class HotDetailDialog extends Dialog {
	private Context mContext;
	private Fragment mFragment;
	private List<HotDataItem> mListData;
	private ImageView mImageView;
	private RelativeLayout mRlItem;
	private ProgressDialog mProgressDialog;
	private int mCurrentPosition;
	private Animation mFadeOutAnim;
	private Bitmap mBitmap;
	private Animation mFadeInAnim;
	private FlipAnimator mFlipAnimator;
	private boolean mIsSetBitmap = false;
	private View mPrevious;
	private View mNext;

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
		mImageView = (ImageView) findViewById(R.id.ps_imgHot_Detail_Item1);

		View share = findViewById(R.id.ps_tvHot_Detail_Share);
		share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				HotDetailShareDialog shareDialog = new HotDetailShareDialog(
						mContext, mFragment, mListData.get(mCurrentPosition));
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
					String title = fileName;
					PictureStoreDownloadManager.getInstance().download(
							mContext, dataItem.getImage(), fileName, title, "");
				}
			}
		});

		mPrevious = findViewById(R.id.ps_imgHot_Detail_Previous);
		mNext = findViewById(R.id.ps_imgHot_Detail_Next);
		mPrevious.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loadPrevious();
			}
		});
		mNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loadNext();
			}
		});

		final View close = findViewById(R.id.ps_imgHot_Detail_Close);
		close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		mFadeOutAnim = AnimationFactory.FadeOutAnimation(500, mFadeOutListener);
		mFadeInAnim = AnimationFactory.FadeInAnimation(500, null);

		mFlipAnimator = new FlipAnimator();
		mFlipAnimator.setDuration(1000);
		mFlipAnimator.setFlipAnimationListener(mAnimationListener);

		mListData = data;
		if (mListData != null) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage(mContext
					.getString(R.string.display_loading));
			mProgressDialog.show();

			populateData(position, mNext, mPrevious);
		}
	}

	protected void loadNext() {
		if (mListData == null) {
			return;
		}
		if (mCurrentPosition + 1 < mListData.size()) {
			mCurrentPosition++;
			if (mCurrentPosition + 1 == mListData.size()) {
				if (mNext.getVisibility() != View.INVISIBLE) {
					mNext.setVisibility(View.INVISIBLE);
				}
			}
			if (mCurrentPosition > 0) {
				if (mPrevious.getVisibility() != View.VISIBLE) {
					mPrevious.setVisibility(View.VISIBLE);
				}
			}
			mIsSetBitmap = false;
			mBitmap = null;

			mFlipAnimator.restore();

			PictureStoreImageDownloader.download(mListData
					.get(mCurrentPosition).getImage(), mImageListener);

			mRlItem.startAnimation(mFlipAnimator);
		}
	}

	protected void loadPrevious() {
		if (mListData == null) {
			return;
		}
		if (mCurrentPosition - 1 >= 0) {
			mCurrentPosition--;
			if (mCurrentPosition + 1 < mListData.size()) {
				if (mNext.getVisibility() != View.VISIBLE) {
					mNext.setVisibility(View.VISIBLE);
				}
			}
			if (mCurrentPosition == 0) {
				if (mPrevious.getVisibility() != View.INVISIBLE) {
					mPrevious.setVisibility(View.INVISIBLE);
				}
			}
			mIsSetBitmap = false;
			mBitmap = null;

			mFlipAnimator.restore();
			mFlipAnimator.reverse();

			PictureStoreImageDownloader.download(mListData
					.get(mCurrentPosition).getImage(), mImageListener);

			mRlItem.startAnimation(mFlipAnimator);
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

		PictureStoreImageDownloader.download(
				mListData.get(position).getImage(), new ImageListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
					}

					@Override
					public void onResponse(ImageContainer response,
							boolean isImmediate) {
						mBitmap = response.getBitmap();
						if (mBitmap != null) {
							mImageView.setImageBitmap(mBitmap);
							mImageView.startAnimation(mFadeInAnim);
						}
					}
				});

		mProgressDialog.dismiss();
	}

	private ImageListener mImageListener = new ImageListener() {

		@Override
		public void onErrorResponse(VolleyError error) {

		}

		@Override
		public void onResponse(ImageContainer response, boolean isImmediate) {
			mBitmap = response.getBitmap();
			if (mFlipAnimator.isReachMidPoint()) {
				setBitmap();
			}
		}
	};

	private AnimationListener mFadeOutListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (!animation.hasEnded()) {
				mImageView.setAlpha(0.0f);
			}
		}
	};

	private FlipAnimationListener mAnimationListener = new FlipAnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			mPrevious.setEnabled(false);
			mNext.setEnabled(false);
			mImageView.startAnimation(mFadeOutAnim);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			mPrevious.setEnabled(true);
			mNext.setEnabled(true);
		}

		@Override
		public void onReachMidPoint() {
			setBitmap();
		}
	};

	private synchronized void setBitmap() {
		if (!mIsSetBitmap && mBitmap != null) {
			mFadeOutAnim.cancel();
			mImageView.setImageBitmap(mBitmap);
			mImageView.startAnimation(mFadeInAnim);
			mIsSetBitmap = true;
		}
	}
}
