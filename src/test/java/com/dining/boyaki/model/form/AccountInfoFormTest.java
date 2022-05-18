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
	@DatabaseSetup(value = "/form/setup/")
	void バリデーション問題なし() throws Exception{
		form = new AccountInfoForm("miho","axios","しがない会社員",
				                   0,2,2,160,65);
		
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		
		uniqueNickNameValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
	}
	
	@Test
	@DatabaseSetup(value = "/form/setup/")
	void ニックネームが重複してユーザ名が一緒ならエラーが発生しない() throws Exception{
		form = new AccountInfoForm("miho","匿名","しがない会社員",
                                   0,1,3,160,65);
		
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		
		uniqueNickNameValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
	}
	
	@ParameterizedTest
	@CsvSource({"亜,0,0",
		        "寿限無寿限無後光の擦り切れ回砂利,301,301"})
	@DatabaseSetup(value = "/form/setup/")
	void 指定サイズ範囲外でエラー発生(String nickName,float height,float weight) throws Exception{
		form = new AccountInfoForm("糸井",nickName,null,
                                   3,3,2,height,weight);
		form.setProfile("123456789012345678901234567890123456789012345678901");
		
		validator.validate(form, bindingResult);
		assertEquals(4,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("nickName")
				                .toString().contains("ニックネームは2字以上15字以内で作成してください"));
		assertTrue(bindingResult.getFieldError("profile")
				                .toString().contains("50文字以内で入力してください"));
		assertTrue(bindingResult.getFieldError("height")
                                .toString().contains("正しい値を入力してください"));
		assertTrue(bindingResult.getFieldError("weight")
                                .toString().contains("正しい値を入力してください"));
		
		uniqueNickNameValidator.validate(form, bindingResult);
		assertEquals(4,bindingResult.getFieldErrorCount());
	}
	
	@Test
	@DatabaseSetup(value = "/form/setup/")
	void ニックネームが重複してユーザ名が一致しないならエラーが発生する() throws Exception{
		form = new AccountInfoForm("加藤健","sigeno","つい最近嫁に小遣いを削られました",
                                   5,1,3,160,65);
		
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		
		uniqueNickNameValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("nickName")
				                .toString().contains("入力されたニックネームは既に使われています"));
	}
}
