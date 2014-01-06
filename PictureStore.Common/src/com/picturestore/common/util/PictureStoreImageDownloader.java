package com.picturestore.common.util;

import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;

public class PictureStoreImageDownloader {
	private static ImageLoader mImageLoader;

	public static void setImageLoader(ImageLoader imageLoader) {
		mImageLoader = imageLoader;
	}

	public static void download(String url, ImageListener imageListener) {
		new BitmapImageDownloader(imageListener).setImageUrl(url, mImageLoader);
	}

	public static void download(String url, ImageView imageView) {
		if (imageView instanceof NetworkImageView) {
			((NetworkImageView) imageView).setImageUrl(url, mImageLoader);
		}
	}
}
