package com.dining.boyaki.model.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.mapper.UpdatePasswordMapper;

@Service
public class UpdatePasswordService {
	
	private final UpdatePasswordMapper updatePasswordMapper;
	
    private final ChangeEntitySharedService changeEntitySharedService;
    
    private final FindDataSharedService findDataSharedService;
	
	private final PasswordEncoder passwordEncoder;
	
	public UpdatePasswordService(UpdatePasswordMapper updatePasswordMapper,
			                     ChangeEntitySharedService changeEntitySharedService,
			                     FindDataSharedService findDataSharedService,
			                     PasswordEncoder passwordEncoder) {
		this.updatePasswordMapper = updatePasswordMapper;
		this.changeEntitySharedService = changeEntitySharedService;
		this.findDataSharedService = findDataSharedService;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional(readOnly=false)
	public void updatePassword(RegisterForm form) {
		form.setUserName(findDataSharedService.findUserNameFromMail(form.getMail()));
		form.setPassword(passwordEncoder.encode(form.getPassword()));
		updatePasswordMapper.updatePassword(changeEntitySharedService.setToAccount(form));
		updatePasswordMapper.insertPasswordHistory(changeEntitySharedService.setToPasswordHistory(form));
	}
	
}
