package com.dining.boyaki.model.mapper;

import java.sql.Date;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import com.dining.boyaki.util.CsvDataSetLoader;
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

import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.DiaryRecord;

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
		
		userName = findDataMapper.findUserName("健太郎");
		assertEquals(null,userName);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/FindData/setup/")
	void findUserNameFromMailでユーザを一人見つける() throws Exception{
		String userName = findDataMapper.findUserNameFromMail("miho@gmail.com");
		assertEquals("miho",userName);
		
		userName = findDataMapper.findUserNameFromMail("homi@gmail.com");
		assertEquals(null,userName);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/FindData/setup/")
	void findMailでメールアドレスを一件見つける() throws Exception{
		String mail = findDataMapper.findMail("example@ezweb.ne.jp");
		assertEquals("example@ezweb.ne.jp",mail);
		
		mail = findDataMapper.findMail("test@ezweb.ne.jp");
		assertEquals(null,mail);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/FindData/setup/")
	void findOneDiaryRecordで食事投稿を1件見つける() throws Exception{
		DiaryRecord record = findDataMapper.findOneDiaryRecord("加藤健", 1, Date.valueOf("2022-02-11"));
		assertEquals("加藤健",record.getUserName());
		assertEquals(1,record.getCategoryId());
		assertEquals(Date.valueOf("2022-02-11"),record.getDiaryDay());
		assertEquals("玄米ご飯",record.getRecord1());
		assertEquals("ウインナー",record.getRecord2());
		assertEquals("小松菜のお浸し",record.getRecord3());
		assertEquals(null,record.getImageName());
		assertEquals(null,record.getMemo());
		assertEquals(LocalDateTime.parse("2022-02-11T21:37:19"),record.getCreateAt());
		assertEquals(LocalDateTime.parse("2022-02-11T21:37:19"),record.getUpdateAt());
		
		record = findDataMapper.findOneDiaryRecord("加藤健", 1, Date.valueOf("2022-02-19"));
		assertEquals(null,record);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/FindData/setup/")
	void findNickNameでニックネームを1人見つける() throws Exception{
		AccountInfo userName = findDataMapper.findNickName("sigeno");
		assertEquals("糸井",userName.getUserName());
		assertEquals("sigeno",userName.getNickName());
		
		userName = findDataMapper.findNickName("健太郎");
		assertEquals(null,userName);
	}
}
