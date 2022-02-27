package com.dining.boyaki.model.form;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotEmpty;

public class AccountInfoForm {
	
	private String userName;
	
	@NotEmpty(message="ニックネームは必須項目です")
	private String nickName;
	
	@Size(max = 50, message="50文字以内で入力してください")
	private String profile;
	
	private int status;
	
	private int gender;
	
	private int age;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
}
