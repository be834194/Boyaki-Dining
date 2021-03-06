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
	void findAllCalendarRecords???????????????DiaryRecord???CalendarRecord??????????????????() throws Exception{
		List<CalendarRecord> calendarRecords = diaryRecordService.findAllCalendarRecords("miho");
		assertEquals("<a href=\"/index/record/2022-01-26/1\">??????:???????????????</a>",calendarRecords.get(0).getTitle());
		assertEquals("2022-01-26",calendarRecords.get(0).getStart());
		assertEquals("2022-01-26",calendarRecords.get(0).getEnd());
		assertEquals("<a href=\"/index/record/2022-01-26/2\">??????:????????????</a>",calendarRecords.get(1).getTitle());
		assertEquals("2022-01-26",calendarRecords.get(1).getStart());
		assertEquals("2022-01-26",calendarRecords.get(1).getEnd());
		assertEquals("<a href=\"/index/record/2022-01-26/3\">??????:?????????????????????</a>",calendarRecords.get(2).getTitle());
		assertEquals("2022-01-26",calendarRecords.get(2).getStart());
		assertEquals("2022-01-26",calendarRecords.get(2).getEnd());
		assertEquals("<a href=\"/index/record/2022-02-15/4\">??????-??????-??????</a>",calendarRecords.get(3).getTitle());
		assertEquals("2022-02-15",calendarRecords.get(3).getStart());
		assertEquals("2022-02-15",calendarRecords.get(3).getEnd());	
	}
	
	@Test
	@DatabaseSetup(value = "/service/DiaryRecord/setup/")
	void findOneDiaryRecord???DiaryRecord???1???????????????() throws Exception{
		DiaryRecordForm result = diaryRecordService.findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-15"));
		assertEquals(4,result.getCategoryId());
		assertEquals(Date.valueOf("2022-02-15"),result.getDiaryDay());
		assertEquals(null,result.getRecord1());
		assertEquals("?????????????????????",result.getRecord2());
		assertEquals("???????????????15?????3?????????",result.getRecord3());
		assertEquals(null,result.getImageName());
		assertNull(result.getMemo());
		assertEquals(LocalDateTime.parse("2022-02-15T23:30:34"),result.getCreateAt());
		
		result = diaryRecordService.findOneDiaryRecord("?????????", 4, Date.valueOf("2022-02-01"));
		assertNull(result);
	}
	
	@Test
	@DatabaseSetup(value = "/service/DiaryRecord/setup/")
	@ExpectedDatabase(value = "/service/DiaryRecord/insert/",table="diary_record")
	void insertDiaryRecord???DiaryRecord???1???????????????() throws Exception{
		datetime = LocalDateTime.parse("2022-02-26T14:01:25");
		mock.when(LocalDateTime::now).thenReturn(datetime);
		DiaryRecordForm form = new DiaryRecordForm("?????????",2,Date.valueOf("2022-02-26"),
				                                   "??????","????????????","?????????????????????",
				                                   "FLeLXKVUUAAgeF0.jpeg",null,datetime);

		diaryRecordService.insertDiaryRecord(form);
	}
	
	@Test
	@DatabaseSetup(value = "/service/DiaryRecord/setup/")
	@ExpectedDatabase(value = "/service/DiaryRecord/update/",table="diary_record")
	void updateDiaryRecord???DiaryRecord???1???????????????() throws Exception{
		datetime = LocalDateTime.parse("2022-02-02T16:23:33");
		mock.when(LocalDateTime::now).thenReturn(datetime);
		DiaryRecordForm form = new DiaryRecordForm("??????",3,Date.valueOf("2022-01-31"),
				                                   "?????????","?????????",null,
				                                   null,"????????????",LocalDateTime.parse("2022-02-02T10:22:57"));
		
		diaryRecordService.updateDiaryRecord(form);
	}
	
	@Test
	@DatabaseSetup(value = "/service/DiaryRecord/setup/")
	@ExpectedDatabase(value = "/service/DiaryRecord/delete/",table="diary_record")
	void deleteDiaryRecord???DiaryRecord???1???????????????() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		form.setUserName("miho");
		form.setCategoryId(3);
		form.setDiaryDay(Date.valueOf("2022-01-26"));
		diaryRecordService.deleteDiaryRecord(form);
	}

}
