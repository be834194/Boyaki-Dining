package com.dining.boyaki.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.mapper.RegistrationMapper;

@Service
public class RegistrationService {
	
	private final RegistrationMapper registrationMapper;
	
	private final ChangeEntitySharedService changeEntitySharedService;
	
	private final PasswordEncoder passwordEncoder;
	
	public RegistrationService(RegistrationMapper registrationMapper,
			                   ChangeEntitySharedService changeEntitySharedService,
			                   PasswordEncoder passwordEncoder) {
		this.registrationMapper = registrationMapper;
		this.changeEntitySharedService = changeEntitySharedService;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional(readOnly = false)
	public void insertAccount(RegisterForm form) {
		form.setPassword(passwordEncoder.encode(form.getPassword()));
		registrationMapper.insertAccount(changeEntitySharedService.setToAccount(form));
		registrationMapper.insertPasswordHistory(changeEntitySharedService.setToPasswordHistory(form));
	}

}
