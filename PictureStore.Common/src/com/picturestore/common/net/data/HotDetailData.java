package com.picturestore.common.net.data;

import java.util.List;

public class HotDetailData implements PictureStoreData {
	private String mCreated;
	private String mEdited;
	private String mSexualLevel;
	private List<CountryData> mCountryData;

	public String getCreated() {
		return mCreated;
	}

	public void setCreated(String created) {
		mCreated = created;
	}

	public String getEdited() {
		return mEdited;
	}

	public void setEdited(String edited) {
		mEdited = edited;
	}

	public String getSexualLevel() {
		return mSexualLevel;
	}

	public void setSexualLevel(String sexualLevel) {
		mSexualLevel = sexualLevel;
	}

	public List<CountryData> getCountryData() {
		return mCountryData;
	}

	public void setCountryData(List<CountryData> countryData) {
		mCountryData = countryData;
	}


}
