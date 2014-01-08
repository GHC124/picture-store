package com.picturestore.common.net.data.parser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.picturestore.common.net.data.GalaryCategoryDetailData;

public class GalaryCategoryDetailDataParser {
	private static final String LINKS_KEY = "links";

	public static GalaryCategoryDetailData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		GalaryCategoryDetailData data = new GalaryCategoryDetailData();

		if (jsonObj.has(LINKS_KEY)) {
			JSONArray array = jsonObj.getJSONArray(LINKS_KEY);
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < array.length(); i++) {
				list.add(array.getString(i));
			}
			data.setLinkData(list);
		}

		return data;
	}
}
