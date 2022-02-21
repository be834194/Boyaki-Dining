package com.dining.boyaki.model.form;

import java.time.LocalDateTime;
import java.util.Date;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.dining.boyaki.model.form.validation.ConfirmDiaryRecord;

@ConfirmDiaryRecord(records = {"record1","record2","record3"})
public class DiaryRecordForm {
	
	private String userName;
	
	@Min(value=1,message="カテゴリを選んでください")
	private int categoryId;
	
	@NotNull(message="日付を入力してください")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date diaryDay;
	
	private String record1;
	
	private String record2;
	
	private String record3;
	
	private int price;
	
	private String memo;
	
	private LocalDateTime createAt;

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

}
