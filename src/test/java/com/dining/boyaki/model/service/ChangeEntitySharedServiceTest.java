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
import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.model.form.PasswordChangeForm;
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
		assertEquals("マクベイ",account.getUserName());
		assertEquals("encodedPassword",account.getPassword());
		assertEquals("north-east@gmail.com",account.getMail());
		assertEquals("ROLE_USER",account.getRole());
	}
	
	@Test
    void setToAccountでPasswordChangeFormをAccountに詰め替える() throws Exception{
		PasswordChangeForm form = new PasswordChangeForm();
		form.setUserName("加藤健");
		form.setMail("example@ezweb.ne.jp");
		form.setOldPassword("pinballs");
		form.setPassword("wonderSong");
		form.setConfirmPassword("WONDERSONG");
		
		Account account = changeEntitySharedService.setToAccount(form);
		assertEquals("加藤健",account.getUserName());
		assertEquals("wonderSong",account.getPassword());
		assertEquals("example@ezweb.ne.jp",account.getMail());
		assertEquals(null,account.getRole());
	}
	
	@Test
    void setToPasswordHistoryでRegisterFormをPasswordHistoryに詰め替える() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setUserName("マクベイ");
		form.setPassword("encodedPassword");
		form.setMail("north-east@gmail.com");
		form.setConfirmPassword("sun-flan-sis");
		
		PasswordHistory history = changeEntitySharedService.setToPasswordHistory(form);
		assertEquals("マクベイ",history.getUserName());
		assertEquals("encodedPassword",history.getPassword());
		assertEquals(datetime,history.getUseDay());
	}
	
	@Test
    void setToPasswordHistoryでPasswordChangeFormをPasswordHistoryに詰め替える() throws Exception{
		PasswordChangeForm form = new PasswordChangeForm();
		form.setUserName("加藤健");
		form.setMail("example@ezweb.ne.jp");
		form.setOldPassword("pinballs");
		form.setPassword("wonderSong");
		form.setConfirmPassword("WONDERSONG");
		
		PasswordHistory history = changeEntitySharedService.setToPasswordHistory(form);
		assertEquals("加藤健",history.getUserName());
		assertEquals("wonderSong",history.getPassword());
		assertEquals(datetime,history.getUseDay());
	}
	
	@Test
    void setToAccountInfoでRegisterFormをAccountInfoに詰め替える() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setUserName("マクベイ");
		form.setPassword("encodedPassword");
		form.setMail("north-east@gmail.com");
		form.setConfirmPassword("sun-flan-sis");
		
		AccountInfo info = changeEntitySharedService.setToAccountInfo(form);
		assertEquals("マクベイ",info.getUserName());
		assertEquals("マクベイ",info.getNickName());
		assertEquals(null,info.getProfile());
		assertEquals(0,info.getStatus());
		assertEquals(0,info.getGender());
		assertEquals(0,info.getAge());
	}
	
}
