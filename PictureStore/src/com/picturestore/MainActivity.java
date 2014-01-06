package com.picturestore;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.picturestore.animation.AnimationFactory;
import com.picturestore.common.net.data.MasterData;
import com.picturestore.common.net.data.parser.MasterDataParser;
import com.picturestore.common.net.manager.PictureStoreManager;
import com.picturestore.common.net.manager.PictureStoreManagerFactory;
import com.picturestore.common.util.PictureStoreImageDownloader;
import com.picturestore.content.ContentDetailFragment;
import com.picturestore.content.ContentDetailsViewFactory.MenuItem;
import com.picturestore.popup.PopupWindowsManager;
import com.picturestore.popup.PopupWindowsTypes;

/**
 * MainActivity
 * 
 * @author ChungPV1
 * 
 */
public class MainActivity extends BaseActivity {
	private MenuItem mCurrentMenu = MenuItem.HOT;
	private PopupWindowsManager mPopupWindowsManager;
	private MasterData mMasterData;
	private ProgressDialog mProgressDialog;
	private RelativeLayout mRlSelectBox;
	private boolean mIsFirst = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mRlSelectBox = (RelativeLayout) findViewById(R.id.ps_rlMenuItem_SelectBox);

		BaseApplication.getInstance().enableLog();
		PictureStoreImageDownloader.setImageLoader(BaseApplication
				.getInstance().getImageLoader());
		PictureStoreManager
				.setMasterDataEnpoint("https://docs.google.com/document/d/1LDtmWo13thB4ZIL3ffEmZmKT6XIhz8Rq6O6i72NAfHo/export?format=txt");
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getString(R.string.display_loading));
		mProgressDialog.show();

		prepareData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onMenuItemClick(View v) {
		View old = null;
		if (mCurrentMenu != null) {
			switch (mCurrentMenu) {
			case HOT:
				old = findViewById(R.id.ps_rlMenuItem_Hot);
				break;
			case IDOL:
				old = findViewById(R.id.ps_rlMenuItem_Idol);
				break;
			case GALARY:
				old = findViewById(R.id.ps_rlMenuItem_Galary);
				break;
			case SETTING:
				old = findViewById(R.id.ps_rlMenuItem_Setting);
				break;
			case MORE:
				old = findViewById(R.id.ps_rlMenuItem_More);
				break;
			}
		}
		moveSelectBox(old, v);
		switch (v.getId()) {
		case R.id.ps_rlMenuItem_Hot:
			mCurrentMenu = MenuItem.HOT;
			changeMenuItem(MenuItem.HOT);
			break;
		case R.id.ps_rlMenuItem_Idol:
			mCurrentMenu = MenuItem.IDOL;
			changeMenuItem(MenuItem.IDOL);
			break;
		case R.id.ps_rlMenuItem_Galary:
			mCurrentMenu = MenuItem.GALARY;
			changeMenuItem(MenuItem.GALARY);
			break;
		case R.id.ps_rlMenuItem_Setting:
			if (mCurrentMenu != MenuItem.MORE) {
				mCurrentMenu = MenuItem.SETTING;
			}
			changeMenuItem(MenuItem.SETTING);
			break;
		case R.id.ps_rlMenuItem_More:
			mCurrentMenu = MenuItem.MORE;
			showMoreMenu(v);
			break;
		}
	}

	private void changeMenuItem(MenuItem item) {
		Fragment fg = new ContentDetailFragment(item, mMasterData);
		// get fragment
		if (fg != null) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.replace(R.id.ps_flMain_Content, fg);
			ft.commit();
		}
	}

	private void showMoreMenu(View anchor) {
		mPopupWindowsManager = new PopupWindowsManager(this, anchor);
		mPopupWindowsManager.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindowsManager.showWindow(PopupWindowsTypes.MENU_MORE);
		final View layout = mPopupWindowsManager.getContentView();
		final View setting = layout.findViewById(R.id.ps_rlMenuItem_Setting);
		setting.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changeMenuItem(MenuItem.SETTING);
				mPopupWindowsManager.closePopupWindow();
			}
		});
		final View other = layout.findViewById(R.id.ps_rlMenuItem_Other);
		other.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				mPopupWindowsManager.closePopupWindow();
			}
		});
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.anim_dialog_menu_in);
		animation.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				Animation animation1 = AnimationUtils.loadAnimation(
						MainActivity.this, R.anim.anim_dialog_menu_in);
				other.setVisibility(View.VISIBLE);
				other.startAnimation(animation1);
			}
		});
		setting.startAnimation(animation);
	}

	// Get data from server
	private void prepareData() {
		Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					mProgressDialog.dismiss();
					mMasterData = MasterDataParser.parse(response);
					View view = findViewById(R.id.ps_rlMenuItem_Hot);
					onMenuItemClick(view);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		};
		Response.ErrorListener errorListener = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
			}
		};
		PictureStoreManagerFactory.newGetMasterDataManager(
				Volley.newRequestQueue(this), listener, errorListener)
				.execute();
	}

	private void moveSelectBox(final View oldView, final View newView) {
		int[] fromLocation = new int[2];
		int[] toLocation = new int[2];
		if (oldView != null) {
			oldView.getLocationOnScreen(fromLocation);
			TextView oldText = (TextView) oldView
					.findViewWithTag("menuItem_text");
			if (oldText != null) {
				oldText.setTextColor(Color.parseColor("#000000"));
			}
		}
		newView.getLocationOnScreen(toLocation);
		fromLocation[1] = fromLocation[1] - mRlSelectBox.getHeight() - 35;
		toLocation[1] = toLocation[1] - mRlSelectBox.getHeight() - 35;

		float fromXDelta = fromLocation[0];
		float toXDelta = toLocation[0];
		float fromYDelta = fromLocation[1];
		float toYDelta = toLocation[1];

		Animation animation = AnimationFactory.TranslateAnimation(fromXDelta,
				toXDelta, fromYDelta, toYDelta, 500,
				new Animation.AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						TextView newText = (TextView) newView
								.findViewWithTag("menuItem_text");
						if (newText != null) {
							newText.setTextColor(Color.parseColor("#e4e1bd"));
						}
						if (mIsFirst) {
							mIsFirst = false;
							mRlSelectBox.setVisibility(View.VISIBLE);
							animation = AnimationFactory.FadeInAnimation(1000,
									null);
							mRlSelectBox.startAnimation(animation);
						}
					}
				});
		mRlSelectBox.startAnimation(animation);
	}
}
