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
import com.github.springtestdbunit.annotation.DatabaseSetup;

@SpringBootTest
@Transactional
public class AccountUserDetailsServiceCombinedTest {
	
	@Autowired
	AccountUserDetailsService accountUserDetailsService;
	
	@Test
	@DatabaseSetup(value="/service/AccountUserDetails/setup/")
    void loadUserByUsernameでユーザが一人見つかる() throws Exception{
		AccountUserDetails details = (AccountUserDetails)accountUserDetailsService.loadUserByUsername("加藤健");
		assertEquals(true,details instanceof AccountUserDetails);
		assertNotNull(details.getAccount());
		assertEquals("加藤健",details.getUsername());
		assertEquals("$2a$10$56/AV51uDcWs7qsdHD98U.IdXVkae9CrrvtKbNaj8sJNaRGsvNBqK",details.getPassword());
		assertEquals("[ROLE_USER]",details.getAuthorities().toString());
	}
	
	@Test
	@DatabaseSetup(value="/service/AccountUserDetails/setup/")
    void loadUserByUsernameでユーザが見つからない場合に例外を投げる() throws Exception{
		try{
			accountUserDetailsService.loadUserByUsername("加藤健");
		} catch(UsernameNotFoundException e) {
			assertEquals(e.getMessage(),"User not found.");
		}
	}

}
