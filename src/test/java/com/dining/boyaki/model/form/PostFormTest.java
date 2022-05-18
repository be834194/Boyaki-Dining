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
public class PostFormTest {
	
	@Autowired
	Validator validator;
	
	PostForm form = new PostForm();
                                                //ターゲット,ターゲットオブジェクトの名前
    BindingResult bindingResult = new BindException(form,"PostForm");
    
    @Test
    void バリデーション問題なし() throws Exception{
    	form = new PostForm("糸井","sigeno","お腹周りが改善されなくてダイエットめげそう",0);
    	
    	validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
    }
    
    @Test
    void 指定サイズ範囲外でエラー発生() throws Exception{
    	form = new PostForm("糸井","sigeno",null,0);
    	form.setContent("12345678901234567890123456789012345678901234567890"
    			      + "123456789012345678901234567890123456789012345678901");
    	
    	validator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("content")
                                .toString().contains("100文字以内で入力してください"));
    }
    
    @Test
    void 空文字でバリデーションエラー発生() throws Exception{
    	form = new PostForm("糸井","sigeno","",0);
    	
    	validator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("content")
                                .toString().contains("投稿内容は必須項目です"));
    }
    
    @Test
    void Nullでバリデーションエラー発生() throws Exception{
    	form = new PostForm("糸井","sigeno",null,0);
    	
    	validator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("content")
                                .toString().contains("投稿内容は必須項目です"));
    }

}
