package com.dining.boyaki.model.form;

import javax.validation.constraints.Size;

import com.dining.boyaki.model.form.validation.ConfirmPassword;

@ConfirmPassword(password="password",confirmPassword="confirmPassword")
public class PasswordChangeForm {
	
	private String userName;
	
	private String mail;
	
	private String oldPassword;
	
	@Size(min=8,message="パスワードは8文字以上で入力してください")
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

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
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
