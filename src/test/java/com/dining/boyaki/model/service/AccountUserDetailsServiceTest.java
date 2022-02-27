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
    	when(loginmapper.findAccount("加藤健")).thenReturn(account);
    	when(loginmapper.findAccount("佐藤健")).thenReturn(null);
    }
	
	@Test
    void loadUserByUsernameでユーザが一人見つかる() throws Exception{
		AccountUserDetails details = (AccountUserDetails)accountUserDetailsService.loadUserByUsername("加藤健");
		assertEquals(true,details instanceof AccountUserDetails);
		assertNotNull(details.getAccount());
		assertEquals(details.getUsername(),"加藤健");
		assertEquals(details.getPassword(),"pinballs");
		assertEquals(details.getAuthorities().toString(),"[ROLE_USER]");
		verify(loginmapper,times(1)).findAccount("加藤健");
	}
	
	@Test
    void loadUserByUsernameでユーザが見つからない場合に例外を投げる() throws Exception{
		try{
			accountUserDetailsService.loadUserByUsername("佐藤健");
		} catch(UsernameNotFoundException e) {
			assertEquals(e.getMessage(),"User not found.");
		}
		verify(loginmapper,times(1)).findAccount("佐藤健");
	}

}
