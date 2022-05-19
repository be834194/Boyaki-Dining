package com.dining.boyaki.model.service.conbined;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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
	
	private static LocalDateTime datetime;
	
	private static MockedStatic<LocalDateTime> mock;
	
	@Autowired
	DiaryRecordService diaryRecordService;
	
	@BeforeEach
    void setUp() {
		mock = Mockito.mockStatic(LocalDateTime.class,Mockito.CALLS_REAL_METHODS);
	}
	
	@AfterEach
    void tearDown() throws Exception {
        mock.close();
	}
	
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
		assertEquals("<a href=\"/index/record/2022-02-15/4\">飲酒-間食-運動</a>",calendarRecords.get(3).getTitle());
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
		assertEquals(null,result.getImageName());
		assertNull(result.getMemo());
		assertEquals(LocalDateTime.parse("2022-02-15T23:30:34"),result.getCreateAt());
		
		result = diaryRecordService.findOneDiaryRecord("加藤健", 4, Date.valueOf("2022-02-01"));
		assertNull(result);
	}
	
	@Test
	@DatabaseSetup(value = "/service/DiaryRecord/setup/")
	@ExpectedDatabase(value = "/service/DiaryRecord/insert/",table="diary_record")
	void insertDiaryRecordでDiaryRecordを1件追加する() throws Exception{
		datetime = LocalDateTime.parse("2022-02-26T14:01:25");
		mock.when(LocalDateTime::now).thenReturn(datetime);
		DiaryRecordForm form = new DiaryRecordForm("加藤健",2,Date.valueOf("2022-02-26"),
				                                   "白米","生姜焼き","きのこのマリネ",
				                                   "FLeLXKVUUAAgeF0.jpeg",null,datetime);

		diaryRecordService.insertDiaryRecord(form);
	}
	
	@Test
	@DatabaseSetup(value = "/service/DiaryRecord/setup/")
	@ExpectedDatabase(value = "/service/DiaryRecord/update/",table="diary_record")
	void updateDiaryRecordでDiaryRecordを1件更新する() throws Exception{
		datetime = LocalDateTime.parse("2022-02-02T16:23:33");
		mock.when(LocalDateTime::now).thenReturn(datetime);
		DiaryRecordForm form = new DiaryRecordForm("糸井",3,Date.valueOf("2022-01-31"),
				                                   "うどん","唐揚げ",null,
				                                   null,"冷凍食品",LocalDateTime.parse("2022-02-02T10:22:57"));
		
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
		diaryRecordService.deleteDiaryRecord(form);
	}

}
