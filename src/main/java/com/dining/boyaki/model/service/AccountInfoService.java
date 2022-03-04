package com.dining.boyaki.model.service;

import java.util.Objects;

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
	
	public AccountInfoService(AccountInfoMapper accountInfoMapper,
			                  ChangeEntitySharedService changeEntitySharedService) {
		this.accountInfoMapper = accountInfoMapper;
		this.changeEntitySharedService = changeEntitySharedService;
	}
	
	@Transactional(readOnly = true)
	public AccountInfoForm findAccountInfo(String userName) {
		AccountInfo info = accountInfoMapper.findAccountInfo(userName);
		if(Objects.isNull(info)) {
    		return null;
    	}
    	return changeEntitySharedService.setToAccountInfoForm(info);
	}
	
	@Transactional(readOnly = false)
	public void updateAccountInfo(AccountInfoForm form) {
		accountInfoMapper.updateAccountInfo(changeEntitySharedService.setToAccountInfo(form));
	}
	
	@Transactional(readOnly = false)
	public void updatePassword(PasswordChangeForm form) {
		accountInfoMapper.updatePassword(changeEntitySharedService.setToAccount(form));
		accountInfoMapper.insertPasswordHistory(changeEntitySharedService.setToPasswordHistory(form));
	}
	
	@Transactional(readOnly = false)
	public void deleteAccount(String userName) {
		accountInfoMapper.deleteAccount(userName);
	}

}
