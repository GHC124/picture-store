package com.picturestore.common.net.data.parser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.picturestore.common.net.data.CountryData;
import com.picturestore.common.net.data.HotDetailData;

public class HotDetailDataParser {
	private static final String CREATED_KEY = "created";
	private static final String EDITED_KEY = "edited";
	private static final String SEXUAL_LEVEL_KEY = "sexualLevel";
	private static final String COUNTRIES_KEY = "countries";

	public static HotDetailData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		HotDetailData data = new HotDetailData();

		if (jsonObj.has(CREATED_KEY)) {
			data.setCreated(jsonObj.getString(CREATED_KEY));
		}

		if (jsonObj.has(EDITED_KEY)) {
			data.setEdited(jsonObj.getString(EDITED_KEY));
		}
		

		if (jsonObj.has(SEXUAL_LEVEL_KEY)) {
			data.setSexualLevel(jsonObj.getString(SEXUAL_LEVEL_KEY));
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
