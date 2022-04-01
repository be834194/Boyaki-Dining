package com.dining.boyaki.model.form.validation.combined;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import com.dining.boyaki.util.CsvDataSetLoader;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.dining.boyaki.model.form.PasswordChangeForm;
import com.dining.boyaki.model.form.validation.OldPasswordValidator;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class OldPasswordValidatorCombinedTest {
	
	@Autowired
	OldPasswordValidator oldPasswordValidator;
	
	PasswordChangeForm form = new PasswordChangeForm();
	BindingResult bindingResult = new BindException(form, "PasswordChangeForm");
	
	@Test
	@DatabaseSetup(value = "/validation/setup/")
	void validateでパスワードが一致するのでエラーが発生しない() throws Exception{
		form.setUserName("糸井");
		form.setMail("mother@yahoo.co.jp");
		form.setOldPassword("sigeSIGE");
		
		oldPasswordValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
	}
	
	@ParameterizedTest
	@CsvSource({"糸井,mother@yahoo.co.jp,sigesige",
		        "糸井,father@yahoo.co.jp,sigeSIGE"})
	@DatabaseSetup(value = "/validation/setup/")
	void validateでパスワードの不一致やメールアドレスの誤りでエラーが発生する(
			String userName,String mail,String oldPassword) throws Exception{
		form.setUserName(userName);
		form.setMail(mail);
		form.setOldPassword(oldPassword);
		
		oldPasswordValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("oldPassword")
				.toString().contains("メールアドレスに誤りがあるか、ログイン中のパスワードと異なります"));
	}

}
