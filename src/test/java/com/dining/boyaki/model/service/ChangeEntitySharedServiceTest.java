package com.dining.boyaki.model.service;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.model.form.RegisterForm;

@RunWith(SpringRunner.class)
public class ChangeEntitySharedServiceTest {
	
	private static final LocalDateTime datetime = LocalDateTime.parse("2022-02-08T11:00:52");
	
	private static MockedStatic<LocalDateTime> mock;
	
	ChangeEntitySharedService changeEntitySharedService = new ChangeEntitySharedService();
	
	@BeforeEach
	void setup() {
		mock = Mockito.mockStatic(LocalDateTime.class,Mockito.CALLS_REAL_METHODS);
		mock.when(LocalDateTime::now).thenReturn(datetime);
	}
	
	@AfterEach
	void tearDown() throws Exception{
		mock.close();
	}
	
	@Test
    void setToAccountでRegisterFormをAccountに詰め替える() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setUserName("マクベイ");
		form.setPassword("encodedPassword");
		form.setMail("north-east@gmail.com");
		form.setConfirmPassword("sun-flan-sis");
		Account account = changeEntitySharedService.setToAccount(form);
		assertEquals(account.getUserName(),"マクベイ");
		assertEquals(account.getPassword(),"encodedPassword");
		assertEquals(account.getMail(),"north-east@gmail.com");
		assertEquals(account.getRole(),"ROLE_USER");
	}
	
	@Test
    void setToAccountでRegisterFormをPasswordHistoryに詰め替える() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setUserName("マクベイ");
		form.setPassword("encodedPassword");
		form.setMail("north-east@gmail.com");
		form.setConfirmPassword("sun-flan-sis");
		PasswordHistory history = changeEntitySharedService.setToPasswordHistory(form);
		assertEquals(history.getUserName(),"マクベイ");
		assertEquals(history.getPassword(),"encodedPassword");
		assertEquals(history.getUseDay(),datetime);
	}

}
