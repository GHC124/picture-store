package com.picturestore.content.hot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.picturestore.BaseListAdapter;
import com.picturestore.R;
import com.picturestore.common.net.data.CountryData;
import com.picturestore.common.net.data.HotData;
import com.picturestore.common.net.data.HotDetailData;
import com.picturestore.common.net.data.MasterData;
import com.picturestore.common.net.data.parser.HotDetailDataParser;
import com.picturestore.common.net.manager.PictureStoreManagerFactory;
import com.picturestore.content.IContentDetailView;
import com.picturestore.view.HorizontalListView;

public class HotFragment implements IContentDetailView {
	private Context mContext;
	private Fragment mFragment;
	private HorizontalListView mHlvVietNam;
	private HorizontalListView mHlvKorea;
	private HorizontalListView mHlvUSA;
	private MasterData mMasterData;
	private ProgressDialog mProgressDialog;

	@Override
	public View getView(Context context, LayoutInflater inflater,
			ViewGroup container, Fragment fragment, MasterData masterData) {
		mContext = context;
		mFragment = fragment;
		mMasterData = masterData;

		View layout = inflater.inflate(R.layout.content_hot, null);
		mHlvVietNam = (HorizontalListView) layout
				.findViewById(R.id.ps_hlvHot_VietNam);
		mHlvKorea = (HorizontalListView) layout
				.findViewById(R.id.ps_hlvHot_Korea);
		mHlvUSA = (HorizontalListView) layout.findViewById(R.id.ps_hlvHot_USA);

		mHlvVietNam
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						onItemDetailClick(mHlvVietNam, position);
					}
				});
		mHlvKorea.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onItemDetailClick(mHlvKorea, position);
			}
		});
		mHlvUSA.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onItemDetailClick(mHlvUSA, position);
			}
		});

		if (mMasterData != null) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage(mContext
					.getString(R.string.display_loading));
			mProgressDialog.show();

			prepareData();
		}
		;

		return layout;
	}

	// Get data from server
	private void prepareData() {
		Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				mProgressDialog.dismiss();
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
		final List<HotData> hotsData = mMasterData.getHotData();
		String url = null;
		if (hotsData.size() > 0) {
			for (HotData hotData : hotsData) {
				if ("normal".equalsIgnoreCase(hotData.getSexualLevel())) {
					url = hotData.getLink();
					break;
				}
			}
		}
		if (!TextUtils.isEmpty(url)) {
			PictureStoreManagerFactory.newGetHotDetailDataManager(
					Volley.newRequestQueue(activity), url, listener,
					errorListener).execute();
		} else {
			mProgressDialog.dismiss();
		}
	}

	private void onItemDetailClick(HorizontalListView listView, int position) {
		BaseListAdapter adapter = (BaseListAdapter) listView.getAdapter();

		List<HotDataItem> list = new ArrayList<HotDataItem>();
		for (int i = 0; i < adapter.getCount(); i++) {
			list.add((HotDataItem) adapter.getItem(i));
		}
		HotDetailDialog detailDialog = new HotDetailDialog(mContext, mFragment,
				list, position);
		detailDialog.show();
	}
}
