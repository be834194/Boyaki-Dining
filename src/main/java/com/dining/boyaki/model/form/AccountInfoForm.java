package com.dining.boyaki.model.form;

import java.io.Serializable;
import javax.validation.constraints.Size;

public class AccountInfoForm implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String userName;
	
	@Size(min=2,max=15,message="ニックネームは2字以上15字以内で作成してください")
	private String nickName;
	
	@Size(max = 50, message="50文字以内で入力してください")
	private String profile;
	
	private int status;
	
	private int gender;
	
	private int age;
	
	private int height;
	
	private int weight;
	
	public AccountInfoForm() {
		
	}

	public AccountInfoForm(String userName,String nickName, String profile, int status, int gender, int age,
			               int height, int weight) {
		this.userName = userName;
		this.nickName = nickName;
		this.profile = profile;
		this.status = status;
		this.gender = gender;
		this.age = age;
		this.height = height;
		this.weight = weight;
	}

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

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
}
