package com.dining.boyaki.model.service.conbined;

import java.sql.Date;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.CalendarRecord;
import com.dining.boyaki.model.form.DiaryRecordForm;
import com.dining.boyaki.model.service.DiaryRecordService;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class DiaryRecordServiceCombinedTest {
	
	@Autowired
	DiaryRecordService diaryRecordService;
	
	@Test
	@DatabaseSetup(value = "/service/DiaryRecord/setup/")
	void findAllCalendarRecordsで取得したDiaryRecordをCalendarRecordに詰め替える() throws Exception{
		List<CalendarRecord> calendarRecords = diaryRecordService.findAllCalendarRecords("miho");
		assertEquals("<a href=\"/index/record/2022-01-26/1\">朝食:グラノーラ</a>",calendarRecords.get(0).getTitle());
		assertEquals("2022-01-26",calendarRecords.get(0).getStart());
		assertEquals("2022-01-26",calendarRecords.get(0).getEnd());
		assertEquals("<a href=\"/index/record/2022-01-26/2\">昼食:グラタン</a>",calendarRecords.get(1).getTitle());
		assertEquals("2022-01-26",calendarRecords.get(1).getStart());
		assertEquals("2022-01-26",calendarRecords.get(1).getEnd());
		assertEquals("<a href=\"/index/record/2022-01-26/3\">夕食:チキンステーキ</a>",calendarRecords.get(2).getTitle());
		assertEquals("2022-01-26",calendarRecords.get(2).getStart());
		assertEquals("2022-01-26",calendarRecords.get(2).getEnd());
		assertEquals("<a href=\"/index/record/2022-02-15/4\">飲酒ー間食ー運動</a>",calendarRecords.get(3).getTitle());
		assertEquals("2022-02-15",calendarRecords.get(3).getStart());
		assertEquals("2022-02-15",calendarRecords.get(3).getEnd());	
	}
	
	@Test
	@DatabaseSetup(value = "/service/DiaryRecord/setup/")
	void findOneDiaryRecordでDiaryRecordを1件取得する() throws Exception{
		DiaryRecordForm result = diaryRecordService.findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-15"));
		assertEquals(4,result.getCategoryId());
		assertEquals(Date.valueOf("2022-02-15"),result.getDiaryDay());
		assertEquals(null,result.getRecord1());
		assertEquals("ポテトチップス",result.getRecord2());
		assertEquals("腕立て伏せ15回×3セット",result.getRecord3());
		assertEquals(0,result.getPrice());
		assertNull(result.getMemo());
	}
	
	@Test
	@DatabaseSetup(value = "/service/DiaryRecord/setup/")
	void findOneDiaryRecordでDiaryRecordが取得できない場合nullが返ってくる() throws Exception{
		DiaryRecordForm result = diaryRecordService.findOneDiaryRecord("加藤健", 4, Date.valueOf("2022-02-01"));
		assertNull(result);
	}
	
	@Test
	@DatabaseSetup(value = "/service/DiaryRecord/setup/")
	@ExpectedDatabase(value = "/service/DiaryRecord/insert/",table="diary_record")
	void insertDiaryRecordでDiaryRecordを1件追加する() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		form.setUserName("加藤健");
		form.setCategoryId(2);
		form.setDiaryDay(Date.valueOf("2022-02-26"));
		form.setRecord1("白米");
		form.setRecord2("生姜焼き");
		form.setRecord3("きのこのマリネ");
		form.setPrice(0);
		form.setMemo(null);
		diaryRecordService.insertDiaryRecord(form);
	}
	
	@Test
	@DatabaseSetup(value = "/service/DiaryRecord/setup/")
	@ExpectedDatabase(value = "/service/DiaryRecord/update/",table="diary_record")
	void updateDiaryRecordでDiaryRecordを1件更新する() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		form.setUserName("糸井");
		form.setCategoryId(3);
		form.setDiaryDay(Date.valueOf("2022-01-31"));
		form.setRecord1("うどん");
		form.setRecord2("唐揚げ");
		form.setRecord3(null);
		form.setPrice(320);
		form.setMemo("冷凍食品");
		diaryRecordService.updateDiaryRecord(form);
	}
	
	@Test
	@DatabaseSetup(value = "/service/DiaryRecord/setup/")
	@ExpectedDatabase(value = "/service/DiaryRecord/delete/",table="diary_record")
	void deleteDiaryRecordでDiaryRecordを1件削除する() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		form.setUserName("miho");
		form.setCategoryId(3);
		form.setDiaryDay(Date.valueOf("2022-01-26"));
		form.setRecord1(null);
		form.setRecord2("チキンステーキ");
		form.setRecord3("余りもの野菜炒め");
		form.setPrice(0);
		form.setMemo(null);
		diaryRecordService.deleteDiaryRecord(form);
	}

}
