package picturestore.common.util;

import java.util.Map;
import java.util.WeakHashMap;

import android.os.SystemClock;
import android.view.View;

public abstract class DebouncedOnClickListener implements View.OnClickListener {

	/**
	 * MINIMUM_INTERVAL_TIME The minimum allowed time between clicks any click
	 * sooner than this after a previous click will be rejected
	 */
	private static final long MINIMUM_INTERVAL_TIME = 500; // 500ms
	private Map<View, Long> mLastClickedMap;

	/**
	 * Implement this in your subclass instead of onClick
	 * 
	 * @param v
	 *            The view that was clicked
	 */
	public abstract void onDebouncedClick(View v);

	public DebouncedOnClickListener() {
		this.mLastClickedMap = new WeakHashMap<View, Long>();
	}

	@Override
	public void onClick(View clickedView) {
		Long previousClickTimestamp = mLastClickedMap.get(clickedView);
		long currentTimestamp = SystemClock.uptimeMillis();

		mLastClickedMap.put(clickedView, currentTimestamp);
		if (previousClickTimestamp == null
				|| (currentTimestamp - previousClickTimestamp.longValue() > MINIMUM_INTERVAL_TIME)) {
			onDebouncedClick(clickedView);
		}
	}
}
