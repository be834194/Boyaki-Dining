package com.dining.boyaki.model.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.CalendarRecord;
import com.dining.boyaki.model.entity.DiaryRecord;
import com.dining.boyaki.model.form.DiaryRecordForm;
import com.dining.boyaki.model.mapper.DiaryRecordMapper;

@RunWith(SpringRunner.class)
@Transactional
public class DiaryRecordServiceTest {
	
	@Mock
	DiaryRecordMapper diaryRecordMapper;
	
	@InjectMocks
	DiaryRecordService diaryRecordService;
	
	DiaryRecord diary = new DiaryRecord();
	DiaryRecordForm form = new DiaryRecordForm();
	
	@BeforeEach
    void setUp() {
    	MockitoAnnotations.openMocks(this);
    	
    	diary.setUserName("miho");
		diary.setCategoryId(1);
		diary.setDiaryDay(Date.valueOf("2022-02-15"));
		diary.setRecord1("食パン2枚");
		diary.setRecord2("目玉焼き");
		diary.setRecord3(null);
		diary.setPrice(0);
		diary.setMemo(null);
		diary.setCreateAt(LocalDateTime.parse("2022-02-15T11:22:33"));
		form.setCategoryId(1);
		form.setDiaryDay(Date.valueOf("2022-02-15"));
		form.setRecord1("食パン2枚");
		form.setRecord2("目玉焼き");
		form.setRecord3(null);
		form.setPrice(0);
		form.setMemo(null);
		form.setCreateAt(LocalDateTime.parse("2022-02-15T11:22:33"));
    }
	
	@Test
    void setToDiaryRecordでDiaryRecordFormをDiaryRecordに詰め替える() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		form.setUserName("糸井");
		form.setCategoryId(3);
		form.setDiaryDay(Date.valueOf("2022-02-04"));
		form.setRecord1("冷麺");
		form.setRecord2("焼肉");
		form.setRecord3("もやしナムル");
		form.setPrice(2980);
		form.setMemo("焼肉屋で外食");
		form.setCreateAt(LocalDateTime.parse("2022-02-04T12:54:27"));
		
		DiaryRecord record = diaryRecordService.setToDiaryRecord(form);
		assertEquals("糸井",record.getUserName());
		assertEquals(3,record.getCategoryId());
		assertEquals(Date.valueOf("2022-02-04"),record.getDiaryDay());
		assertEquals("冷麺",record.getRecord1());
		assertEquals("焼肉",record.getRecord2());
		assertEquals("もやしナムル",record.getRecord3());
		assertEquals(2980,record.getPrice());
		assertEquals("焼肉屋で外食",record.getMemo());
		assertEquals(LocalDateTime.parse("2022-02-04T12:54:27"),record.getCreateAt());
	}
	
	@Test
    void setToDiaryRecordFormでDiaryRecordをDiaryRecordFormに詰め替える() throws Exception{
		DiaryRecord record = new DiaryRecord();
		record.setUserName("糸井");
		record.setCategoryId(3);
		record.setDiaryDay(Date.valueOf("2022-02-04"));
		record.setRecord1("冷麺");
		record.setRecord2("焼肉");
		record.setRecord3("もやしナムル");
		record.setPrice(2980);
		record.setMemo("焼肉屋で外食");
		record.setCreateAt(LocalDateTime.parse("2022-02-04T12:54:27"));
		
		DiaryRecordForm form = diaryRecordService.setToDiaryRecordForm(record);
		assertEquals("糸井",form.getUserName());
		assertEquals(3,form.getCategoryId());
		assertEquals(Date.valueOf("2022-02-04"),form.getDiaryDay());
		assertEquals("冷麺",form.getRecord1());
		assertEquals("焼肉",form.getRecord2());
		assertEquals("もやしナムル",form.getRecord3());
		assertEquals(2980,form.getPrice());
		assertEquals("焼肉屋で外食",form.getMemo());
		assertEquals(LocalDateTime.parse("2022-02-04T12:54:27"),form.getCreateAt());
	}
	
	@Test
	void returnTitleで対応するRecordを返す() throws Exception{
		DiaryRecord diary = new DiaryRecord();
		diary.setRecord1("白米");
		diary.setRecord2("生姜焼き");
		String title = diaryRecordService.returnTitle(diary);
		assertEquals("生姜焼き",title); //record2
		
		diary = new DiaryRecord();
		diary.setRecord1("白米");
		title = diaryRecordService.returnTitle(diary);
		assertEquals("白米",title); //record1
		
		diary = new DiaryRecord();
		diary.setRecord3("きゅうりの浅漬け");
		title = diaryRecordService.returnTitle(diary);
		assertEquals("きゅうりの浅漬け",title); //record3
	}
	
	@Test
	void findAllCalendarRecordsで取得したDiaryRecordをCalendarRecordに詰め替える() throws Exception{
		List<DiaryRecord> diaryRecords = new ArrayList<DiaryRecord>();
		DiaryRecord diary = new DiaryRecord();
		diary.setUserName("加藤健");
		diary.setCategoryId(1);
		diary.setDiaryDay(Date.valueOf("2022-02-16"));
		diary.setRecord1(null);
		diary.setRecord2(null);
		diary.setRecord3("昨日のサラダ");
		diaryRecords.add(diary);
		diary = new DiaryRecord();
		diary.setUserName("加藤健");
		diary.setCategoryId(2);
		diary.setDiaryDay(Date.valueOf("2022-02-17"));
		diary.setRecord1("白米");
		diary.setRecord2("生姜焼き");
		diary.setRecord3("きゅうりの浅漬け");
		diaryRecords.add(diary);
		diary = new DiaryRecord();
		diary.setUserName("加藤健");
		diary.setCategoryId(3);
		diary.setDiaryDay(Date.valueOf("2022-02-18"));
		diary.setRecord1("牛丼");
		diary.setRecord2(null);
		diary.setRecord3("生野菜のサラダ");
		diaryRecords.add(diary);
		diary = new DiaryRecord();
		diary.setUserName("加藤健");
		diary.setCategoryId(4);
		diary.setDiaryDay(Date.valueOf("2022-02-18"));
		diary.setRecord1("ビール一缶");
		diary.setRecord2("肉まん");
		diary.setRecord3("自宅からお花茶屋まで徒歩で往復30分");
		diaryRecords.add(diary);
		when(diaryRecordMapper.findAllDiaryRecords("加藤健")).thenReturn(diaryRecords);
		
		List<CalendarRecord> calendarRecords = diaryRecordService.findAllCalendarRecords("加藤健");
		assertEquals("<a href=\"/index/record/2022-02-16/1\">朝食:昨日のサラダ</a>",calendarRecords.get(0).getTitle());
		assertEquals("2022-02-16",calendarRecords.get(0).getStart());
		assertEquals("2022-02-16",calendarRecords.get(0).getEnd());
		assertEquals("<a href=\"/index/record/2022-02-17/2\">昼食:生姜焼き</a>",calendarRecords.get(1).getTitle());
		assertEquals("2022-02-17",calendarRecords.get(1).getStart());
		assertEquals("2022-02-17",calendarRecords.get(1).getEnd());
		assertEquals("<a href=\"/index/record/2022-02-18/3\">夕食:牛丼</a>",calendarRecords.get(2).getTitle());
		assertEquals("2022-02-18",calendarRecords.get(2).getStart());
		assertEquals("2022-02-18",calendarRecords.get(2).getEnd());
		assertEquals("<a href=\"/index/record/2022-02-18/4\">飲酒-間食-運動</a>",calendarRecords.get(3).getTitle());
		assertEquals("2022-02-18",calendarRecords.get(3).getStart());
		assertEquals("2022-02-18",calendarRecords.get(3).getEnd());
		verify(diaryRecordMapper,times(1)).findAllDiaryRecords("加藤健");
	}
	
	@Test
	void findOneDiaryRecordでDiaryRecordを1件取得する() throws Exception{
		when(diaryRecordMapper.findOneDiaryRecord("miho", 0, Date.valueOf("2022-02-15")))
		   .thenReturn(diary);
		
		DiaryRecordForm result = diaryRecordService.findOneDiaryRecord("miho", 0, Date.valueOf("2022-02-15"));
		assertEquals(1,result.getCategoryId());
		assertEquals("2022-02-15",result.getDiaryDay().toString());
		assertEquals("食パン2枚",result.getRecord1());
		assertEquals("目玉焼き",result.getRecord2());
		assertNull(result.getRecord3());
		assertEquals(0,result.getPrice());
		assertNull(result.getMemo());
		assertEquals(LocalDateTime.parse("2022-02-15T11:22:33"),result.getCreateAt());
		verify(diaryRecordMapper,times(1)).findOneDiaryRecord("miho", 0, Date.valueOf("2022-02-15"));
	}
	
	@Test
	void findOneDiaryRecordでDiaryRecordが取得できない場合nullが返ってくる() throws Exception{
		when(diaryRecordMapper.findOneDiaryRecord("加藤健", 4, Date.valueOf("2022-02-01")))
		   .thenReturn(null);
		
		DiaryRecordForm result = diaryRecordService.findOneDiaryRecord("加藤健", 4, Date.valueOf("2022-02-01"));
		assertNull(result);
		verify(diaryRecordMapper,times(1)).findOneDiaryRecord("加藤健", 4, Date.valueOf("2022-02-01"));
	}
	
	@Test
	void insertDiaryRecordでDiaryRecordを1件追加する() throws Exception{
		doNothing().when(diaryRecordMapper).insertDiaryRecord(any(DiaryRecord.class));
		
		diaryRecordService.insertDiaryRecord(form);
		verify(diaryRecordMapper,times(1)).insertDiaryRecord(any(DiaryRecord.class));
	}
	
	@Test
	void updateDiaryRecordでDiaryRecordを1件更新する() throws Exception{
		doNothing().when(diaryRecordMapper).updateDiaryRecord(any(DiaryRecord.class));
		
		diaryRecordService.updateDiaryRecord(form);
		verify(diaryRecordMapper,times(1)).updateDiaryRecord(any(DiaryRecord.class));
	}
	
	@Test
	void deleteDiaryRecordでDiaryRecordを1件削除する() throws Exception{
		doNothing().when(diaryRecordMapper).deleteDiaryRecord(any(DiaryRecord.class));
		
		diaryRecordService.deleteDiaryRecord(form);
		verify(diaryRecordMapper,times(1)).deleteDiaryRecord(any(DiaryRecord.class));
	}
	
}
