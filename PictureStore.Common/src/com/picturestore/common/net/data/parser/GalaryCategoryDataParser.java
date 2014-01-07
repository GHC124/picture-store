package com.picturestore.common.net.data.parser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.picturestore.common.net.data.CountryData;
import com.picturestore.common.net.data.GalaryCategoryData;

public class GalaryCategoryDataParser {
	private static final String ALL_KEY = "all";
	private static final String COUNTRIES_KEY = "countries";

	public static GalaryCategoryData parse(JSONObject jsonObj)
			throws JSONException, ParseException {
		GalaryCategoryData data = new GalaryCategoryData();

		if (jsonObj.has(ALL_KEY)) {
			data.setAllData(jsonObj.getString(ALL_KEY));
		}

		if (jsonObj.has(COUNTRIES_KEY)) {
			JSONArray array = jsonObj.getJSONArray(COUNTRIES_KEY);
			List<CountryData> list = new ArrayList<CountryData>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);
				list.add(CountryDataParser.parse(item));
			}
			data.setCountryData(list);
		}

		return data;
	}
}
