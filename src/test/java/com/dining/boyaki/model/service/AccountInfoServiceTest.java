package com.dining.boyaki.model.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
			info = new AccountInfo("糸井","sigeno","今年中に体重5キロ落としたい",
					               3,3,2,165,60,null,null);
			form = new AccountInfoForm("糸井","sigeno","今年中に体重5キロ落としたい",
					                   3,3,2,165,60);
			when(accountInfoMapper.findAccountInfo("糸井")).thenReturn(info);
			when(accountInfoMapper.findAccountInfo("miho")).thenReturn(null);
		}
	
		@Test
		void findAccountInfoでユーザ情報レコードを1件取得する() {
			AccountInfoForm result =  accountInfoService.findAccountInfo("糸井");
			assertEquals("糸井",result.getUserName());
			assertEquals("sigeno",result.getNickName());
			assertEquals("今年中に体重5キロ落としたい",result.getProfile());
			assertEquals(3,result.getStatus());
			assertEquals(3,result.getGender());
			assertEquals(2,result.getAge());;
			verify(accountInfoMapper,times(1)).findAccountInfo("糸井");
			
			result =  accountInfoService.findAccountInfo("miho");
			assertEquals(null,result);
			verify(accountInfoMapper,times(1)).findAccountInfo("miho");
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
		PasswordChangeForm form = new PasswordChangeForm("加藤健","example@ezweb.ne.jp","pinballs",
				                                         "wonderSong","wonderSong");
		Account account = new Account("加藤健","wonderSong","example@ezweb.ne.jp",null);
		
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
