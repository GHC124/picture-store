package com.picturestore.content.galary;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.picturestore.R;
import com.picturestore.common.net.data.GalaryCategoryData;
import com.picturestore.common.net.data.GalaryCategoryDetailData;
import com.picturestore.common.net.data.GalaryData;
import com.picturestore.common.net.data.MasterData;
import com.picturestore.common.net.data.parser.GalaryCategoryDetailDataParser;
import com.picturestore.common.net.manager.PictureStoreManagerFactory;
import com.picturestore.content.IContentDetailView;
import com.picturestore.content.ItemDataItem;
import com.picturestore.content.ItemDetailDialog;

public class GalaryFragment implements IContentDetailView {
	private Context mContext;
	private Fragment mFragment;
	private MasterData mMasterData;
	private ProgressBar mLoading;
	private View mLlGalary;
	private GridView mGrdItem;
	private GalaryDataAdapter mAdapter;

	@Override
	public View getView(Context context, LayoutInflater inflater,
			ViewGroup container, Fragment fragment, MasterData masterData) {
		mContext = context;
		mFragment = fragment;
		mMasterData = masterData;

		View layout = inflater.inflate(R.layout.content_galary, null);
		mLlGalary = layout.findViewById(R.id.ps_llGalary);
		mLoading = (ProgressBar) layout.findViewById(R.id.ps_pbGalary_Loading);
		mGrdItem = (GridView) layout.findViewById(R.id.ps_grdGalary_Item);
		mAdapter = new GalaryDataAdapter(mContext, mFragment,
				R.layout.content_galary_list_item);
		mGrdItem.setAdapter(mAdapter);
		mGrdItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onItemDetailClick(position);
			}
		});

		if (mMasterData != null) {
			prepareData();
		}

		return layout;
	}

	protected void onItemDetailClick(int position) {
		List<ItemDataItem> list = new ArrayList<ItemDataItem>();
		for (int i = 0; i < mAdapter.getCount(); i++) {
			list.add((ItemDataItem) mAdapter.getItem(i));
		}
		ItemDetailDialog detailDialog = new ItemDetailDialog(mContext, mFragment,
				list, position);
		detailDialog.show();
	}

	private void prepareData() {
		Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				mLoading.setVisibility(View.INVISIBLE);
				mLlGalary.setVisibility(View.VISIBLE);
				try {
					GalaryCategoryDetailData detailData = GalaryCategoryDetailDataParser
							.parse(response);
					List<String> links = detailData.getLinkData();
					mAdapter.clear();
					for (String link : links) {
						ItemDataItem dataItem = new ItemDataItem();
						dataItem.setImage(link);
						mAdapter.add(dataItem);
					}
					mAdapter.notifyDataSetChanged();
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
		final Activity activity = mFragment.getActivity();
		final GalaryData galaryData = mMasterData.getGalaryData();
		GalaryCategoryData categoryData = galaryData.getCategoryData();
		String allUrl = categoryData.getAllData();
		if (!TextUtils.isEmpty(allUrl)) {
			PictureStoreManagerFactory.newGetGalaryCategoryAllDataManager(
					Volley.newRequestQueue(activity), allUrl, listener,
					errorListener).execute();
		} else {
			mLoading.setVisibility(View.INVISIBLE);
		}
	}
}
