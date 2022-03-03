package com.dining.boyaki.model.form.validation.combined;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import com.dining.boyaki.model.form.PasswordChangeForm;
import com.dining.boyaki.model.form.validation.NotReusedPasswordValidator;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class NotReusedPasswordValidatorCombinedTest {
	
	private static final LocalDateTime datetime = LocalDateTime.of(2022, 01, 14, 0, 0, 0);
	
	private static MockedStatic<LocalDateTime> mock;
	
	@Autowired
	NotReusedPasswordValidator notReusedPasswordValidator;
	
	PasswordChangeForm form = new PasswordChangeForm();
	BindingResult bindingResult = new BindException(form, "PasswordChangeForm");
	
	@BeforeEach
	void setUp() {
		mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
		mock.when(LocalDateTime::now).thenReturn(datetime);
	}
	
	@AfterEach //mockStaticのモック化の解除
    void tearDown() throws Exception {
        mock.close();
    }
	
	@Test
	@DatabaseSetup(value = "/validation/NotReusedPassword/setup/")
	void validateで新パスワードが重複せずエラーが発生しない() throws Exception{
		form.setUserName("加藤健");
		form.setMail("example@ezweb.ne.jp");
		form.setOldPassword("pinballs");
		form.setPassword("wonderSong"); //31日前のパスワード
		form.setConfirmPassword("wonderSong");
		
		notReusedPasswordValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
	}
	
	@Test
	@DatabaseSetup(value = "/validation/NotReusedPassword/setup/")
	void validateで30日以内のパスワード変更履歴がない場合はエラーが発生しない() throws Exception{
		form.setUserName("糸井");
		form.setMail("mother@yahoo.co.jp");
		form.setOldPassword("sigeSIGE");
		form.setPassword("star-Man");
		form.setConfirmPassword("star-Man");
		
		notReusedPasswordValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
	}
	
	@Test
	@DatabaseSetup(value = "/validation/NotReusedPassword/setup/")
	void validateでメールアドレスの誤りでエラーが発生する() throws Exception{
		form.setUserName("加藤健");
		form.setMail("hogehoge@ezweb.ne.jp");
		form.setOldPassword("pinballs");
		form.setPassword("wonderSong");
		form.setConfirmPassword("wonderSong");
		
		notReusedPasswordValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("mail")
				.toString().contains("メールアドレスに誤りがあります"));
	}
	
	@Test
	@DatabaseSetup(value = "/validation/NotReusedPassword/setup/")
	void validateで旧パスワードと一致してエラーが発生する() throws Exception{
		form.setUserName("加藤健");
		form.setMail("example@ezweb.ne.jp");
		form.setOldPassword("pinballs");
		form.setPassword("pinballs");
		form.setConfirmPassword("pinballs");
		
		notReusedPasswordValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("password")
				.toString().contains("ログイン中のパスワードと一緒です"));
	}
	
	@Test
	@DatabaseSetup(value = "/validation/NotReusedPassword/setup/")
	void validateで新パスワードが過去30日以内のパスワードと一致してエラーが発生する() throws Exception{
		form.setUserName("加藤健");
		form.setMail("example@ezweb.ne.jp");
		form.setOldPassword("pinballs");
		form.setPassword("ten_bear"); // //30日前のパスワード
		form.setConfirmPassword("ten_bear");
		
		notReusedPasswordValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("password")
				.toString().contains("30日以内に使用したパスワードはご利用できません"));
	}

}
