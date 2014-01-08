package com.picturestore.common.net.data;

import java.util.List;

public class GalaryCategoryDetailData implements PictureStoreData {
	private List<String> mLinkData;

	public List<String> getLinkData() {
		return mLinkData;
	}

	public void setLinkData(List<String> linkData) {
		mLinkData = linkData;
	}

}
