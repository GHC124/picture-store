package com.picturestore.common.net.data;

import java.util.List;

public class GalaryCategoryData implements PictureStoreData {
	private String mAllData;
	private List<CountryData> mCountryData;

	public String getAllData() {
		return mAllData;
	}

	public void setAllData(String allData) {
		mAllData = allData;
	}

	public List<CountryData> getCountryData() {
		return mCountryData;
	}

	public void setCountryData(List<CountryData> countryData) {
		mCountryData = countryData;
	}

}
