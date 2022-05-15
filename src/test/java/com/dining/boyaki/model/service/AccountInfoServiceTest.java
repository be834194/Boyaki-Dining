package com.dining.boyaki.model.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import com.dining.boyaki.model.form.AccountInfoForm;
import com.dining.boyaki.model.form.PasswordChangeForm;
import com.dining.boyaki.model.mapper.AccountInfoMapper;

@RunWith(SpringRunner.class)
@Transactional
public class AccountInfoServiceTest {
	
	@Mock
	AccountInfoMapper accountInfoMapper;
	
	@Mock
	PasswordEncoder passwordEncoder;
	
	@InjectMocks
	AccountInfoService accountInfoService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Nested
	class AccountInfoMethods {
		AccountInfo info;
		AccountInfoForm form;
		
		@BeforeEach
		void setUp(){
			info = new AccountInfo();
			info.setUserName("糸井");
			info.setNickName("sigeno");
			info.setProfile("今年中に体重5キロ落としたい");
			info.setStatus(3);
			info.setGender(3);
			info.setAge(2);
			form = new AccountInfoForm();
			form.setUserName("糸井");
			form.setNickName("sigeno");
			form.setProfile("今年中に体重5キロ落としたい");
			form.setStatus(3);
			form.setGender(3);
			form.setAge(2);
		}
	
		@Test
		void findAccountInfoでユーザ情報レコードを1件取得する() {
			when(accountInfoMapper.findAccountInfo("糸井")).thenReturn(info);
			
			AccountInfoForm result =  accountInfoService.findAccountInfo("糸井");
			assertEquals("糸井",result.getUserName());
			assertEquals("sigeno",result.getNickName());
			assertEquals("今年中に体重5キロ落としたい",result.getProfile());
			assertEquals(3,result.getStatus());
			assertEquals(3,result.getGender());
			assertEquals(2,result.getAge());
			verify(accountInfoMapper,times(1)).findAccountInfo("糸井");
		}
	
		@Test
		void findAccountInfoでユーザ情報レコードが取得できない場合はnullが返ってくる() {
			when(accountInfoMapper.findAccountInfo("糸井")).thenReturn(null);
			
			AccountInfoForm result =  accountInfoService.findAccountInfo("糸井");
			assertEquals(null,result);
			verify(accountInfoMapper,times(1)).findAccountInfo("糸井");
		}
		
		@Test
		void updateAccountInfoでユーザ情報レコードを更新する() {
			doNothing().when(accountInfoMapper).updateAccountInfo(any(AccountInfo.class));
			
			accountInfoService.updateAccountInfo(form);
			verify(accountInfoMapper,times(1)).updateAccountInfo(any(AccountInfo.class));
		}
	}
	
	@Test
	void updatePasswordでパスワードを更新する() {
		PasswordChangeForm form = new PasswordChangeForm();
		form.setUserName("加藤健");
		form.setMail("example@ezweb.ne.jp");
		form.setOldPassword("pinballs");
		form.setPassword("wonderSong");
		form.setConfirmPassword("wonderSong");
		
		Account account = new Account();
		account.setUserName("加藤健");
		account.setPassword("wonderSong");
		account.setMail("example@ezweb.ne.jp");
		PasswordHistory history = new PasswordHistory();
		history.setUserName("加藤健");
		history.setPassword("wonderSong");
		history.setUseDay(LocalDateTime.now());
		
		when(passwordEncoder.encode(form.getPassword())).thenReturn(account.getPassword());
		doNothing().when(accountInfoMapper).updatePassword(any(Account.class));
		doNothing().when(accountInfoMapper).insertPasswordHistory(any(PasswordHistory.class));
		
		accountInfoService.updatePassword(form);
		verify(passwordEncoder,times(1)).encode(form.getPassword());;
		verify(accountInfoMapper,times(1)).updatePassword(any(Account.class));
		verify(accountInfoMapper,times(1)).insertPasswordHistory(any(PasswordHistory.class));
	}
	
	@Test
	void deleteAccountでアカウントを削除する() {
		doNothing().when(accountInfoMapper).deleteAccount("加藤健");
		accountInfoService.deleteAccount("加藤健");
		verify(accountInfoMapper,times(1)).deleteAccount("加藤健");
	}

}
