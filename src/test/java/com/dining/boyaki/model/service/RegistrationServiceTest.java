package com.dining.boyaki.model.service;

import static org.mockito.Mockito.any;
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
	
	@InjectMocks
	RegistrationService registrationService;
	
	@BeforeEach
    void setUp() {
    	MockitoAnnotations.openMocks(this);
    }
	
	@Test
	void insertAccountでaccountとpassword_historyとaccount_infoが登録される() throws Exception{
		RegisterForm form = new RegisterForm("マクベイ","north-east@gmail.com","sun-flan-sis","sun-flan-sis");
		
		when(passwordEncoder.encode(form.getPassword())).thenReturn("sun-flan-sis");
		doNothing().when(registrationMapper).insertAccount(any(Account.class));
		doNothing().when(registrationMapper).insertPasswordHistory(any(PasswordHistory.class));
		doNothing().when(registrationMapper).insertAccountInfo(any(AccountInfo.class));
		
		registrationService.insertAccount(form);
		verify(passwordEncoder,times(1)).encode(form.getPassword());
		verify(registrationMapper,times(1)).insertAccount(any(Account.class));
		verify(registrationMapper,times(1)).insertPasswordHistory(any(PasswordHistory.class));
		verify(registrationMapper,times(1)).insertAccountInfo(any(AccountInfo.class));
	}

}
