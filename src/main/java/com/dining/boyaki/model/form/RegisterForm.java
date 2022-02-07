package com.dining.boyaki.model.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import com.dining.boyaki.model.form.validation.ConfirmPassword;

@ConfirmPassword(password="password",confirmPassword="confirmPassword")
public class RegisterForm {
	
	@Size(min=2,max=15,message="ユーザ名は2字以上15字以内で作成してください")
	private String userName;
	
	@Email(message="メールアドレスの形式で入力してください")
	@NotEmpty(message="メールアドレスは必須項目です")
	private String mail;
	
	@Size(min=8,message="パスワードは8文字以上の半角英数字で入力してください")
	@Pattern(regexp="[a-zA-z0-9]+",message="パスワードは8文字以上の半角英数字で入力してください")
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
