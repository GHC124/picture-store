package com.picturestore.common.util;

import android.text.TextUtils;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

public class BitmapImageDownloader {
	/** The URL of the network image to load */
	private String mUrl;

	/** Local copy of the ImageLoader. */
	private ImageLoader mImageLoader;

	/** Current ImageContainer. (either in-flight or finished) */
	private ImageContainer mImageContainer;

	private ImageListener mImageListener;

	public BitmapImageDownloader(ImageListener imageListener) {
		mImageListener = imageListener;
	}

	/**
	 * Sets URL of the image that should be loaded into this view. Note that
	 * calling this will immediately either set the cached image (if available)
	 * 
	 * @param url
	 *            The URL that should be loaded into this ImageView.
	 * @param imageLoader
	 *            ImageLoader that will be used to make the request.
	 */
	public void setImageUrl(String url, ImageLoader imageLoader) {
		mUrl = url;
		mImageLoader = imageLoader;
		// The URL has potentially changed. See if we need to load it.
		loadImageIfNecessary();
	}

	/**
	 * Loads the image for the view if it isn't already loaded.
	 */
	private void loadImageIfNecessary() {

		// if the URL to be loaded in this view is empty, cancel any old
		// requests and clear the
		// currently loaded image.
		if (TextUtils.isEmpty(mUrl)) {
			if (mImageContainer != null) {
				mImageContainer.cancelRequest();
				mImageContainer = null;
			}
			setDefaultImageOrNull();
			return;
		}

		// if there was an old request in this view, check if it needs to be
		// canceled.
		if (mImageContainer != null && mImageContainer.getRequestUrl() != null) {
			if (mImageContainer.getRequestUrl().equals(mUrl)) {
				// if the request is from the same URL, return.
				return;
			} else {
				// if there is a pre-existing request, cancel it if it's
				// fetching a different URL.
				mImageContainer.cancelRequest();
				setDefaultImageOrNull();
			}
		}

		// The pre-existing content of this view didn't match the current URL.
		// Load the new image
		// from the network.
		ImageContainer newContainer = mImageLoader.get(mUrl, mImageListener);

		// update the ImageContainer to be the new bitmap container.
		mImageContainer = newContainer;
	}

	private void setDefaultImageOrNull() {

	}
}
