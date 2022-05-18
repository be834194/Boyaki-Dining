package com.dining.boyaki.model.mapper;

import java.util.List;
import java.sql.Date;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
		assertEquals(3,records.size());
		assertEquals("加藤健",records.get(0).getUserName());
		assertEquals(Date.valueOf("2022-02-11"),records.get(0).getDiaryDay());
		assertEquals(Date.valueOf("2022-02-11"),records.get(1).getDiaryDay());
		assertEquals(Date.valueOf("2022-02-14"),records.get(2).getDiaryDay());
		assertEquals(1,records.get(0).getCategoryId());
		assertEquals(2,records.get(1).getCategoryId());
		assertEquals(3,records.get(2).getCategoryId());
		
		records = diaryRecordMapper.findAllDiaryRecords("健太郎");
		assertTrue(records.isEmpty());
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/DiaryRecord/setup/")
	void findOneDiaryRecordsで一つの投稿を取得する() throws Exception{
		DiaryRecord records= diaryRecordMapper.findOneDiaryRecord("miho",1,Date.valueOf("2022-01-26"));
		assertEquals("miho",records.getUserName());
		assertEquals(1,records.getCategoryId());
		assertEquals(Date.valueOf("2022-01-26"),records.getDiaryDay());
		assertEquals("グラノーラ",records.getRecord1());
		assertEquals(null,records.getRecord2());
		assertEquals("ヨーグルト",records.getRecord3());
		assertEquals("20220312_172549007.jpg",records.getImageName());
		assertEquals(null,records.getMemo());
		assertEquals(LocalDateTime.parse("2022-01-26T09:55:41"),records.getCreateAt());
		assertEquals(LocalDateTime.parse("2022-01-26T09:55:41"),records.getUpdateAt());
		
		records= diaryRecordMapper.findOneDiaryRecord("miho",1,Date.valueOf("2022-02-26"));
		assertEquals(null,records);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/DiaryRecord/setup/")
	@ExpectedDatabase(value = "/mapper/DiaryRecord/insert/",table="diary_record")
	void insertDiaryRecordsで一つの投稿が追加される() throws Exception{
		DiaryRecord record = new DiaryRecord("加藤健",2,Date.valueOf("2022-02-26"),
				             "白米","生姜焼き","きのこのマリネ","FLeLXKVUUAAgeF0.jpeg",
				             null,LocalDateTime.parse("2022-02-26T14:01:25"),LocalDateTime.parse("2022-02-26T14:01:25"));
		diaryRecordMapper.insertDiaryRecord(record);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/DiaryRecord/setup/")
	@ExpectedDatabase(value = "/mapper/DiaryRecord/update/",table="diary_record")
	void updateDiaryRecordsで一つの投稿が更新される() throws Exception{
		DiaryRecord record = new DiaryRecord("糸井",3,Date.valueOf("2022-01-31"),
				             "うどん","唐揚げ",null,
				             null,"冷凍食品",LocalDateTime.parse("2022-02-02T10:22:57"),LocalDateTime.parse("2022-02-02T16:23:33"));
		diaryRecordMapper.updateDiaryRecord(record);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/DiaryRecord/setup/")
	@ExpectedDatabase(value = "/mapper/DiaryRecord/delete/",table="diary_record")
	void deleteDiaryRecordsで一つの投稿が削除される() throws Exception{
		DiaryRecord record = new DiaryRecord();
		record.setUserName("miho");
		record.setCategoryId(3);
		record.setDiaryDay(Date.valueOf("2022-01-26"));
		diaryRecordMapper.deleteDiaryRecord(record);
	}

}
