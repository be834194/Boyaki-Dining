package com.dining.boyaki.model.mapper;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.AccountInfo;
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
	@ExpectedDatabase(value = "/mapper/AccountInfo/update/",table="account_info")
	void updateAccountInfoでユーザレコードが1件更新される() throws Exception{
		AccountInfo info = new AccountInfo();
		info.setUserName("加藤健");
		info.setNickName("加藤健");
		info.setProfile("間食が止まらない");
		info.setStatus(3);
		info.setGender(1);
		info.setAge(31);
		accountInfoMapper.updateAccountInfo(info);
	}

}
