package com.dining.boyaki.model.mapper;

import java.time.LocalDateTime;

import org.apache.ibatis.session.SqlSession;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

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
public class UpdatePasswordMapperTest {
	
	@Autowired
	UpdatePasswordMapper updatePasswordMapper;
	
	@Autowired
	SqlSession session;
	
	@AfterEach
	void tearDown() {
		session.flushStatements();
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/UpdatePassword/setup/")
	@ExpectedDatabase(value = "/mapper/UpdatePassword/update/account/",table="account")
	void updateMemberでユーザレコードが1件追加される() throws Exception{
		Account account = new Account();
		account.setPassword("script-Java");
		account.setMail("miho@gmail.com");
		updatePasswordMapper.updatePassword(account);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/UpdatePassword/setup/")
	@ExpectedDatabase(value = "/mapper/UpdatePassword/update/password_history/",table="password_history")
	void insertPasswordHistoryでPW履歴レコードが1件追加される() throws Exception{
		PasswordHistory history = new PasswordHistory();
		history.setUserName("miho");
		history.setPassword("script-Java");
		history.setUseDay(LocalDateTime.parse("2022-02-10T20:39:45"));
		updatePasswordMapper.insertPasswordHistory(history);
	}

}
