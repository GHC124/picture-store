package com.picturestore.common.net.data.parser;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import com.picturestore.common.net.data.HotData;

public class HotDataParser {
	private static final String SEXUAL_LEVEL_KEY = "sexualLevel";
	private static final String LINK_KEY = "link";

	public static HotData parse(JSONObject jsonObj)
			throws JSONException, ParseException {
		HotData data = new HotData();

		if (jsonObj.has(SEXUAL_LEVEL_KEY)) {
			data.setSexualLevel(jsonObj.getString(SEXUAL_LEVEL_KEY));
		}

		if (jsonObj.has(LINK_KEY)) {
			data.setLink(jsonObj.getString(LINK_KEY));
		}

		return data;
	}
}
