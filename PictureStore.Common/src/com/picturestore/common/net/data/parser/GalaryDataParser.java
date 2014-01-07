package com.picturestore.common.net.data.parser;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import com.picturestore.common.net.data.GalaryCategoryData;
import com.picturestore.common.net.data.GalaryData;
import com.picturestore.common.net.data.GalarySortData;

public class GalaryDataParser {
	private static final String SORTS_KEY = "sorts";
	private static final String CATEGORIES_KEY = "categories";

	public static GalaryData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		GalaryData data = new GalaryData();

		if (jsonObj.has(SORTS_KEY)) {
			data.setSortData(new GalarySortData());
		}

		if (jsonObj.has(CATEGORIES_KEY)) {
			GalaryCategoryData categoryData = GalaryCategoryDataParser
					.parse(jsonObj.getJSONObject(CATEGORIES_KEY));
			data.setCategoryData(categoryData);
		}

		return data;
	}
}
