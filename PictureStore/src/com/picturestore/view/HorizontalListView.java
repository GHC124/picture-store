package com.picturestore.view;

import java.util.LinkedList;
import java.util.Queue;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

import com.picturestore.BaseListAdapter;
import com.picturestore.BaseViewHolder;

/**
 * This class make the ListView shows items in horizontal. Currently, it is only
 * used for Moment Time Line.
 * 
 * 
 * @author DTV
 */
public class HorizontalListView extends AdapterView<ListAdapter> {
	public boolean mAlwaysOverrideTouch = true;

	protected BaseListAdapter mAdapter;
	protected int mCurrentX;
	protected int mNextX;
	protected Scroller mScroller;

	private int mLeftViewIndex = -1;
	private int mRightViewIndex = 0;
	private int mMaxX = Integer.MAX_VALUE;
	private int mTrackingMaxX = Integer.MAX_VALUE;
	private int mDisplayOffset = 0;
	private int mAdapterChildCount = 0;
	private GestureDetector mGesture;
	private Queue<View> mRemovedViewQueue = new LinkedList<View>();
	private OnItemSelectedListener mOnItemSelected;
	private OnItemClickListener mOnItemClicked;
	private OnItemLongClickListener mOnItemLongClicked;
	private boolean mDataChanged = false;
	private Activity mActivity;

	private boolean mScrollToEnd;
	private boolean mNeedMeasureMaxX = false;

	// mode/state of HorizontalListView.
	private enum TouchMode {
		TOUCH_MODE_REST, TOUCH_MODE_DOWN, TOUCH_MODE_FLING, TOUCH_MODE_SCROLL, TOUCH_MODE_SINGLE_TAP
	}

	private TouchMode mTouchMode = TouchMode.TOUCH_MODE_REST;

	public HorizontalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		mActivity = (Activity) context;
	}

	private synchronized void initView() {
		mLeftViewIndex = -1;
		mRightViewIndex = 0;
		mDisplayOffset = 0;
		mCurrentX = 0;
		mNextX = 0;
		mMaxX = Integer.MAX_VALUE;
		Context context = getContext();

		mScroller = new Scroller(context);
		mGesture = new GestureDetector(context, mOnGesture);
	}

	@Override
	public void setOnItemSelectedListener(
			AdapterView.OnItemSelectedListener listener) {
		mOnItemSelected = listener;
	}

	@Override
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
		mOnItemClicked = listener;
	}

	@Override
	public void setOnItemLongClickListener(
			AdapterView.OnItemLongClickListener listener) {
		mOnItemLongClicked = listener;
	}

	private DataSetObserver mDataObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			synchronized (HorizontalListView.this) {
				mDataChanged = true;
			}
			invalidate();
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			reset();

			invalidate();
			requestLayout();
		}
	};

	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public View getSelectedView() {
		return null;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataObserver);
		}
		mAdapter = (BaseListAdapter) adapter;
		mAdapter.registerDataSetObserver(mDataObserver);
		reset();
	}

	private synchronized void reset() {
		initView();
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public void setSelection(int position) {
	}

	public void setScrollToEnd(boolean scrollToEnd) {
		mScrollToEnd = scrollToEnd;
		if (mScrollToEnd) {
			mNeedMeasureMaxX = true;
		}
	}

	public boolean isScrollToEnd() {
		return mScrollToEnd;
	}

	private void measureChild(View child) {
		final LayoutParams params = child.getLayoutParams();
		int measuredWidth;
		switch (params.width) {
		case ViewGroup.LayoutParams.MATCH_PARENT:
			measuredWidth = MeasureSpec.makeMeasureSpec(getWidth(),
					MeasureSpec.EXACTLY);
			break;
		case ViewGroup.LayoutParams.WRAP_CONTENT:
			measuredWidth = MeasureSpec.makeMeasureSpec(params.width,
					MeasureSpec.UNSPECIFIED);
			break;
		default:
			measuredWidth = MeasureSpec.makeMeasureSpec(params.width,
					MeasureSpec.AT_MOST);
			break;
		}
		int measuredHeight;
		switch (params.height) {

		case ViewGroup.LayoutParams.MATCH_PARENT:
			measuredHeight = MeasureSpec.makeMeasureSpec(getHeight(),
					MeasureSpec.EXACTLY);
			break;
		case ViewGroup.LayoutParams.WRAP_CONTENT:
			measuredHeight = MeasureSpec.makeMeasureSpec(params.height,
					MeasureSpec.UNSPECIFIED);
			break;
		default:
			measuredHeight = MeasureSpec.makeMeasureSpec(params.height,
					MeasureSpec.AT_MOST);
			break;
		}
		child.measure(measuredWidth, measuredHeight);
	}

	private void addAndMeasureChild(final View child, int viewPos) {
		LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
		}
		addViewInLayout(child, viewPos, params, true);
		measureChild(child);
	}

	@Override
	protected synchronized void onLayout(boolean changed, int left, int top,
			int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		layoutChildren();
	}

	private void layoutChildren() {
		if (mAdapter == null) {
			return;
		}
		if (mDataChanged) {
			int oldCurrentX = mCurrentX;

			initView();
			removeAllViewsInLayout();

			int oldAdapterChildCount = mAdapterChildCount;
			mAdapterChildCount = mAdapter.getCount();
			if (oldAdapterChildCount != mAdapterChildCount || mNeedMeasureMaxX) {
				measureMaxX();
				mNeedMeasureMaxX = false;
			}
			if (mScrollToEnd) {
				oldCurrentX = mTrackingMaxX;
			}

			mNextX = oldCurrentX;
			mDataChanged = false;
		}

		if (mScroller.computeScrollOffset()) {
			mNextX = mScroller.getCurrX();

		}

		if (mNextX <= 0) {
			mNextX = 0;
		}
		if (mNextX >= mMaxX) {
			mNextX = mMaxX;
		}

		int dx = mCurrentX - mNextX;

		removeNonVisibleItems(dx);
		fillList(dx);
		positionItems(dx);

		mCurrentX = mNextX;

		if (!mScroller.isFinished()) {
			post(mRequestLayoutRunnable);
		} else {
			mTouchMode = TouchMode.TOUCH_MODE_REST;
		}
	}

	private final Runnable mRequestLayoutRunnable = new Runnable() {
		@Override
		public void run() {
			invalidate();
			if (mTouchMode == TouchMode.TOUCH_MODE_FLING) {
				layoutChildren();
			}
		}
	};

	private FlingRunnable mFlingRunnable;
	private final int FLING_RUNNABLE_DELAY = 200;

	private class FlingRunnable implements Runnable {
		public float mVelocityX;

		public void setVelocityX(float velocityX) {
			mVelocityX = velocityX;
		}

		@Override
		public void run() {
			if (mTouchMode == TouchMode.TOUCH_MODE_REST) {
				mTouchMode = TouchMode.TOUCH_MODE_FLING;
				mScroller
						.fling(mNextX, 0, (int) -mVelocityX, 0, 0, mMaxX, 0, 0);
				layoutChildren();
			} else {
				// if other event still hasn't finished, we will try again.
				postDelayed(this, FLING_RUNNABLE_DELAY);
			}

		}
	}

	private void fillList(final int dx) {
		int edge = 0;
		View child = getChildAt(getChildCount() - 1);
		if (child != null) {
			edge = child.getRight();
		}
		fillListRight(edge, dx);

		edge = 0;
		child = getChildAt(0);
		if (child != null) {
			edge = child.getLeft();
		}
		fillListLeft(edge, dx);
	}

	private void fillListRight(int rightEdge, final int dx) {
		int right = rightEdge;
		int dX = dx;
		int layoutSize = mAdapter.getItemLayoutSize();
		View child = null;
		View convertView = null;
		boolean inflatView = true;
		while (right + dX < getWidth() && mRightViewIndex < mAdapterChildCount) {
			inflatView = true;
			convertView = mRemovedViewQueue.poll();
			if (right + dX + layoutSize < 0 && convertView == null) {
				inflatView = false;
			}
			if (inflatView && convertView != null) {
				BaseViewHolder listItem = (BaseViewHolder) convertView.getTag();
				if (!listItem.isInflatView()) {
					convertView = null;
				}
			}
			mAdapter.setInflatView(inflatView);
			child = mAdapter.getView(mRightViewIndex, convertView, this);
			addAndMeasureChild(child, -1);
			right += child.getMeasuredWidth();
			if (mRightViewIndex == mAdapterChildCount - 1) {
				mMaxX = mCurrentX + right - getWidth();
				if (mTrackingMaxX != mMaxX) {
					mTrackingMaxX = mMaxX;
				}
				if (mMaxX < 0) {
					mMaxX = 0;
				}
			}
			mRightViewIndex++;
		}
	}

	private void fillListLeft(int leftEdge, final int dx) {
		int left = leftEdge;
		int dX = dx;
		while (left + dX > 0 && mLeftViewIndex >= 0) {
			View child = mAdapter.getView(mLeftViewIndex,
					mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, 0);
			left -= child.getMeasuredWidth();
			mLeftViewIndex--;
			mDisplayOffset -= child.getMeasuredWidth();
		}
	}

	private void removeNonVisibleItems(final int dx) {
		View child = getChildAt(0);
		while (child != null && child.getRight() + dx <= 0) {
			mDisplayOffset += child.getMeasuredWidth();
			if (((BaseViewHolder) child.getTag()).isInflatView()) {
				mRemovedViewQueue.offer(child);
			}
			removeViewInLayout(child);
			mLeftViewIndex++;
			child = getChildAt(0);
		}
		child = getChildAt(getChildCount() - 1);
		while (child != null && child.getLeft() + dx >= getWidth()) {
			if (((BaseViewHolder) child.getTag()).isInflatView()) {
				mRemovedViewQueue.offer(child);
			}
			removeViewInLayout(child);
			mRightViewIndex--;
			child = getChildAt(getChildCount() - 1);
		}
	}

	/** Loops through each child and positions them onto the screen */
	private void positionItems(final int dx) {
		int childCount = getChildCount();
		if (childCount > 0) {
			mDisplayOffset += dx;
			int leftOffset = mDisplayOffset;
			// Loop each child view
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				int width = child.getMeasuredWidth();
				int left = leftOffset + getPaddingLeft();
				int top = getPaddingTop();
				int right = left + width;
				int bottom = top + child.getMeasuredHeight();
				// Layout the child
				child.layout(left, top, right, bottom);
				// Increment our offset
				leftOffset += width;
			}
		}
	}

	private void measureMaxX() {
		int right = 0;
		View child = null;
		int i = 0;
		while (i < mAdapterChildCount) {
			// Measure Max X
			mAdapter.setInflatView(false);
			child = mAdapter.getView(i, child, this);
			LayoutParams params = child.getLayoutParams();
			if (params == null) {
				params = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
			}
			measureChild(child);
			right += child.getMeasuredWidth();
			if (i == mAdapterChildCount - 1) {
				mMaxX = right - getWidth();
				if (mTrackingMaxX != mMaxX) {
					mTrackingMaxX = mMaxX;
				}
				if (mMaxX < 0) {
					mMaxX = 0;
				}
			}
			i++;
		}
		mAdapter.setInflatView(true);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean handled = super.dispatchTouchEvent(ev);
		handled |= mGesture.onTouchEvent(ev);
		return handled;
	}

	protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (mFlingRunnable == null) {
			mFlingRunnable = new FlingRunnable();
		}
		mFlingRunnable.setVelocityX(velocityX);

		removeCallbacks(mFlingRunnable);
		post(mFlingRunnable);

		return true;
	}

	protected boolean onScroll(int distanceX) {
		mNextX += distanceX;

		layoutChildren();

		return true;
	}

	/**
	 * Stop scrolling immediately. Reset the state.
	 */
	public void onForceScrollFinish() {
		mScroller.forceFinished(true);
		mTouchMode = TouchMode.TOUCH_MODE_REST;
	}

	/**
	 * Determines whether or not the view can scroll left.
	 * 
	 * @return true if possible, false, if already scrolled as far left as
	 *         possible.
	 * 
	 */
	private boolean canScrollLeft() {
		return true;
	}

	/**
	 * Determines whether or not the view can scroll right.
	 * 
	 * @return true if possible, false, if already scrolled as far right as
	 *         possible.
	 */
	private boolean canScrollRight() {
		return !(mLeftViewIndex == -1 && mDisplayOffset == 0);
	}

	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onDown(MotionEvent e) {
			HorizontalListView.this.onForceScrollFinish();

			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (((velocityX >= 0) && (!canScrollLeft()))
					|| ((velocityX < 0) && (!canScrollRight()))) {
				HorizontalListView.this.onForceScrollFinish();
				return true;
			}

			return HorizontalListView.this
					.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			mTouchMode = TouchMode.TOUCH_MODE_SCROLL;

			final boolean overScrollRight = (distanceX >= 0f)
					&& (!canScrollLeft());
			final boolean overScrollLeft = (distanceX < 0f)
					&& (!canScrollRight());

			if (overScrollLeft || overScrollRight) {
				HorizontalListView.this.onForceScrollFinish();
				return true;
			}

			return HorizontalListView.this.onScroll((int) distanceX);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				if (isEventWithinView(e, child)) {
					if (mOnItemClicked != null) {
						mOnItemClicked.onItemClick(HorizontalListView.this,
								child, mLeftViewIndex + 1 + i,
								mAdapter.getItemId(mLeftViewIndex + 1 + i));
					}
					if (mOnItemSelected != null) {
						mOnItemSelected.onItemSelected(HorizontalListView.this,
								child, mLeftViewIndex + 1 + i,
								mAdapter.getItemId(mLeftViewIndex + 1 + i));
					}
					break;
				}
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				if (isEventWithinView(e, child)) {
					if (mOnItemLongClicked != null) {
						mOnItemLongClicked.onItemLongClick(
								HorizontalListView.this, child, mLeftViewIndex
										+ 1 + i,
								mAdapter.getItemId(mLeftViewIndex + 1 + i));
					}
					break;
				}
			}
		}

		private boolean isEventWithinView(MotionEvent e, View child) {
			Rect viewRect = new Rect();
			int[] childPosition = new int[2];
			child.getLocationOnScreen(childPosition);
			int left = childPosition[0];
			int right = left + child.getWidth();
			int top = childPosition[1];
			int bottom = top + child.getHeight();
			viewRect.set(left, top, right, bottom);
			return viewRect.contains((int) e.getRawX(), (int) e.getRawY());
		}
	};
}
