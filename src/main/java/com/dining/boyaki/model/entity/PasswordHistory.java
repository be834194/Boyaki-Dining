package com.dining.boyaki.model.entity;

import java.time.LocalDateTime;

public class PasswordHistory {
	
	private String userName;
	
	private String password;
	
	private LocalDateTime useDay;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalDateTime getUseDay() {
		return useDay;
	}

	public void setUseDay(LocalDateTime useDay) {
		this.useDay = useDay;
	}

}
