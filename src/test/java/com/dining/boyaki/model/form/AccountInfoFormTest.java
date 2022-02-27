package com.dining.boyaki.model.form;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

@SpringBootTest
@Transactional
public class AccountInfoFormTest {
	
	@Autowired
	Validator validator;
	
	AccountInfoForm form = new AccountInfoForm();
                                                  //ターゲット,ターゲットオブジェクトの名前
	BindingResult bindingResult = new BindException(form,"AccountInfoForm");
	
	@Test
	void バリデーション問題なし() throws Exception{
		form.setUserName("マクベイ");
		form.setNickName("mack");
		form.setProfile("しがない会社員");
		form.setStatus(0);
		form.setGender(1);
		form.setAge(37);
		
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
	}
	
	@Test
	void バリデーション問題あり() throws Exception{
		form.setUserName("マクベイ");
		form.setNickName(null);
		form.setProfile("123456789012345678901234567890123456789012345678901");
		form.setStatus(0);
		form.setGender(1);
		form.setAge(37);
		
		validator.validate(form, bindingResult);
		assertEquals(2,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("nickName")
				.toString().contains("ニックネームは必須項目です"));
		assertTrue(bindingResult.getFieldError("profile")
				.toString().contains("50文字以内で入力してください"));
	}
}
