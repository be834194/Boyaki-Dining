package com.dining.boyaki.model.mapper;

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
import com.dining.boyaki.util.CsvDataSetLoader;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
                         TransactionalTestExecutionListener.class,
                         DbUnitTestExecutionListener.class})
@MybatisTest
@Transactional
public class LoginMapperTest {
	
	@Autowired
	LoginMapper loginMapper;
	
	@Autowired
	SqlSession session;
	
	@AfterEach
	void tearDown() {
		session.flushStatements(); 
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Login/setup/")
	void findAccountでユーザを一人見つける() throws Exception{
		Account existAccount = loginMapper.findAccount("加藤健");
		assertEquals("加藤健",existAccount.getUserName());
		assertEquals("pinballs",existAccount.getPassword());
		assertEquals("ROLE_USER",existAccount.getRole());
		
		existAccount = loginMapper.findAccount("マクベイ");
		assertEquals(null,existAccount);
	}

}
