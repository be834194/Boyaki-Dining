package com.dining.boyaki.model.service;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.form.AccountInfoForm;
import com.dining.boyaki.model.form.PasswordChangeForm;
import com.dining.boyaki.model.mapper.AccountInfoMapper;

@Service
public class AccountInfoService {
	
	private final AccountInfoMapper accountInfoMapper;
	
	private final ChangeEntitySharedService changeEntitySharedService;
	
	private final PasswordEncoder passwordEncoder;
	
	public AccountInfoForm setToAccountInfoForm(AccountInfo info) {
		AccountInfoForm form = new AccountInfoForm();
		form.setUserName(info.getUserName());
		form.setNickName(info.getNickName());
		form.setProfile(info.getProfile());
		form.setStatus(info.getStatus());
		form.setAge(info.getAge());
		form.setGender(info.getGender());
		return form;
	}
	
	public AccountInfo setToAccountInfo(AccountInfoForm form) {
		AccountInfo info = new AccountInfo();
		info.setUserName(form.getUserName());
		info.setNickName(form.getNickName());
		info.setProfile(form.getProfile());
		info.setStatus(form.getStatus());
		info.setGender(form.getGender());
		info.setAge(form.getAge());
		return info;
	}
	
	public AccountInfoService(AccountInfoMapper accountInfoMapper,
			                  ChangeEntitySharedService changeEntitySharedService,
			                  PasswordEncoder passwordEncoder) {
		this.accountInfoMapper = accountInfoMapper;
		this.changeEntitySharedService = changeEntitySharedService;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional(readOnly = true)
	public AccountInfoForm findAccountInfo(String userName) {
		AccountInfo info = accountInfoMapper.findAccountInfo(userName);
		if(Objects.isNull(info)) {
    		return null;
    	}
    	return setToAccountInfoForm(info);
	}
	
	@Transactional(readOnly = false)
	public void updateAccountInfo(AccountInfoForm form) {
		accountInfoMapper.updateAccountInfo(setToAccountInfo(form));
	}
	
	@Transactional(readOnly = false)
	public void updatePassword(PasswordChangeForm form) {
		form.setPassword(passwordEncoder.encode(form.getPassword()));
		accountInfoMapper.updatePassword(changeEntitySharedService.setToAccount(form));
		accountInfoMapper.insertPasswordHistory(changeEntitySharedService.setToPasswordHistory(form));
	}
	
	@Transactional(readOnly = false)
	public void deleteAccount(String userName) {
		accountInfoMapper.deleteAccount(userName);
	}

}
