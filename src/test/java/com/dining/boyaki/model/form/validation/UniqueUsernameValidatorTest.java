package com.dining.boyaki.model.form.validation;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import com.dining.boyaki.model.service.FindDataSharedService;
import com.dining.boyaki.model.form.RegisterForm;

@RunWith(SpringRunner.class)
public class UniqueUsernameValidatorTest {
	
	@Mock
	FindDataSharedService findDataSharedService;
	
	@InjectMocks
	UniqueUsernameValidator uniqueUsernameValidator;
	
	RegisterForm form = new RegisterForm();
	BindingResult bindingResult = new BindException(form, "RegisterForm");
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void validateでユーザ名が重複せずエラーが発生しない() throws Exception{
		form.setUserName("マクベイ");
		when(findDataSharedService.findUserName("マクベイ")).thenReturn(null);
		
		uniqueUsernameValidator.validate(form, bindingResult);
		assertNull(bindingResult.getFieldError());
		verify(findDataSharedService,times(1)).findUserName("マクベイ");
	}
	
	@Test
	void validateでユーザ名が重複してエラーが発生する() throws Exception{
		form.setUserName("糸井");
		when(findDataSharedService.findUserName("糸井")).thenReturn("糸井");
		
		uniqueUsernameValidator.validate(form, bindingResult);
		assertTrue(bindingResult.getFieldError("userName")
				                .toString().contains("入力されたユーザ名は既に使われています"));
		verify(findDataSharedService,times(1)).findUserName("糸井");
	}

}
