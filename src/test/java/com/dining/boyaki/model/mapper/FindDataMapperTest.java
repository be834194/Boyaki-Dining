package com.dining.boyaki.model.mapper;

import static org.junit.Assert.assertEquals;
import com.dining.boyaki.util.CsvDataSetLoader;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
                         TransactionalTestExecutionListener.class,
                         DbUnitTestExecutionListener.class})
@MybatisTest
@Transactional
public class FindDataMapperTest {
	
	@Autowired
	FindDataMapper findDataMapper;
	
	@Autowired
	SqlSession session;
	
	@AfterEach
	void tearDown() {
		session.flushStatements(); 
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/FindData/setup/")
	void findUserNameでユーザを一人見つける() throws Exception{
		String userName = findDataMapper.findUserName("糸井");
		assertEquals("糸井",userName);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/FindData/setup/")
	void findMailでメールアドレスを一見見つける() throws Exception{
		String mail = findDataMapper.findMail("example@ezweb.ne.jp");
		assertEquals("example@ezweb.ne.jp",mail);
	}
}
