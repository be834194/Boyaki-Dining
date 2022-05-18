package com.dining.boyaki.model.form;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import org.springframework.validation.Validator;

import com.dining.boyaki.model.form.validation.NotReusedPasswordValidator;
import com.dining.boyaki.model.form.validation.OldPasswordValidator;
import com.dining.boyaki.util.CsvDataSetLoader;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
                         TransactionalTestExecutionListener.class,
                         DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class PasswordChangeFormTest {
	
	private static final LocalDateTime datetime = LocalDateTime.of(2022, 01, 14, 0, 0, 0);
	
	private static MockedStatic<LocalDateTime> mock;
	
	@Autowired
	Validator validator;
	
	@Autowired
	NotReusedPasswordValidator notReusedPasswordValidator;
	
	@Autowired
	OldPasswordValidator oldPasswordValidator;
	
	PasswordChangeForm form = new PasswordChangeForm();
                                                //ターゲット,ターゲットオブジェクトの名前
	BindingResult bindingResult = new BindException(form,"RegistrationForm");
	
	@BeforeEach
	void setUp() {
		mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
		mock.when(LocalDateTime::now).thenReturn(datetime);
	}
	
	@AfterEach
    void tearDown() throws Exception {
        mock.close();
    }
	
	@Test
	@DatabaseSetup(value = "/form/setup/")
	void バリデーション問題なし() throws Exception{
		form = new PasswordChangeForm("加藤健","example@ezweb.ne.jp","pinballs",
				                      null,"wonderSong");
		form.setPassword("wonderSong"); //31日前のパスワード
		
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		
		oldPasswordValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		
		notReusedPasswordValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
	}
	
	@Test
	@DatabaseSetup(value = "/form/setup/")
	void validateで30日以内のパスワード変更履歴がない場合はエラーが発生しない() throws Exception{
		form = new PasswordChangeForm("糸井","mother@yahoo.co.jp","sigeSIGE",
				                      "star-Man","star-Man");
		
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		
		oldPasswordValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		
		notReusedPasswordValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
	}
	
	@ParameterizedTest
	@CsvSource({"passwor",
		        "passwordpasswordp"})
	void 未入力や指定サイズ範囲外でフィールドエラー発生(String password) throws Exception{
		form = new PasswordChangeForm("糸井",null,"sigeSIGE",
                                      password,"");
		
		validator.validate(form, bindingResult);
		assertEquals(2,bindingResult.getFieldErrorCount());;
		assertTrue(bindingResult.getFieldError("password")
                                .toString().contains("パスワードは8～16文字で入力してください"));
		assertTrue(bindingResult.getFieldError("confirmPassword")
                                .toString().contains("パスワードが一致していません"));
		
		oldPasswordValidator.validate(form, bindingResult);
		assertEquals(3,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("oldPassword")
                                .toString().contains("メールアドレスに誤りがあるか、ログイン中のパスワードと異なります"));
		
		notReusedPasswordValidator.validate(form, bindingResult);
		assertEquals(4,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("mail")
                                .toString().contains("メールアドレスに誤りがあります"));
	}
	
	@Test
	@DatabaseSetup(value = "/form/setup/")
	void メールアドレスの誤りでエラーが発生する() throws Exception{
		form = new PasswordChangeForm("miho","miki@gmail.com","ocean-Nu",
				                      "script-Java","script-Java");
		
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		
		oldPasswordValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("oldPassword")
				                .toString().contains("メールアドレスに誤りがあるか、ログイン中のパスワードと異なります"));
		
		notReusedPasswordValidator.validate(form, bindingResult);
		assertEquals(2,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("mail")
				                .toString().contains("メールアドレスに誤りがあります"));
	}
	
	@Test
	@DatabaseSetup(value = "/form/setup/")
	void 旧パスワードの不一致でエラーが発生する() throws Exception{
		form = new PasswordChangeForm("糸井","mother@yahoo.co.jp","SIGEsige",
				                      "star-Man","star-Man");
		
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		
		oldPasswordValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("oldPassword")
				                .toString().contains("メールアドレスに誤りがあるか、ログイン中のパスワードと異なります"));
		
		notReusedPasswordValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
	}
	
	@Test
	@DatabaseSetup(value = "/form/setup/")
	void 新パスワードが旧パスワードと一致してエラーが発生する() throws Exception{
		form = new PasswordChangeForm("加藤健","example@ezweb.ne.jp","pinballs",
				                      "pinballs","pinballs");
		
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		
		oldPasswordValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		
		notReusedPasswordValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("password")
				                .toString().contains("ログイン中のパスワードと一緒です"));
	}
	
	@Test
	@DatabaseSetup(value = "/form/setup/")
	void 新パスワードが過去30日以内のパスワードと一致してエラーが発生する() throws Exception{
		form = new PasswordChangeForm("加藤健","example@ezweb.ne.jp","pinballs",
                                      null,"ten_bear");
		form.setPassword("ten_bear"); // //30日前のパスワード
		
		validator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		
		oldPasswordValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		
		notReusedPasswordValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("password")
				                .toString().contains("30日以内に使用したパスワードはご利用できません"));
	}

}
