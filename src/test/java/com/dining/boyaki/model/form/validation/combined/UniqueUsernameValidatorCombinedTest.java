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
import com.dining.boyaki.model.form.validation.UniqueUsernameValidator;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class UniqueUsernameValidatorCombinedTest {
	
	@Autowired
	UniqueUsernameValidator uniqueUsernameValidator;
	
	RegisterForm form = new RegisterForm();
	BindingResult bindingResult = new BindException(form, "RegisterForm");
	
	@Test
	@DatabaseSetup(value = "/validation/UniqueUsername/setup/")
	void validateでユーザ名が重複せずエラーが発生しない() throws Exception{
		form.setUserName("マクベイ");
		uniqueUsernameValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
	}
	
	@Test
	@DatabaseSetup(value = "/validation/UniqueUsername/setup/")
	void validateでユーザ名が重複してエラーが発生する() throws Exception{
		form.setUserName("糸井");
		uniqueUsernameValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("userName")
				                .toString().contains("入力されたユーザ名は既に使われています"));
	}
	
	@Test
	@DatabaseSetup(value = "/validation/UniqueUsername/setup/")
	void validateでニックネームが重複してエラーが発生する() throws Exception{
		form.setUserName("sigeno");
		uniqueUsernameValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("userName")
				                .toString().contains("入力されたユーザ名は既に使われています"));
	}

}
