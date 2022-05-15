package com.dining.boyaki.model.entity;

public class Account {
	
	private String userName;
	
	private String password;
	
	private String mail;
	
	private String Role;
	
	public Account() {
		
	}

	public Account(String userName, String password, String mail, String role) {
		this.userName = userName;
		this.password = password;
		this.mail = mail;
		this.Role = role;
	}

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

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getRole() {
		return Role;
	}

	public void setRole(String role) {
		Role = role;
	}
}
