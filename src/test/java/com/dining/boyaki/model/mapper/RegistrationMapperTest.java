package com.dining.boyaki.model.mapper;

import java.time.LocalDateTime;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

import static org.junit.Assert.assertEquals;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
                         TransactionalTestExecutionListener.class,
                         DbUnitTestExecutionListener.class})
@MybatisTest
@Transactional
public class RegistrationMapperTest {
	
	@Autowired
	RegistrationMapper registrationMapper;
	
	@Autowired
	SqlSession session;
	
	@AfterEach
	void tearDown() {
		session.flushStatements(); 
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Registration/setup/")
	void findUserNameでユーザを一人見つける() throws Exception{
		String userName = registrationMapper.findUserName("糸井");
		assertEquals(userName,"糸井");
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Registration/setup/")
	@ExpectedDatabase(value = "/mapper/Registration/insert/account/",table="account")
	void insertMemberでユーザレコードが1件追加される() throws Exception{
		Account account = new Account();
		account.setUserName("マクベイ");
		account.setPassword("sun-flan-sis");
		account.setMail("north-east@gmail.com");
		account.setRole("ROLE_USER");
		registrationMapper.insertAccount(account);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Registration/setup/")
	@ExpectedDatabase(value = "/mapper/Registration/insert/password_history/",table="password_history")
	void insertPasswordHistoryでPW履歴レコードが1件追加される() throws Exception{
		PasswordHistory history = new PasswordHistory();
		history.setUserName("マクベイ");
		history.setPassword("sun-flan-sis");
		history.setUseDay(LocalDateTime.parse("2022-02-08T11:00:52"));
		registrationMapper.insertPasswordHistory(history);
	}
	
}