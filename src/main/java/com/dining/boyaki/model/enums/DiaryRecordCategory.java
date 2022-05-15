package com.dining.boyaki.model.enums;

public enum DiaryRecordCategory {
	MORNIBG(1,"朝食"),
	LUNCH(2,"昼食"),
	DINNER(3,"夕食"),
	OTHERS(4,"飲酒ー間食ー運動");
	
	private int categoryId;
	private String categoryName;
	
	private DiaryRecordCategory(int categoryId,String categoryName) {
		this.categoryId = categoryId;
		this.categoryName = categoryName;
	}
	
	public int getCategoryId() {
		return categoryId;
	}
	
	public String getCategoryName() {
		return categoryName;
	}

}
