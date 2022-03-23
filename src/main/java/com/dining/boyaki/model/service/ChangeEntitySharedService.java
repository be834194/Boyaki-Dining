package com.dining.boyaki.model.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.model.form.PasswordChangeForm;
import com.dining.boyaki.model.form.RegisterForm;

@Service
public class ChangeEntitySharedService {
	
	public Account setToAccount(RegisterForm form) {
		Account account = new Account();
		account.setUserName(form.getUserName());
		account.setPassword(form.getPassword());
		account.setMail(form.getMail());
		account.setRole("ROLE_USER");
		return account;
	}
	
	public Account setToAccount(PasswordChangeForm form) {
		Account account = new Account();
		account.setUserName(form.getUserName());
		account.setPassword(form.getPassword());
		account.setMail(form.getMail());
		return account;
	}
	
	public PasswordHistory setToPasswordHistory(RegisterForm form) {
		PasswordHistory history = new PasswordHistory();
		history.setUserName(form.getUserName());
		history.setPassword(form.getPassword());
		history.setUseDay(LocalDateTime.now());
		return history;
	}
	
	public PasswordHistory setToPasswordHistory(PasswordChangeForm form) {
		PasswordHistory history = new PasswordHistory();
		history.setUserName(form.getUserName());
		history.setPassword(form.getPassword());
		history.setUseDay(LocalDateTime.now());
		return history;
	}
	
	public AccountInfo setToAccountInfo(RegisterForm form) {
		AccountInfo info = new AccountInfo();
		info.setUserName(form.getUserName());
		info.setNickName(form.getUserName());
		info.setProfile(null);
		info.setStatus(0);
		info.setGender(0);
		info.setAge(0);
		return info;
	}

}
