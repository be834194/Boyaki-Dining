package com.dining.boyaki.model.form.validation.combined;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.form.validation.ExistMailValidator;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class ExistMailvalidatorCombinedTest {
	
	@Autowired
	ExistMailValidator existMailValidator;
	
	RegisterForm form = new RegisterForm();
	BindingResult bindingResult = new BindException(form, "RegisterForm");
	
	@Test
	@DatabaseSetup(value = "/validation/ExistMail/setup/")
	void validateでメールアドレスが存在するのでエラーが発生しない() throws Exception{
		form.setMail("miho@gmail.com");
		existMailValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
	}
	
	@Test
	@DatabaseSetup(value = "/validation/ExistMail/setup/")
	void validateでメールアドレスが取得出来ないのでエラーが発生する() throws Exception{
		form.setMail("disney@gmail.com");
		existMailValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("mail")
				                .toString().contains("入力されたメールアドレスは登録されていません"));
	}

}
