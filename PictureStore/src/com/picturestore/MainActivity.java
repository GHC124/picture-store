package com.picturestore;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		BaseApplication.getInstance().enableLog();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onMenuItemClick(View v) {

		if (mCurrentMenu != null) {
			View old = null;
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
			if (old != null) {
				old.setBackground(new BitmapDrawable());
			}
		}
		v.setBackgroundResource(R.drawable.menu_item_border);
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
			if(mCurrentMenu != MenuItem.MORE){
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
		Fragment fg = new ContentDetailFragment(item);
		// get fragment
		if (fg != null) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.ps_flMain_Content, fg);
			ft.commit();
		}
	}

	private void showMoreMenu(View anchor) {
		mPopupWindowsManager = new PopupWindowsManager(this, anchor);
		mPopupWindowsManager.showWindow(PopupWindowsTypes.MENU_MORE);
		View layout = mPopupWindowsManager.getContentView();
		View setting = layout.findViewById(R.id.ps_rlMenuItem_Setting);
		setting.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changeMenuItem(MenuItem.SETTING);
				mPopupWindowsManager.closePopupWindow();
			}
		});
	}
}
