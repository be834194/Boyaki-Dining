package com.dining.boyaki.model.form.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.service.RegistrationService;

@Component
public class UniqueUsernameValidator implements Validator {
	
	private final RegistrationService registrationService;
	
	public UniqueUsernameValidator(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return RegisterForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		RegisterForm form = (RegisterForm) target;
		if(form.getUserName() == null || form.getUserName().equals("")) {
			return;
		}
		
		String existName = registrationService.findUserName(form.getUserName());
		if(existName != null) {
			errors.rejectValue("userName",
			                   "RegisterForm.userName",
			                   "入力されたユーザ名は既に使われています");
		}
	}

}
