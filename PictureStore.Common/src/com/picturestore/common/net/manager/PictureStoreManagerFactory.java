package com.picturestore.common.net.manager;

import org.json.JSONObject;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

public class PictureStoreManagerFactory {

	public static PictureStoreManager newGetMasterDataManager(
			RequestQueue requestQueue, Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		if (TextUtils.isEmpty(PictureStoreManager.getMasterDataEnpoint())) {
			throw new IllegalArgumentException();
		}
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.GET, PictureStoreManager.getMasterDataEnpoint(),
				null, listener, errorListener);
		return new PictureStoreManager(requestQueue, jsonObjectRequest);
	}

	public static PictureStoreManager newGetHotDetailDataManager(
			RequestQueue requestQueue, String url,
			Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.GET, url, null, listener, errorListener);
		return new PictureStoreManager(requestQueue, jsonObjectRequest);
	}
	
	public static PictureStoreManager newGetGalaryCategoryAllDataManager(
			RequestQueue requestQueue, String url,
			Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.GET, url, null, listener, errorListener);
		return new PictureStoreManager(requestQueue, jsonObjectRequest);
	}
}
