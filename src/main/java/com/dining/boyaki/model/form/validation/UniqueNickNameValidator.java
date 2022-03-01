package com.dining.boyaki.model.form.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.dining.boyaki.model.form.AccountInfoForm;
import com.dining.boyaki.model.service.FindDataSharedService;

@Component
public class UniqueNickNameValidator implements Validator {
	
	private final FindDataSharedService findDataSharedService;
	
	public UniqueNickNameValidator(FindDataSharedService findDataSharedService) {
		this.findDataSharedService = findDataSharedService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return AccountInfoForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		AccountInfoForm form = (AccountInfoForm) target;
		if(form.getNickName() == null || form.getNickName().equals("")) {
			return;
		}
		
		String existName = findDataSharedService.findNickName(form.getNickName());
		if(existName != null) {
			errors.rejectValue("nickName",
			                   "AccountInfoForm.nickName",
			                   "入力されたニックネームは既に使われています");
		}
	}

}
