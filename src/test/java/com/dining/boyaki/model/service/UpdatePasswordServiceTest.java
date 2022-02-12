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
import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.mapper.UpdatePasswordMapper;

@RunWith(SpringRunner.class)
@Transactional
public class UpdatePasswordServiceTest {
	
	@Mock
	UpdatePasswordMapper updatePasswordMapper;
	
	@Mock
	ChangeEntitySharedService changeEntitySharedService;
    
	@Mock
    FindDataSharedService findDataSharedService;
	
	@Mock
	PasswordEncoder passwordEncoder;
	
	@InjectMocks
	UpdatePasswordService updatePasswordService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void updatePasswordでユーザのPWが更新されてPW変更履歴が1件登録される() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setPassword("script-Java");
		form.setMail("miho@gmail.com");
		form.setConfirmPassword("script-Java");
		Account account = new Account();
		account.setUserName("miho");
		account.setPassword("script-Java");
		account.setMail("miho@gmail.com");
		PasswordHistory history = new PasswordHistory();
		history.setUserName("miho");
		history.setPassword("script-Java");
		history.setUseDay(LocalDateTime.now());
		
		when(findDataSharedService.findUserNameFromMail("miho@gmail.com")).thenReturn("miho");
		when(passwordEncoder.encode(form.getPassword())).thenReturn("script-Java");
		when(changeEntitySharedService.setToAccount(form)).thenReturn(account);
		when(changeEntitySharedService.setToPasswordHistory(form)).thenReturn(history);
		doNothing().when(updatePasswordMapper).updatePassword(account);
		doNothing().when(updatePasswordMapper).insertPasswordHistory(history);
		
		updatePasswordService.updatePassword(form);
		verify(findDataSharedService,times(1)).findUserNameFromMail("miho@gmail.com");
		verify(passwordEncoder,times(1)).encode(form.getPassword());
		verify(changeEntitySharedService,times(1)).setToAccount(form);
		verify(changeEntitySharedService,times(1)).setToPasswordHistory(form);
		verify(updatePasswordMapper,times(1)).updatePassword(account);
		verify(updatePasswordMapper,times(1)).insertPasswordHistory(history);
	}

}
