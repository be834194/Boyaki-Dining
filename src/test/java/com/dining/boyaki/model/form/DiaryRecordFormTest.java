package com.dining.boyaki.model.form;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

@SpringBootTest
@Transactional
public class DiaryRecordFormTest {
	
	@Autowired
	Validator validator;
	
	DiaryRecordForm form = new DiaryRecordForm();
	                                            //ターゲット,ターゲットオブジェクトの名前
	BindingResult bindingResult = new BindException(form,"DiaryRecordForm");
	
	@Test
	void バリデーション問題なし() throws Exception{
		form = new DiaryRecordForm("加藤健",1,Date.valueOf("2022-02-19"),
                                   null,null,"サラダ",null,null,null);
		
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
	}
	
	@Test
	void 指定サイズ範囲外でエラー発生() throws Exception{
		form = new DiaryRecordForm("加藤健",1,Date.valueOf("2022-02-19"),
				                   null,null,null,null,null,null);
		form.setRecord1("123456789012345678901234567890123456789012345678901");
		form.setRecord2("123456789012345678901234567890123456789012345678902");
		form.setRecord3("123456789012345678901234567890123456789012345678903");
		form.setMemo("12345678901234567890123456789012345678901234567890"
				   + "123456789012345678901234567890123456789012345678904");
		
		validator.validate(form, bindingResult);
		assertEquals(4,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("record1")
                .toString().contains("50文字以内で入力してください"));
		assertTrue(bindingResult.getFieldError("record2")
                .toString().contains("50文字以内で入力してください"));
		assertTrue(bindingResult.getFieldError("record3")
                .toString().contains("50文字以内で入力してください"));
		assertTrue(bindingResult.getFieldError("memo")
                .toString().contains("100文字以内で入力してください"));
	}
	
	@Test
	void 未入力でエラー発生() throws Exception{
		form = new DiaryRecordForm("加藤健",0,null,
                                   null,null,null,null,"memo",null);
		
		validator.validate(form, bindingResult);
		assertEquals(5,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("categoryId")
                .toString().contains("カテゴリを選んでください"));
		assertTrue(bindingResult.getFieldError("diaryDay")
                .toString().contains("日付を入力してください"));
		assertTrue(bindingResult.getFieldError("record1")
                .toString().contains("いずれかの入力が必須です"));
		assertTrue(bindingResult.getFieldError("record2")
                .toString().contains("いずれかの入力が必須です"));
		assertTrue(bindingResult.getFieldError("record3")
                .toString().contains("いずれかの入力が必須です"));
	}

}
