package com.dining.boyaki.model.service.conbined;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

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

import com.dining.boyaki.model.form.AccountInfoForm;
import com.dining.boyaki.model.form.PasswordChangeForm;
import com.dining.boyaki.model.service.AccountInfoService;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class AccountInfoServiceCombinedTest {
	
	private static final LocalDateTime datetime = LocalDateTime.of(2022, 2, 10, 20, 39, 45);
	
	private static MockedStatic<LocalDateTime> mock;
	
	@Autowired
	AccountInfoService  accountInfoService;
	
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
	@DatabaseSetup(value = "/service/AccountInfo/setup/")
	void findAccountInfoでユーザ情報レコードを1件取得する() {
		AccountInfoForm form = accountInfoService.findAccountInfo("糸井");
		assertEquals("糸井",form.getUserName());
		assertEquals("sigeno",form.getNickName());
		assertEquals("今年中に体重5キロ落としたい",form.getProfile());
		assertEquals(3,form.getStatus());
		assertEquals(3,form.getGender());
		assertEquals(2,form.getAge());
		
		form = accountInfoService.findAccountInfo("健太郎");
		assertEquals(null,form);
	}
	
	@Test
	@DatabaseSetup(value = "/service/AccountInfo/setup/")
	@ExpectedDatabase(value = "/service/AccountInfo/update/account_info/")
	void updateAccountInfoでユーザ情報レコードを更新する() {
		AccountInfoForm info = new AccountInfoForm();
		info.setUserName("加藤健");
		info.setNickName("kenken");
		info.setProfile("間食が止まらない");
		info.setStatus(3);
		info.setGender(1);
		info.setAge(3);
		accountInfoService.updateAccountInfo(info);
	}
	
	@Test
	@DatabaseSetup(value = "/service/AccountInfo/setup/")
	@ExpectedDatabase(value = "/service/AccountInfo/update/account/",assertionMode=DatabaseAssertionMode.NON_STRICT)
	void updatePasswordでパスワードを更新する() {
		PasswordChangeForm form = new PasswordChangeForm();
		form.setUserName("miho");
		form.setMail("miho@gmail.com");
		form.setOldPassword("ocean-Nu");
		form.setPassword("script-Java");
		form.setConfirmPassword("script-Java");
		accountInfoService.updatePassword(form);
	}
	
	@Test
	@DatabaseSetup(value = "/service/AccountInfo/setup/")
	@ExpectedDatabase(value = "/service/AccountInfo/delete/")
	void deleteAccountでアカウントを削除する() {
		accountInfoService.deleteAccount("加藤健");
	}

}
