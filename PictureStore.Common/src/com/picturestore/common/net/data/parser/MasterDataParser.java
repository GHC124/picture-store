package com.picturestore.common.net.data.parser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.picturestore.common.net.data.GalaryData;
import com.picturestore.common.net.data.HotData;
import com.picturestore.common.net.data.MasterData;

public class MasterDataParser {
	private static final String AUTHOR_KEY = "author";
	private static final String CREATED_KEY = "created";
	private static final String EDITED_KEY = "edited";
	private static final String HOTS_KEY = "hots";
	private static final String GALARY_KEY = "galary";

	public static MasterData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		MasterData data = new MasterData();

		if (jsonObj.has(AUTHOR_KEY)) {
			data.setAuthor(jsonObj.getString(AUTHOR_KEY));
		}

		if (jsonObj.has(CREATED_KEY)) {
			data.setCreated(jsonObj.getString(CREATED_KEY));
		}

		if (jsonObj.has(EDITED_KEY)) {
			data.setEdited(jsonObj.getString(EDITED_KEY));
		}

		if (jsonObj.has(HOTS_KEY)) {
			JSONArray array = jsonObj.getJSONArray(HOTS_KEY);
			List<HotData> list = new ArrayList<HotData>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);
				list.add(HotDataParser.parse(item));
			}
			data.setHotData(list);
		}

		if (jsonObj.has(GALARY_KEY)) {
			GalaryData galaryData = GalaryDataParser.parse(jsonObj
					.getJSONObject(GALARY_KEY));
			data.setGalaryData(galaryData);
		}

		return data;
	}
}
