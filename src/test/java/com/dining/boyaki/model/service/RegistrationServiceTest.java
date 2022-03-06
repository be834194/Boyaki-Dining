package com.dining.boyaki.model.service;

import java.time.LocalDateTime;
import static org.mockito.Mockito.doNothing;
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
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.mapper.RegistrationMapper;

@RunWith(SpringRunner.class)
@Transactional
public class RegistrationServiceTest {
	
	@Mock
	PasswordEncoder passwordEncoder;
	
	@Mock
	RegistrationMapper registrationMapper;
	
	@Mock
	ChangeEntitySharedService changeEntitySharedService;
	
	@InjectMocks
	RegistrationService registrationService;
	
	@BeforeEach
    void setUp() {
    	MockitoAnnotations.openMocks(this);
    }
	
	@Test
	void insertAccountでaccountとpassword_historyとaccount_infoが登録される() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setUserName("マクベイ");
		form.setPassword("sun-flan-sis");
		form.setMail("north-east@gmail.com");
		form.setConfirmPassword("sun-flan-sis");
		Account account = new Account();
		account.setUserName("マクベイ");
		account.setPassword("sun-flan-sis");
		account.setRole("ROLE_USER");
		account.setMail("north-east@gmail.com");
		PasswordHistory history = new PasswordHistory();
		history.setUserName("マクベイ");
		history.setPassword("sun-flan-sis");
		history.setUseDay(LocalDateTime.now());
		AccountInfo info = new AccountInfo();
		info.setUserName("マクベイ");
		info.setNickName("マクベイ");
		info.setProfile(null);
		info.setStatus(0);
		info.setGender(0);
		info.setAge(20);
		
		when(passwordEncoder.encode(form.getPassword())).thenReturn("sun-flan-sis");
		when(changeEntitySharedService.setToAccount(form)).thenReturn(account);
		when(changeEntitySharedService.setToPasswordHistory(form)).thenReturn(history);
		when(changeEntitySharedService.setToAccountInfo(form)).thenReturn(info);
		doNothing().when(registrationMapper).insertAccount(account);
		doNothing().when(registrationMapper).insertPasswordHistory(history);
		doNothing().when(registrationMapper).insertAccountInfo(info);
		
		registrationService.insertAccount(form);
		verify(passwordEncoder,times(1)).encode(form.getPassword());
		verify(changeEntitySharedService,times(1)).setToAccount(form);
		verify(changeEntitySharedService,times(1)).setToPasswordHistory(form);
		verify(changeEntitySharedService,times(1)).setToAccountInfo(form);
		verify(registrationMapper,times(1)).insertAccount(account);
		verify(registrationMapper,times(1)).insertPasswordHistory(history);
		verify(registrationMapper,times(1)).insertAccountInfo(info);
	}

}
