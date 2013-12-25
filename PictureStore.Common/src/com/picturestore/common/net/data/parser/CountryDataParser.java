package com.picturestore.common.net.data.parser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.picturestore.common.net.data.CountryData;

public class CountryDataParser {
	private static final String TITLE_KEY = "title";
	private static final String CODE_KEY = "code";
	private static final String LINKS_KEY = "links";

	public static CountryData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		CountryData data = new CountryData();

		if (jsonObj.has(TITLE_KEY)) {
			data.setTitle(jsonObj.getString(TITLE_KEY));
		}

		if (jsonObj.has(CODE_KEY)) {
			data.setCode(jsonObj.getString(CODE_KEY));
		}

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
