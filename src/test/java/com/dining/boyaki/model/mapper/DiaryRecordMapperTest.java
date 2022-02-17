package com.dining.boyaki.model.mapper;

import java.util.List;
import java.sql.Date;
import static org.junit.Assert.assertEquals;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
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

import com.dining.boyaki.model.entity.DiaryRecord;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
                         TransactionalTestExecutionListener.class,
                         DbUnitTestExecutionListener.class})
@MybatisTest
@Transactional
public class DiaryRecordMapperTest {
	
	@Autowired
	DiaryRecordMapper diaryRecordMapper;
	
	@Autowired
	SqlSession session;
	
	@AfterEach
	void tearDown() {
		session.flushStatements();
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/DiaryRecord/setup/")
	void findAllDiaryRecordsでユーザ一人の投稿を全て取得する() throws Exception{
		List<DiaryRecord> records = diaryRecordMapper.findAllDiaryRecords("加藤健");
		assertEquals(records.size(),3);
		assertEquals(records.get(0).getUserName(),"加藤健");
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/DiaryRecord/setup/")
	void findOneDiaryRecordsで一つの投稿を取得する() throws Exception{
		DiaryRecord records= diaryRecordMapper.findOneDiaryRecord("miho",1,Date.valueOf("2021-12-26"));
		assertEquals(records.getUserName(),"miho");
		assertEquals(records.getCategoryId(),1);
		assertEquals(records.getDiaryday(),Date.valueOf("2021-12-26"));
		assertEquals(records.getRecord1(),"グラノーラ");
		assertEquals(records.getRecord2(),null);
		assertEquals(records.getRecord3(),"ヨーグルト");
		assertEquals(records.getPrice(),0);
		assertEquals(records.getMemo(),null);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/DiaryRecord/setup/")
	@ExpectedDatabase(value = "/mapper/DiaryRecord/insert/",table="diary_record")
	void insertDiaryRecordsで一つの投稿が追加される() throws Exception{
		DiaryRecord record = new DiaryRecord();
		record.setUserName("加藤健");
		record.setCategoryId(2);
		record.setDiaryDay(Date.valueOf("2022-02-26"));
		record.setRecord1("白米");
		record.setRecord2("生姜焼き");
		record.setRecord3("きのこのマリネ");
		record.setPrice(0);
		record.setMemo(null);
		diaryRecordMapper.insertDiaryRecord(record);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/DiaryRecord/setup/")
	@ExpectedDatabase(value = "/mapper/DiaryRecord/update/",table="diary_record")
	void updateDiaryRecordsで一つの投稿が更新される() throws Exception{
		DiaryRecord record = new DiaryRecord();
		record.setUserName("糸井");
		record.setCategoryId(3);
		record.setDiaryDay(Date.valueOf("2022-01-31"));
		record.setRecord1("うどん");
		record.setRecord2("唐揚げ");
		record.setRecord3(null);
		record.setPrice(320);
		record.setMemo("冷凍食品");
		diaryRecordMapper.updateDiaryRecord(record);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/DiaryRecord/setup/")
	@ExpectedDatabase(value = "/mapper/DiaryRecord/delete/",table="diary_record")
	void deleteDiaryRecordsで一つの投稿が削除される() throws Exception{
		DiaryRecord record = new DiaryRecord();
		record.setUserName("miho");
		record.setCategoryId(3);
		record.setDiaryDay(Date.valueOf("2021-12-26"));
		diaryRecordMapper.deleteDiaryRecord(record);
	}

}
