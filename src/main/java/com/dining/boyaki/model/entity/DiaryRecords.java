package com.dining.boyaki.model.entity;

import java.util.Date;

public class DiaryRecords {
	
	private String userName;
	
	private Category category;
	
	private Date start;
	
	private String record1;
	
	private String record2;
	
	private String record3;
	
	private int price;
	
	private String memo;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCategory() {
		return category.name();
	}

	public void setCategory(String category) {
		this.category = Category.valueOf(category);
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public String getRecord1() {
		return record1;
	}

	public void setRecord1(String record1) {
		this.record1 = record1;
	}

	public String getRecord2() {
		return record2;
	}

	public void setRecord2(String record2) {
		this.record2 = record2;
	}

	public String getRecord3() {
		return record3;
	}

	public void setRecord3(String record3) {
		this.record3 = record3;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}
