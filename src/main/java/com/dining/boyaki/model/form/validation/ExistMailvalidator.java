package com.dining.boyaki.model.form.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.service.FindDataSharedService;

@Component
public class ExistMailValidator implements Validator {
	
	private final FindDataSharedService findDataSharedService;
	
	public ExistMailValidator(FindDataSharedService findDataSharedService) {
		this.findDataSharedService = findDataSharedService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return RegisterForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		RegisterForm form = (RegisterForm)target;
		if(form.getMail() == null || form.getMail().equals("")) {
			return;
		}
		
		String existMail = findDataSharedService.findMail(form.getMail());
		if(existMail == null) {
			errors.rejectValue("mail",
			                   "RegisterForm.mail",
			                   "入力されたメールアドレスは登録されていません");
		}
	}

}
