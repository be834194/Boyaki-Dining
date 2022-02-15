package com.dining.boyaki.model.service.conbined;

import java.time.LocalDateTime;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.service.UpdatePasswordService;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class UpdatePasswordServiceCombinedTest {
	
	private static final LocalDateTime datetime = LocalDateTime.of(2022, 2, 10, 20, 39, 45);
	
	private static MockedStatic<LocalDateTime> mock;
	
	@Autowired
	UpdatePasswordService updatePasswordService;
	
	@BeforeEach
	void setUp(){
		MockitoAnnotations.openMocks(this);
		mock = Mockito.mockStatic(LocalDateTime.class,Mockito.CALLS_REAL_METHODS);
		mock.when(LocalDateTime::now).thenReturn(datetime);
	}
	@AfterEach //mockStaticのモック化の解除
    void tearDown() throws Exception {
        mock.close();
	}
	
	@Test
	@DatabaseSetup(value="/service/UpdatePassword/setup/")
	@ExpectedDatabase(value="/service/UpdatePassword/update/",assertionMode=DatabaseAssertionMode.NON_STRICT)
	void updatePasswordでユーザのPWが更新されてPW変更履歴が1件登録される() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setPassword("script-Java");
		form.setMail("miho@gmail.com");
		form.setConfirmPassword("script-Java");
		updatePasswordService.updatePassword(form);
	}

}
