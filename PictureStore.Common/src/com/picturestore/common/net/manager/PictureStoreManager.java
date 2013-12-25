package com.picturestore.common.net.manager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

public class PictureStoreManager {
	private static String sMasterDataEnpoint;
	
	private final RequestQueue mRequestQueue;
	@SuppressWarnings("rawtypes")
	private final Request mRequest;

	@SuppressWarnings("rawtypes")
	public PictureStoreManager(RequestQueue requestQueue, Request request) {
		mRequestQueue = requestQueue;
		mRequest = request;
	}

	public void execute() {
		mRequestQueue.add(mRequest);
	}

	public static String getMasterDataEnpoint() {
		return sMasterDataEnpoint;
	}

	public static void setMasterDataEnpoint(String masterDataEnpoint) {
		sMasterDataEnpoint = masterDataEnpoint;
	}	
}
