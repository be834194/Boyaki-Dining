package com.dining.boyaki.model.form;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
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
import org.springframework.validation.Validator;

import com.dining.boyaki.model.form.validation.ExistMailValidator;
import com.dining.boyaki.model.form.validation.UniqueMailValidator;
import com.dining.boyaki.model.form.validation.UniqueUsernameValidator;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
                         TransactionalTestExecutionListener.class,
                         DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class RegisterFormTest {
	
	@Autowired
	Validator validator;
	
	@Autowired
	ExistMailValidator existMailValidator;
	
	@Autowired
	UniqueMailValidator uniqueMailValidator;
	
	@Autowired
	UniqueUsernameValidator uniqueUsernameValidator;
	
	RegisterForm form = new RegisterForm();
	                                            //ターゲット,ターゲットオブジェクトの名前
	BindingResult bindingResult = new BindException(form,"RegistrationForm");
	
	@Test
	@DatabaseSetup(value = "/form/Register/setup/")
	void バリデーション問題なし() throws Exception{
		form.setUserName("竹内");
		form.setPassword("hogetaro");
		form.setConfirmPassword("hogetaro");
		form.setMail("disney@gmail.com");
		
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		
		uniqueMailValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		
		uniqueUsernameValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
	}
	
	@ParameterizedTest
	@CsvSource({"亜",
		        "寿限無寿限無後光の擦り切れ回砂利"})
	void 未入力や指定サイズ範囲外でフィールドエラー発生(String userName) throws Exception{
		form.setUserName(userName);
		form.setMail("");
		form.setPassword("pass");
		form.setConfirmPassword("");
		
		validator.validate(form, bindingResult);
		assertEquals(4,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("userName")
				                .toString().contains("ユーザ名は2字以上15字以内で作成してください"));
		assertTrue(bindingResult.getFieldError("mail")
                                .toString().contains("メールアドレスは必須項目です"));
		assertTrue(bindingResult.getFieldError("password")
                                .toString().contains("パスワードは8文字以上で入力してください"));
		assertTrue(bindingResult.getFieldError("confirmPassword")
                                .toString().contains("パスワードが一致していません"));
	}
	
	@ParameterizedTest
	@CsvSource({"加藤健",
		        "sigeno"})
	@DatabaseSetup(value = "/form/Register/setup/")
	void ユーザ名やニックネームの重複でエラー発生(String userName) throws Exception{
		form.setUserName(userName);
		form.setPassword("hogehoge");
		form.setConfirmPassword("hogehoge");
		form.setMail("example@ezweb.ne.jp");
		
		uniqueUsernameValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("userName")
                                .toString().contains("入力されたユーザ名は既に使われています"));
	}
	
	@Test
	void メール形式でフィールドエラー発生() throws Exception{
		form.setUserName("竹内");
		form.setPassword("hogetaro");
		form.setConfirmPassword("hogetaro");
		form.setMail("メールアドレス");
		
		validator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("mail")
                                .toString().contains("メールアドレスの形式で入力してください"));
	}
	
	@Test
	void パスワード不一致でフィールドエラー発生() throws Exception{
		form.setUserName("加藤健");
		form.setPassword("pinballs");
		form.setConfirmPassword("hogehoge");
		form.setMail("example@ezweb.ne.jp");
		
		validator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("confirmPassword")
                                .toString().contains("パスワードが一致していません"));
	}
	
	@Test
	@DatabaseSetup(value = "/form/Register/setup/")
	void メールアドレスの重複でフィールドエラー発生() throws Exception{
		form.setUserName("加藤健");
		form.setPassword("pinballs");
		form.setConfirmPassword("pinballs");
		form.setMail("example@ezweb.ne.jp");
		
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		
		uniqueMailValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("mail")
				                .toString().contains("入力されたメールアドレスは既に使われています"));
	}
	
	@Test
	@DatabaseSetup(value = "/form/Register/setup/")
	void メールアドレスが存在しないとフィールドエラー発生() throws Exception{
		form.setUserName("加藤健");
		form.setPassword("pinballs");
		form.setConfirmPassword("pinballs");
		form.setMail("sakura.spring@gmail.com");
		
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		
		existMailValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("mail")
                                .toString().contains("入力されたメールアドレスは登録されていません"));
	}
	
}
