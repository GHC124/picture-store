package com.picturestore.content.galary;

import java.text.ParseException;
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
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.picturestore.R;
import com.picturestore.common.net.data.CountryData;
import com.picturestore.common.net.data.GalaryCategoryData;
import com.picturestore.common.net.data.GalaryData;
import com.picturestore.common.net.data.HotDetailData;
import com.picturestore.common.net.data.MasterData;
import com.picturestore.common.net.data.parser.HotDetailDataParser;
import com.picturestore.common.net.manager.PictureStoreManagerFactory;
import com.picturestore.content.IContentDetailView;
import com.picturestore.content.hot.HotDataAdapter;
import com.picturestore.content.hot.HotDataItem;

public class GalaryFragment implements IContentDetailView {
	private Context mContext;
	private Fragment mFragment;
	private MasterData mMasterData;
	private ProgressBar mLoading;

	@Override
	public View getView(Context context, LayoutInflater inflater,
			ViewGroup container, Fragment fragment, MasterData masterData) {
		mContext = context;
		mFragment = fragment;
		mMasterData = masterData;

		View layout = inflater.inflate(R.layout.content_galary, null);

		mLoading = (ProgressBar) layout.findViewById(R.id.ps_pbHot_Loading);

		if (mMasterData != null) {
			prepareData();
		}
		
		return layout;
	}

	private void prepareData() {
		Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				mLoading.setVisibility(View.INVISIBLE);
				mEsv.setVisibility(View.VISIBLE);
				try {
					HotDetailData detailData = HotDetailDataParser
							.parse(response);
					List<CountryData> countries = detailData.getCountryData();
					for (CountryData countryData : countries) {
						HotDataAdapter adapter = new HotDataAdapter(mContext,
								mFragment, R.layout.content_hot_list_item);
						if (CountryData.CODE_VIETNAM
								.equalsIgnoreCase(countryData.getCode())) {
							mHlvVietNam.setAdapter(adapter);
						} else if (CountryData.CODE_USA
								.equalsIgnoreCase(countryData.getCode())) {
							mHlvUSA.setAdapter(adapter);
						} else if (CountryData.CODE_KOREA
								.equalsIgnoreCase(countryData.getCode())) {
							mHlvKorea.setAdapter(adapter);
						}
						List<String> links = countryData.getLinkData();
						for (String link : links) {
							HotDataItem dataItem = new HotDataItem();
							dataItem.setImage(link);
							adapter.add(dataItem);
						}
						adapter.notifyDataSetChanged();
					}

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
			PictureStoreManagerFactory.newGetHotDetailDataManager(
					Volley.newRequestQueue(activity), url, listener,
					errorListener).execute();
		} else {
			mLoading.setVisibility(View.INVISIBLE);
		}
	}
}
