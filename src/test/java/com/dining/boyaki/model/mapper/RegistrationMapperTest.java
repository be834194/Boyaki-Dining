package com.dining.boyaki.model.mapper;

import java.time.LocalDateTime;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

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
import com.dining.boyaki.model.entity.AccountInfo;
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
	@ExpectedDatabase(value = "/mapper/Registration/insert/account/",table="account")
	void insertMemberでユーザレコードが1件追加される() throws Exception{
		Account account = new Account("マクベイ","sun-flan-sis","north-east@gmail.com","ROLE_USER");
		registrationMapper.insertAccount(account);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Registration/setup/")
	@ExpectedDatabase(value = "/mapper/Registration/insert/password_history/",table="password_history")
	void insertPasswordHistoryでPW履歴レコードが1件追加される() throws Exception{
		PasswordHistory history = new PasswordHistory("糸井","sigeSATO",LocalDateTime.parse("2022-02-08T11:00:52"));
		registrationMapper.insertPasswordHistory(history);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Registration/setup/")
	@ExpectedDatabase(value = "/mapper/Registration/insert/account_info/",table="account_info")
	void insertAccountInfoでユーザ情報レコードが1件追加される() throws Exception{
		AccountInfo info = new AccountInfo("加藤健","加藤健","間食が止まらない",3,1,3,165,60,
				                           LocalDateTime.parse("2022-02-01T12:19:33"),
				                           LocalDateTime.parse("2022-02-01T12:19:33"));
		registrationMapper.insertAccountInfo(info);
	}
	
}