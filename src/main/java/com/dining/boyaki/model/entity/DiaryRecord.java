package com.dining.boyaki.model.entity;

import java.time.LocalDateTime;
import java.util.Date;

public class DiaryRecord {
	
	private String userName;
	
	private int categoryId;
	
	private Date diaryDay;
	
	private String record1;
	
	private String record2;
	
	private String record3;
	
	private int price;
	
	private String memo;
	
	private LocalDateTime createAt;
	
	private LocalDateTime updateAt;
	
	public DiaryRecord() {
		
	}

	public DiaryRecord(String userName, int categoryId, Date diaryDay, String record1, String record2, String record3,
			int price, String memo, LocalDateTime createAt, LocalDateTime updateAt) {
		this.userName = userName;
		this.categoryId = categoryId;
		this.diaryDay = diaryDay;
		this.record1 = record1;
		this.record2 = record2;
		this.record3 = record3;
		this.price = price;
		this.memo = memo;
		this.createAt = createAt;
		this.updateAt = updateAt;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public Date getDiaryDay() {
		return diaryDay;
	}

	public void setDiaryDay(Date diaryDay) {
		this.diaryDay = diaryDay;
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

	public LocalDateTime getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}

	public LocalDateTime getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(LocalDateTime updateAt) {
		this.updateAt = updateAt;
	}

}
