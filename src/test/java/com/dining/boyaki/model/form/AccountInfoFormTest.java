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

import com.dining.boyaki.model.form.validation.UniqueNickNameValidator;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
                         TransactionalTestExecutionListener.class,
                         DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class AccountInfoFormTest {
	
	@Autowired
	Validator validator;
	
	@Autowired
	UniqueNickNameValidator uniqueNickNameValidator;
	
	AccountInfoForm form = new AccountInfoForm();
                                                  //ターゲット,ターゲットオブジェクトの名前
	BindingResult bindingResult = new BindException(form,"AccountInfoForm");
	
	@Test
	@DatabaseSetup(value = "/form/AccountInfo/setup/")
	void バリデーション問題なし() throws Exception{
		form.setUserName("miho");
		form.setNickName("匿名");
		form.setProfile("しがない会社員");
		form.setStatus(0);
		form.setGender(2);
		form.setAge(2);
		
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		uniqueNickNameValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
	}
	
	@Test
	@DatabaseSetup(value = "/form/AccountInfo/setup/")
	void ニックネームが重複してユーザ名が一緒ならエラーが発生しない() throws Exception{
		form.setUserName("miho");
		form.setNickName("mack");
		form.setProfile("しがない会社員");
		form.setStatus(0);
		form.setGender(1);
		form.setAge(3);
		
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		uniqueNickNameValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
	}
	
	@ParameterizedTest
	@CsvSource({"亜",
		        "寿限無寿限無後光の擦り切れ回砂利"})
	@DatabaseSetup(value = "/form/AccountInfo/setup/")
	void 指定サイズ範囲外でエラー発生(String nickName) throws Exception{
		form.setUserName("糸井");
		form.setNickName(nickName);
		form.setProfile("123456789012345678901234567890123456789012345678901");
		form.setStatus(3);
		form.setGender(3);
		form.setAge(2);
		
		validator.validate(form, bindingResult);
		assertEquals(2,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("nickName")
				                .toString().contains("ニックネームは2字以上15字以内で作成してください"));
		assertTrue(bindingResult.getFieldError("profile")
				                .toString().contains("50文字以内で入力してください"));
		uniqueNickNameValidator.validate(form, bindingResult);
		assertEquals(2,bindingResult.getFieldErrorCount());
	}
	
	@Test
	@DatabaseSetup(value = "/form/AccountInfo/setup/")
	void ニックネームが重複してユーザ名が一致しないならエラーが発生する() throws Exception{
		form.setUserName("加藤健");
		form.setNickName("sigeno");
		form.setProfile("つい最近嫁に小遣いを削られました");
		form.setStatus(5);
		form.setGender(1);
		form.setAge(3);
		
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		uniqueNickNameValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("nickName")
				                .toString().contains("入力されたニックネームは既に使われています"));
	}
}
