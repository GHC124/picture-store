package com.picturestore.common.net.data;

public class GalaryData implements PictureStoreData {
	private GalarySortData mSortData;
	private GalaryCategoryData mCategoryData;

	public GalarySortData getSortData() {
		return mSortData;
	}

	public void setSortData(GalarySortData sortData) {
		mSortData = sortData;
	}

	public GalaryCategoryData getCategoryData() {
		return mCategoryData;
	}

	public void setCategoryData(GalaryCategoryData categoryData) {
		mCategoryData = categoryData;
	}

}
