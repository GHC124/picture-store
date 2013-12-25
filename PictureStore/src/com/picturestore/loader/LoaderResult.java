package com.picturestore.loader;

import java.util.Iterator;

/**
 * Represents loader execution result. This result may contain multiple results
 * chained via linked list. Next instance of result is available via
 * {@link #next()} call.
 * 
 */
public class LoaderResult implements Iterator<LoaderResult> {

	private final Object mData;
	private final Exception mFailureReason;
	private LoaderResult mNext;

	public <T> T getData() {
		return (T) mData;
	}

	public boolean isSuccessful() {
		return mFailureReason == null && mData != null;
	}

	public Exception getFailureReason() {
		return mFailureReason;
	}

	/**
	 * Creates "successful" loader result.
	 * 
	 * @param data
	 *            representation of loaded information.
	 */
	public LoaderResult(Object data) {
		this(data, null);
	}

	/**
	 * Creates "unsuccessful" loader result.
	 * 
	 * @param reason
	 *            any
	 */
	public LoaderResult(Exception reason) {
		this(null, reason);
	}

	private LoaderResult(Object data, Exception failure) {
		mData = data;
		mFailureReason = failure;
	}

	/**
	 * Appends new loader result to source result. Chained results are available
	 * via {@link LoaderResult#next()}.
	 * 
	 * @param source
	 *            origonal result
	 * @param next
	 *            instance of loader result to append.
	 * @return next result in chain.
	 */
	/* package */static LoaderResult appendNext(LoaderResult source, LoaderResult next) {
		source.mNext = next;
		return next;
	}

	@Override
	public boolean hasNext() {
		return mNext != null;
	}

	@Override
	public LoaderResult next() {
		return mNext;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Removing chained results is not supported");
	}
}
