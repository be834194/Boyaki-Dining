package com.dining.boyaki.model.service.conbined;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.AccountUserDetails;
import com.dining.boyaki.model.service.AccountUserDetailsService;

@SpringBootTest
@Transactional
public class AccountUserDetailsServiceCombinedTest {
	
	@Autowired
	AccountUserDetailsService accountUserDetailsService;
	
	@Test
    void loadUserByUsernameでユーザが一人見つかる() throws Exception{
		AccountUserDetails details = (AccountUserDetails)accountUserDetailsService.loadUserByUsername("加藤健");
		assertEquals(true,details instanceof AccountUserDetails);
		assertNotNull(details.getAccount());
		assertEquals(details.getUsername(),"加藤健");
		assertEquals(details.getPassword(),"$2a$10$56/AV51uDcWs7qsdHD98U.IdXVkae9CrrvtKbNaj8sJNaRGsvNBqK");
		assertEquals(details.getAuthorities().toString(),"[ROLE_USER]");
	}
	
	@Test
    void loadUserByUsernameでユーザが見つからない場合に例外を投げる() throws Exception{
		try{
			accountUserDetailsService.loadUserByUsername("加藤健");
		} catch(UsernameNotFoundException e) {
			assertEquals(e.getMessage(),"User not found.");
		}
	}

}
