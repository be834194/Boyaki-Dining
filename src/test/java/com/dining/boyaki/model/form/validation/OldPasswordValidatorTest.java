package com.dining.boyaki.model.form.validation;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import com.dining.boyaki.model.form.PasswordChangeForm;
import com.dining.boyaki.model.service.PasswordHistoryService;

@RunWith(SpringRunner.class)
public class OldPasswordValidatorTest {
	
	@Mock
	PasswordEncoder passwordEncoder;
	
	@Mock
	PasswordHistoryService passwordHistoryService;
	
	@InjectMocks
	OldPasswordValidator oldPasswordValidator;
	
	PasswordChangeForm form = new PasswordChangeForm();
	BindingResult bindingResult = new BindException(form, "PasswordChangeForm");
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void validateでパスワードが一致するのでエラーが発生しない() throws Exception{
		form.setUserName("糸井");
		form.setMail("mother@yahoo.co.jp");
		form.setOldPassword("sigeSIGE");
		when(passwordHistoryService.findPassword("糸井", "mother@yahoo.co.jp")).thenReturn("sigeSIGE");
		when(passwordEncoder.matches("sigeSIGE", "sigeSIGE")).thenReturn(true);
		
		oldPasswordValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		verify(passwordHistoryService,times(1)).findPassword("糸井", "mother@yahoo.co.jp");
		verify(passwordEncoder,times(1)).matches("sigeSIGE", "sigeSIGE");
	}
	
	@Test
	void validateでパスワードが一致しないのでエラーが発生する() throws Exception{
		form.setUserName("糸井");
		form.setMail("mother@yahoo.co.jp");
		form.setOldPassword("sigesige");
		when(passwordHistoryService.findPassword("糸井", "mother@yahoo.co.jp")).thenReturn("sigeSIGE");
		when(passwordEncoder.matches("sigesige", "sigeSIGE")).thenReturn(false);
		
		oldPasswordValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("oldPassword")
				.toString().contains("メールアドレスに誤りがあるか、ログイン中のパスワードと異なります"));
		verify(passwordHistoryService,times(1)).findPassword("糸井", "mother@yahoo.co.jp");
		verify(passwordEncoder,times(1)).matches("sigesige", "sigeSIGE");
	}
	
	@Test
	void validateでパスワードが取得できないのでエラーが発生する() throws Exception{
		form.setUserName("糸井");
		form.setMail("father@yahoo.co.jp");
		form.setOldPassword("sigeSIGE");
		when(passwordHistoryService.findPassword("糸井", "father@yahoo.co.jp")).thenReturn(null);
		when(passwordEncoder.matches("sigeSIGE", "sigeSIGE")).thenReturn(false);
		
		oldPasswordValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("oldPassword")
				.toString().contains("メールアドレスに誤りがあるか、ログイン中のパスワードと異なります"));
		verify(passwordHistoryService,times(1)).findPassword("糸井", "father@yahoo.co.jp");
		verify(passwordEncoder,times(0)).matches("sigeSIGE", "sigeSIGE");
	}

}
