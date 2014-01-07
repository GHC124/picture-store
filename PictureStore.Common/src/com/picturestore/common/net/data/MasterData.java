package com.picturestore.common.net.data;

import java.util.List;

public class MasterData implements PictureStoreData {
	private String mAuthor;
	private String mCreated;
	private String mEdited;
	private List<HotData> mHotData;
	private GalaryData mGalaryData;

	public String getAuthor() {
		return mAuthor;
	}

	public void setAuthor(String author) {
		mAuthor = author;
	}

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

	public List<HotData> getHotData() {
		return mHotData;
	}

	public void setHotData(List<HotData> hotData) {
		mHotData = hotData;
	}

	public GalaryData getGalaryData() {
		return mGalaryData;
	}

	public void setGalaryData(GalaryData galaryData) {
		mGalaryData = galaryData;
	}

}
