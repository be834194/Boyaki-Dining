package com.dining.boyaki.model.form.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.service.FindDataSharedService;

@Component
public class UniqueUsernameValidator implements Validator {
	
	private final FindDataSharedService findDataSharedService;
	
	public UniqueUsernameValidator(FindDataSharedService findDataSharedService) {
		this.findDataSharedService = findDataSharedService;
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
		
		String existName = findDataSharedService.findUserName(form.getUserName());
		if(existName != null) {
			errors.rejectValue("userName",
			                   "RegisterForm.userName",
			                   "入力されたユーザ名は既に使われています");
		}
	}

}
