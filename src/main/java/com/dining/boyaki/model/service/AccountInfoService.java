package com.dining.boyaki.model.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.model.form.AccountInfoForm;
import com.dining.boyaki.model.form.PasswordChangeForm;
import com.dining.boyaki.model.mapper.AccountInfoMapper;

@Service
public class AccountInfoService {
	
	private final AccountInfoMapper accountInfoMapper;
	
	private final PasswordEncoder passwordEncoder;
	
	public AccountInfoService(AccountInfoMapper accountInfoMapper,
			                  PasswordEncoder passwordEncoder) {
		this.accountInfoMapper = accountInfoMapper;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional(readOnly = true)
	public AccountInfoForm findAccountInfo(String userName) {
		AccountInfo info = accountInfoMapper.findAccountInfo(userName);
		if(Objects.isNull(info)) {
    		return null;
    	}
		AccountInfoForm form = new AccountInfoForm(info.getUserName(),info.getNickName(),
				                                   info.getProfile(),info.getStatus(),
				                                   info.getGender(),info.getAge());
    	return form;
	}
	
	@Transactional(readOnly = false)
	public void updateAccountInfo(AccountInfoForm form) {
		AccountInfo info = new AccountInfo(form.getUserName(),form.getNickName(),
				                           form.getProfile(),form.getStatus(),
				                           form.getGender(),form.getAge());
		accountInfoMapper.updateAccountInfo(info);
	}
	
	@Transactional(readOnly = false)
	public void updatePassword(PasswordChangeForm form) {
		form.setPassword(passwordEncoder.encode(form.getPassword()));
		
		Account account = new Account();
		account.setUserName(form.getUserName());
		account.setPassword(form.getPassword());
		account.setMail(form.getMail());
		accountInfoMapper.updatePassword(account);
		
		PasswordHistory history = new PasswordHistory(form.getUserName(),form.getPassword(),LocalDateTime.now());
		accountInfoMapper.insertPasswordHistory(history);
	}
	
	@Transactional(readOnly = false)
	public void deleteAccount(String userName) {
		accountInfoMapper.deleteAccount(userName);
	}

}
