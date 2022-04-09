package com.dining.boyaki.model.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.mapper.UpdatePasswordMapper;

@Service
public class UpdatePasswordService {
	
	private final UpdatePasswordMapper updatePasswordMapper;
    
    private final FindDataSharedService findDataSharedService;
	
	private final PasswordEncoder passwordEncoder;
	
	public UpdatePasswordService(UpdatePasswordMapper updatePasswordMapper,
			                     FindDataSharedService findDataSharedService,
			                     PasswordEncoder passwordEncoder) {
		this.updatePasswordMapper = updatePasswordMapper;
		this.findDataSharedService = findDataSharedService;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional(readOnly=false)
	public void updatePassword(RegisterForm form) {
		form.setUserName(findDataSharedService.findUserNameFromMail(form.getMail()));
		form.setPassword(passwordEncoder.encode(form.getPassword()));
		
		Account account = new Account(form.getUserName(),form.getPassword(),form.getMail(),"ROLE_USER");
		updatePasswordMapper.updatePassword(account);
		
		PasswordHistory history = new PasswordHistory(form.getUserName(),form.getPassword(),LocalDateTime.now());
		updatePasswordMapper.insertPasswordHistory(history);
	}
	
}
