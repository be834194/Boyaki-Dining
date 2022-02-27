package com.dining.boyaki.model.form.validation;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.service.FindDataSharedService;

@RunWith(SpringRunner.class)
public class ExistMailValidatorTest {
	
	@Mock
	FindDataSharedService findDataSharedService;
	
	@InjectMocks
	ExistMailValidator existMailvalidator;
	
	RegisterForm form = new RegisterForm();
	BindingResult bindingResult = new BindException(form, "RegisterForm");
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void validateでメールアドレスが存在するのでエラーが発生しない() throws Exception{
		form.setMail("miho@gmail.com");
		when(findDataSharedService.findMail("miho@gmail.com")).thenReturn("miho@gmail.com");
		
		existMailvalidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		verify(findDataSharedService,times(1)).findMail("miho@gmail.com");
	}
	
	@Test
	void validateでメールアドレスが取得出来ないのでエラーが発生する() throws Exception{
		form.setMail("disney@gmail.com");
		when(findDataSharedService.findMail("disney@gmail.com")).thenReturn(null);
		
		existMailvalidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("mail")
				                .toString().contains("入力されたメールアドレスは登録されていません"));
		verify(findDataSharedService,times(1)).findMail("disney@gmail.com");
	}

}
