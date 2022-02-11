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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.service.RegistrationService;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class RegistrationServiceCombinedTest {
	
	private static final LocalDateTime datetime = LocalDateTime.of(2022, 2, 8, 11, 00, 52);
	
	private static MockedStatic<LocalDateTime> mock;
	
	@Autowired
	RegistrationService registrationService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		//CALLS_REAL_METHODSで、部分的なMock化を可能にできる
		mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
		mock.when(LocalDateTime::now).thenReturn(datetime);
	}
	
	@AfterEach //mockStaticのモック化の解除
    void tearDown() throws Exception {
        mock.close();
    
	}
	
	@Test
	@DatabaseSetup(value="/service/Registration/setup/")
	@ExpectedDatabase(value="/service/Registration/insert/",assertionMode=DatabaseAssertionMode.NON_STRICT)
	void insertAccountでユーザとPW変更履歴が1件ずつ登録される() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setUserName("マクベイ");
		form.setPassword("sun-flan-sis");
		form.setMail("north-east@gmail.com");
		form.setConfirmPassword("sun-flan-sis");
		
		registrationService.insertAccount(form);
	}

}
