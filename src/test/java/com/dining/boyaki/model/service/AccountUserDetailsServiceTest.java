package com.dining.boyaki.model.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.AccountUserDetails;
import com.dining.boyaki.model.mapper.LoginMapper;

@RunWith(SpringRunner.class)
@Transactional
public class AccountUserDetailsServiceTest {
	
	@Mock
	LoginMapper loginmapper;
	
	@InjectMocks
	AccountUserDetailsService accountUserDetailsService;
	
	@BeforeEach //Mockオブジェクトの初期化
    void setUp() {
    	MockitoAnnotations.openMocks(this);
    	Account account = new Account();
    	account.setUserName("加藤健");
    	account.setPassword("pinballs");
    	account.setRole("ROLE_USER");
    	when(loginmapper.findAccount("example@ezweb.ne.jp")).thenReturn(account);
    	when(loginmapper.findAccount("hogehoge@gmail.com")).thenReturn(null);
    }
	
	@Test
    void loadUserByUsernameでユーザが一人見つかる() throws Exception{
		AccountUserDetails details = (AccountUserDetails)accountUserDetailsService.loadUserByUsername("example@ezweb.ne.jp");
		assertEquals(true,details instanceof AccountUserDetails);
		assertNotNull(details.getAccount());
		assertEquals(details.getUsername(),"加藤健");
		assertEquals(details.getPassword(),"pinballs");
		assertEquals(details.getAuthorities().toString(),"[ROLE_USER]");
		verify(loginmapper,times(1)).findAccount("example@ezweb.ne.jp");
	}
	
	@Test
    void loadUserByUsernameでユーザが見つからない場合に例外を投げる() throws Exception{
		try{
			accountUserDetailsService.loadUserByUsername("hogehoge@gmail.com");
		} catch(UsernameNotFoundException e) {
			assertEquals(e.getMessage(),"User not found.");
		}
		verify(loginmapper,times(1)).findAccount("hogehoge@gmail.com");
	}

}
