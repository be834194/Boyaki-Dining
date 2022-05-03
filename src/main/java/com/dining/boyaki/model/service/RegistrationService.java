package com.dining.boyaki.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.mapper.RegistrationMapper;

@Service
public class RegistrationService {
	
	private final RegistrationMapper registrationMapper;
	
	private final PasswordEncoder passwordEncoder;
	
	public RegistrationService(RegistrationMapper registrationMapper,
			                   PasswordEncoder passwordEncoder) {
		this.registrationMapper = registrationMapper;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional(readOnly = false)
	public void insertAccount(RegisterForm form) {
		form.setPassword(passwordEncoder.encode(form.getPassword()));
		
		Account account = new Account(form.getUserName(),form.getPassword(),form.getMail(),"ROLE_USER");
		registrationMapper.insertAccount(account);
		
		PasswordHistory history = new PasswordHistory(form.getUserName(),form.getPassword(),LocalDateTime.now());
		registrationMapper.insertPasswordHistory(history);
		
		AccountInfo info = new AccountInfo(form.getUserName(),form.getUserName(),null,0,0,0,
				                           165,60,LocalDateTime.now(),LocalDateTime.now());
		registrationMapper.insertAccountInfo(info);
	}

}
