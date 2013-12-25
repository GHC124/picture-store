package com.picturestore.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

public abstract class BaseLoader<T> extends AsyncTaskLoader<T> {

	private T mResult;

	public BaseLoader(Context context) {
		super(context);
	}

	/**
	 * Called when there is new data to deliver to the client. The super class
	 * will take care of delivering it; the implementation here just adds a
	 * little more logic.
	 */
	@Override
	public void deliverResult(T result) {
		if (isReset()) {
			// An async query came in while the loader is stopped. We
			// don't need the result.
			if (result != null) {
				onRelease(result);
			}
		}
		T oldResult = mResult;
		mResult = result;

		if (isStarted()) {
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(result);
		}

		// At this point we can release the resources associated with
		// 'oldApps' if needed; now that the new result is delivered we
		// know that it is no longer in use.
		if (oldResult != null) {
			onRelease(oldResult);
		}
	}

	/**
	 * Handles a request to start the Loader. This implementation assumes that
	 * being loaded, data should be cached, and delivered immediately instead of
	 * being reloaded.
	 */
	@Override
	protected void onStartLoading() {
		if (mResult != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(mResult);
		} else {
			forceLoad();
		}
	}

	/**
	 * Handles a request to stop the Loader.
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	/**
	 * Handles a request to cancel a load.
	 */
	@Override
	public void onCanceled(T result) {
		super.onCanceled(result);
		// At this point we can release the resources associated with 'result'
		// if needed.
		onRelease(result);
	}

	/**
	 * Handles a request to completely reset the Loader.
	 */
	@Override
	protected void onReset() {
		super.onReset();
		// Ensure the loader is stopped
		onStopLoading();

		// At this point we can release the resources associated with 'apps'
		// if needed.
		if (mResult != null) {
			onRelease(mResult);
			mResult = null;
		}
	}

	/**
	 * Called to de-allocate any existing resources that were along with this
	 * result.
	 */
	protected void onRelease(T obsoleteResult) {
	}
}
