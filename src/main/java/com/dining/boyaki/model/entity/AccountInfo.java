package com.dining.boyaki.model.entity;

import java.time.LocalDateTime;

public class AccountInfo {
	
	private String userName;
	
	private String nickName;
	
	private String profile;
	
	private int status;
	
	private int gender;
	
	private int age;
	
	private float height;
	
	private float weight;
	
	private LocalDateTime createAt;
	
	private LocalDateTime updateAt;
	
	public AccountInfo() {
		
	}

	public AccountInfo(String userName, String nickName, String profile, int status, int gender, int age,
			           float height, float weight, LocalDateTime createAt, LocalDateTime updateAt) {
		this.userName = userName;
		this.nickName = nickName;
		this.profile = profile;
		this.status = status;
		this.gender = gender;
		this.age = age;
		this.height = height;
		this.weight = weight;
		this.createAt = createAt;
		this.updateAt = updateAt;
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

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
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