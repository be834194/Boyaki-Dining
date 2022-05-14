package com.dining.boyaki.model.service.conbined;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.AccountUserDetails;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.util.CsvDataSetLoader;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class AccountUserDetailsServiceCombinedTest {
	
	@Autowired
	AccountUserDetailsService accountUserDetailsService;
	
	@Test
	@DatabaseSetup(value="/service/AccountUserDetails/setup/")
    void loadUserByUsernameでユーザが一人見つかる() throws Exception{
		AccountUserDetails details = (AccountUserDetails)accountUserDetailsService.loadUserByUsername("example@ezweb.ne.jp");
		assertEquals(true,details instanceof AccountUserDetails);
		assertNotNull(details.getAccount());
		assertEquals("加藤健",details.getUsername());
		assertEquals("pinballs",details.getPassword());
		assertEquals("[ROLE_USER]",details.getAuthorities().toString());
	}
	
	@Test
	@DatabaseSetup(value="/service/AccountUserDetails/setup/")
    void loadUserByUsernameでユーザが見つからない場合に例外を投げる() throws Exception{
		try{
			accountUserDetailsService.loadUserByUsername("hogehoge@gmail.com");
		} catch(UsernameNotFoundException e) {
			assertEquals(e.getMessage(),"User not found.");
		}
	}

}
