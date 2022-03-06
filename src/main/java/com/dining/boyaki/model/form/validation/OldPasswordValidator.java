package com.dining.boyaki.model.form.validation;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.dining.boyaki.model.form.PasswordChangeForm;
import com.dining.boyaki.model.service.PasswordHistoryService;

@Component
public class OldPasswordValidator implements Validator {
	
	private final PasswordEncoder passwordEncoder;
	
	private final PasswordHistoryService passwordHistoryService;
	
	public OldPasswordValidator(PasswordEncoder passwordEncoder,
			                    PasswordHistoryService passwordHistoryService) {
		this.passwordEncoder = passwordEncoder;
		this.passwordHistoryService = passwordHistoryService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return PasswordChangeForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		PasswordChangeForm form = (PasswordChangeForm)target;
		String userName = form.getUserName();
		String mail = form.getMail();
		String oldPassword = form.getOldPassword();
		
		String currentPassword = passwordHistoryService.findPassword(userName,mail);
		if(currentPassword != null) {
			if(passwordEncoder.matches(oldPassword,currentPassword)) {
				return;
	    	} else {
	    		errors.rejectValue("oldPassword",
	                               "PasswordChangeForm.oldPassword",
	                               "メールアドレスに誤りがあるか、ログイン中のパスワードと異なります");
	    	}
		} else {
			errors.rejectValue("oldPassword",
                               "PasswordChangeForm.oldPassword",
                               "メールアドレスに誤りがあるか、ログイン中のパスワードと異なります");
		}
		
	}

}
