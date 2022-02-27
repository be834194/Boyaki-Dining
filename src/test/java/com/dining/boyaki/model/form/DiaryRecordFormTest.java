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
	void バリデーション問題あり() throws Exception{
		form.setCategoryId(0);
		form.setDiaryDay(null);
		form.setRecord1(null);
		form.setRecord2(null);
		form.setRecord3(null);
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
	
	@Test
	void バリデーション問題なし() throws Exception{
		form.setCategoryId(1);
		form.setDiaryDay(Date.valueOf("2022-02-19"));
		form.setRecord1(null);
		form.setRecord2(null);
		form.setRecord3("サラダ");
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
	}

}
