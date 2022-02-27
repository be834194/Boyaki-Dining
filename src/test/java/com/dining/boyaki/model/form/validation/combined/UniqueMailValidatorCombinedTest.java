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
import com.dining.boyaki.model.form.validation.UniqueMailValidator;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class UniqueMailValidatorCombinedTest {
	
	@Autowired
	UniqueMailValidator uniqueMailValidator;
	
	RegisterForm form = new RegisterForm();
	BindingResult bindingResult = new BindException(form, "RegisterForm");
	
	@Test
	@DatabaseSetup(value = "/validation/UniqueMail/setup/")
	void validateでメールアドレスが重複せずエラーが発生しない() throws Exception{
		form.setMail("disney@gmail.com");
		uniqueMailValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
	}
	
	@Test
	@DatabaseSetup(value = "/validation/UniqueMail/setup/")
	void validateでメールアドレスが重複してエラーが発生する() throws Exception{
		form.setMail("example@ezweb.ne.jp");
		uniqueMailValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("mail")
				                .toString().contains("入力されたメールアドレスは既に使われています"));
	}

}
