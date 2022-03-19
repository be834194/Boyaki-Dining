package com.dining.boyaki.model.mapper;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
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
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
                         TransactionalTestExecutionListener.class,
                         DbUnitTestExecutionListener.class})
@MybatisTest
@Transactional
public class AccountInfoMapperTest {
	
	@Autowired
	AccountInfoMapper accountInfoMapper;
	
	@Autowired
	SqlSession session;
	
	@AfterEach
	void tearDown() {
		session.flushStatements();
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/AccountInfo/setup/")
	void findAccountInfoでユーザ情報レコードを1件取得する() throws Exception{
		AccountInfo info = accountInfoMapper.findAccountInfo("糸井");
		assertEquals("糸井",info.getUserName());
		assertEquals("sigeno",info.getNickName());
		assertEquals("今年中に体重5キロ落としたい",info.getProfile());
		assertEquals(3,info.getStatus());
		assertEquals(3,info.getGender());
		assertEquals(2,info.getAge());
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/AccountInfo/setup/")
	@ExpectedDatabase(value = "/mapper/AccountInfo/update/account_info/")
	void updateAccountInfoでユーザレコードが1件更新される() throws Exception{
		AccountInfo info = new AccountInfo();
		info.setUserName("加藤健");
		info.setNickName("kenken");
		info.setProfile("間食が止まらない");
		info.setStatus(3);
		info.setGender(1);
		info.setAge(3);
		accountInfoMapper.updateAccountInfo(info);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/AccountInfo/setup/")
	@ExpectedDatabase(value = "/mapper/AccountInfo/update/account/",table="account")
	void updatePasswordでパスワードが1件更新される() throws Exception{
		Account account = new Account();
		account.setUserName("miho");
		account.setPassword("script-Java");
		accountInfoMapper.updatePassword(account);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/AccountInfo/setup/")
	@ExpectedDatabase(value = "/mapper/AccountInfo/insert/",table="password_history")
	void insertPasswordHistoryでPW履歴レコードが1件追加される() throws Exception{
		PasswordHistory history = new PasswordHistory();
		history.setUserName("miho");
		history.setPassword("script-Java");
		history.setUseDay(LocalDateTime.parse("2022-02-10T20:39:45"));
		accountInfoMapper.insertPasswordHistory(history);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/AccountInfo/setup/")
	@ExpectedDatabase(value = "/mapper/AccountInfo/delete/")
	void deleteAccountでアカウントが削除される() throws Exception{
		accountInfoMapper.deleteAccount("加藤健");
	}
	
}