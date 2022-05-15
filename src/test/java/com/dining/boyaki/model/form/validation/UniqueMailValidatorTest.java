package com.dining.boyaki.model.form.validation;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
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
public class UniqueMailValidatorTest {
	
	@Mock
	FindDataSharedService findDataSharedService;
	
	@InjectMocks
	UniqueMailValidator uniqueMailValidator;
	
	RegisterForm form = new RegisterForm();
	BindingResult bindingResult = new BindException(form, "RegisterForm");
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void validateでメールアドレスが重複せずエラーが発生しない() throws Exception{
		form.setMail("disney@gmail.com");
		when(findDataSharedService.findMail("disney@gmail.com")).thenReturn(null);
		
		uniqueMailValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		verify(findDataSharedService,times(1)).findMail("disney@gmail.com");
	}
	
	@Test
	void validateでメールアドレスが重複してエラーが発生する() throws Exception{
		form.setMail("example@ezweb.ne.jp");
		when(findDataSharedService.findMail("example@ezweb.ne.jp")).thenReturn("example@ezweb.ne.jp");
		
		uniqueMailValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("mail")
				                .toString().contains("入力されたメールアドレスは既に使われています"));
		verify(findDataSharedService,times(1)).findMail("example@ezweb.ne.jp");
	}
	
}
