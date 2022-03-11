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
    void バリデーション問題あり() throws Exception{
    	form.setUserName("糸井");
    	form.setNickName("sigeno");
    	form.setContent("12345678901234567890123456789012345678901234567890"
    			      + "123456789012345678901234567890123456789012345678901");
    	form.setPostCategory(0);
    	validator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("content")
                                .toString().contains("100文字以内で入力してください"));
    }
    
    @Test
    void 空文字でバリデーションエラー発生() throws Exception{
    	form.setUserName("糸井");
    	form.setNickName("sigeno");
    	form.setContent("");
    	form.setPostCategory(0);
    	validator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("content")
                                .toString().contains("投稿内容は必須項目です"));
    }
    
    @Test
    void Nullでバリデーションエラー発生() throws Exception{
    	form.setUserName("糸井");
    	form.setNickName("sigeno");
    	form.setContent(null);
    	form.setPostCategory(0);
    	validator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("content")
                                .toString().contains("投稿内容は必須項目です"));
    }
    
    @Test
    void バリデーション問題なし() throws Exception{
    	form.setUserName("糸井");
    	form.setNickName("sigeno");
    	form.setContent("お腹周りが改善されなくてダイエットめげそう");
    	form.setPostCategory(0);
    	validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
    }

}
