package com.dining.boyaki.model.form;

import java.sql.Date;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
                         TransactionalTestExecutionListener.class,
                         DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class DiaryRecordFormTest {
	
	@Autowired
	Validator validator;
	
	DiaryRecordForm form = new DiaryRecordForm();
	
	BindingResult bindingResult = new BindException(form,"RegistrationForm");
	
	@Test
	void バリデーション問題あり() throws Exception{
		form.setRecord1("");
		form.setRecord2("");
		form.setRecord3("");
		validator.validate(form, bindingResult);
		assertTrue(bindingResult.getFieldError("categoryId")
                .toString().contains("カテゴリを選んでください"));
		assertTrue(bindingResult.getFieldError("diaryDay")
                .toString().contains("日付を入力してください"));
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
		assertEquals(null,bindingResult.getFieldError());
	}

}
