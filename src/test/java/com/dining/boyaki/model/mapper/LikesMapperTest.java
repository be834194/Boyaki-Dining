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
public class LikesMapperTest {
	
	@Autowired
	LikesMapper likesMapper;
	
	@Autowired
	SqlSession session;
	
	@AfterEach
	void tearDown() {
		session.flushStatements();
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Post/setup/")
	void currentRateで評価状態を取得する() throws Exception{
		int result =  likesMapper.currentRate(2, "miho").orElse(-1);
		assertEquals(1,result);
		
		result =  likesMapper.currentRate(10, "miho").orElse(-1);
		assertEquals(-1,result);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Post/setup/")
	void sumRateで評価状態を取得する() throws Exception{
		int result =  likesMapper.sumRate(2).orElse(0);
		assertEquals(2,result);
		
		result =  likesMapper.sumRate(5).orElse(0);
		assertEquals(0,result);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Post/setup/")
	@ExpectedDatabase(value = "/mapper/Post/insert/likes/",table="likes")
	void insertRateで評価状態を追加する() throws Exception{
		likesMapper.insertRate(3, "miho", 1);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Post/setup/")
	@ExpectedDatabase(value = "/mapper/Post/update/",table="likes")
	void updateRateで評価状態を更新する() throws Exception{
		likesMapper.updateRate(1, "miho", 1);
	}

}
