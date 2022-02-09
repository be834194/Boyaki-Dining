package com.dining.boyaki.model.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.PasswordHistory;
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
	
	public PasswordHistory setToPasswordHistory(RegisterForm form) {
		PasswordHistory history = new PasswordHistory();
		history.setUserName(form.getUserName());
		history.setPassword(form.getPassword());
		history.setUseDay(LocalDateTime.now());
		return history;
	}

}