package com.dining.boyaki.model.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.time.LocalDateTime;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
                         TransactionalTestExecutionListener.class,
                         DbUnitTestExecutionListener.class})
@MybatisTest
@Transactional
public class PasswordHistoryMapperTest {
	
	@Autowired
	PasswordHistoryMapper passwordHistoryMapper;
	
	@Autowired
	SqlSession session;
	
	@AfterEach
	void tearDown() {
		session.flushStatements(); 
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/PasswordHistory/setup/")
	void findPasswordでパスワードを取得する() throws Exception{
		String password = passwordHistoryMapper.findPassword("miho","miho@gmail.com");
		assertEquals("ocean-Nu",password);
		
		password = passwordHistoryMapper.findPassword("miho","homi@gmail.com");
		assertEquals(null,password);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/PasswordHistory/setup/")
	void findUseFromでユーザ一人のPW更新履歴を降順で取得する() throws Exception{
		List<PasswordHistory> history = passwordHistoryMapper.findUseFrom("加藤健", LocalDateTime.of(2021,12,13,12,33,00));
		assertEquals(history.size(),2);
		assertEquals(history.get(0).getUserName(),"加藤健");
		assertEquals(history.get(0).getUseDay(),LocalDateTime.parse("2022-01-13T09:08:56"));
		assertEquals(history.get(1).getUseDay(),LocalDateTime.parse("2021-12-14T12:08:28"));
		
		history = passwordHistoryMapper.findUseFrom("加藤健", LocalDateTime.of(2022,02,13,12,33,00));
		assertTrue(history.isEmpty());
	}

}
