package com.dining.boyaki.model.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public class RegisterForm {
	
	@Size(min=1,max=15,message="ユーザ名は1字以上15字以内で作成してください")
	private String userName;
	
	@Email
	private String mail;
	
	@Size(min=8,message="パスワードは8文字以上で作成してください")
	private String password;
	
	private String confirmPassword;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
}
