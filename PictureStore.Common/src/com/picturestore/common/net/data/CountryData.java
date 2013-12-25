package com.picturestore.common.net.data;

import java.util.List;

public class CountryData implements PictureStoreData {
	public static final String CODE_VIETNAM = "vi";
	public static final String CODE_USA = "us";
	public static final String CODE_KOREA = "ko";
	
	private String mTitle;
	private String mCode;
	private List<String> mLinkData;

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getCode() {
		return mCode;
	}

	public void setCode(String code) {
		mCode = code;
	}

	public List<String> getLinkData() {
		return mLinkData;
	}

	public void setLinkData(List<String> linkData) {
		mLinkData = linkData;
	}

}
