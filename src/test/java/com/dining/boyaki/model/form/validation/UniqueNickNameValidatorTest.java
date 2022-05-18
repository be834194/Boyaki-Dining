package com.dining.boyaki.model.form.validation;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.form.AccountInfoForm;
import com.dining.boyaki.model.service.FindDataSharedService;

@RunWith(SpringRunner.class)
public class UniqueNickNameValidatorTest {
	
	@Mock
	FindDataSharedService findDataSharedService;
	
	@InjectMocks
	UniqueNickNameValidator uniqueNickNameValidator;
	
	AccountInfoForm form = new AccountInfoForm();
	BindingResult bindingResult = new BindException(form, "RegisterForm");
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void validateでニックネームが重複せずエラーが発生しない() throws Exception{
		form.setUserName("加藤健");
		form.setNickName("kenken");
		when(findDataSharedService.findNickName("kenken")).thenReturn(null);
		
		uniqueNickNameValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		verify(findDataSharedService,times(1)).findNickName("kenken");
	}
	
	@Test
	void validateでニックネームが重複してユーザ名が一緒ならエラーが発生しない() throws Exception{
		AccountInfo info = new AccountInfo();
		info.setUserName("加藤健");
		info.setNickName("kenken");
		form.setUserName("加藤健");
		form.setNickName("kenken");
		when(findDataSharedService.findNickName("kenken")).thenReturn(info);
		
		uniqueNickNameValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		verify(findDataSharedService,times(1)).findNickName("kenken");
	}
	
	@Test
	void validateでニックネームが重複してエラーが発生する() throws Exception{
		AccountInfo info = new AccountInfo();
		info.setUserName("加藤健");
		info.setNickName("匿名ちゃん");
		form.setUserName("miho");
		form.setNickName("匿名ちゃん");
		when(findDataSharedService.findNickName("匿名ちゃん")).thenReturn(info);
		
		uniqueNickNameValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("nickName")
				                .toString().contains("入力されたニックネームは既に使われています"));
		verify(findDataSharedService,times(1)).findNickName("匿名ちゃん");
	}

}
