package com.dining.boyaki.model.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
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
import com.dining.boyaki.model.mapper.DiaryRecordMapper;

@RunWith(SpringRunner.class)
@Transactional
public class DiaryRecordServiceTest {
	
	@Mock
	DiaryRecordMapper diaryRecordMapper;
	
	@InjectMocks
	DiaryRecordService diaryRecordService;
	
	@BeforeEach //Mockオブジェクトの初期化
    void setUp() {
    	MockitoAnnotations.openMocks(this);
    }
	
	@Test
	void returnTitleでRecord2を返す() throws Exception{
		DiaryRecord diary = new DiaryRecord();
		diary.setRecord1("白米");
		diary.setRecord2("生姜焼き");
		String title = diaryRecordService.returnTitle(diary);
		assertEquals("生姜焼き",title);
	}
	
	@Test
	void returnTitleでRecord1を返す() throws Exception{
		DiaryRecord diary = new DiaryRecord();
		diary.setRecord1("白米");
		String title = diaryRecordService.returnTitle(diary);
		assertEquals("白米",title);
	}
	
	@Test
	void returnTitleでRecord3を返す() throws Exception{
		DiaryRecord diary = new DiaryRecord();
		diary.setRecord3("きゅうりの浅漬け");
		String title = diaryRecordService.returnTitle(diary);
		assertEquals("きゅうりの浅漬け",title);
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
		assertEquals("<a href=\"/index/record/2022-02-18/4\">飲酒ー間食ー運動</a>",calendarRecords.get(3).getTitle());
		assertEquals("2022-02-18",calendarRecords.get(3).getStart());
		assertEquals("2022-02-18",calendarRecords.get(3).getEnd());
		verify(diaryRecordMapper,times(1)).findAllDiaryRecords("加藤健");
	}
	
	@Test
	void findOneDiaryRecordでDiaryRecordを1件取得する() throws Exception{
		DiaryRecord diary = new DiaryRecord();
		diary.setUserName("miho");
		diary.setCategoryId(1);
		diary.setDiaryDay(Date.valueOf("2022-02-15"));
		diary.setRecord1("食パン2枚");
		diary.setRecord2("目玉焼き");
		diary.setRecord3(null);
		diary.setPrice(0);
		diary.setMemo(null);
		when(diaryRecordMapper.findOneDiaryRecord("miho", 0, Date.valueOf("2022-02-15")))
		   .thenReturn(diary);
		
		DiaryRecord result = diaryRecordService.findOneDiaryRecord("miho", 0, Date.valueOf("2022-02-15"));
		assertEquals("miho",result.getUserName());
		assertEquals(1,result.getCategoryId());
		assertEquals("2022-02-15",result.getDiaryDay().toString());
		assertEquals("食パン2枚",result.getRecord1());
		assertEquals("目玉焼き",result.getRecord2());
		assertNull(result.getRecord3());
		assertEquals(0,result.getPrice());
		assertNull(result.getMemo());
		verify(diaryRecordMapper,times(1)).findOneDiaryRecord("miho", 0, Date.valueOf("2022-02-15"));
	}
	
	@Test
	void insertDiaryRecordでDiaryRecordを1件追加する() throws Exception{
		DiaryRecord diary = new DiaryRecord();
		diary.setUserName("糸井");
		diary.setCategoryId(2);
		diary.setDiaryDay(Date.valueOf("2022-02-14"));
		diary.setRecord1("ラーメン");
		diary.setRecord2(null);
		diary.setRecord3(null);
		diary.setPrice(460);
		diary.setMemo("外食");
		doNothing().when(diaryRecordMapper).insertDiaryRecord(diary);
		
		diaryRecordService.insertDiaryRecord(diary);
		verify(diaryRecordMapper,times(1)).insertDiaryRecord(diary);
	}
	
	@Test
	void updateDiaryRecordでDiaryRecordを1件追加する() throws Exception{
		DiaryRecord diary = new DiaryRecord();
		diary.setUserName("加藤健");
		diary.setCategoryId(1);
		diary.setDiaryDay(Date.valueOf("2022-02-16"));
		diary.setRecord1("チョコパン");
		diary.setRecord2("");
		diary.setRecord3("昨日のサラダ");
		diary.setPrice(100);
		diary.setMemo("コンビニのコーヒー");
		doNothing().when(diaryRecordMapper).updateDiaryRecord(diary);
		
		diaryRecordService.updateDiaryRecord(diary);
		verify(diaryRecordMapper,times(1)).updateDiaryRecord(diary);
	}
	
	@Test
	void deleteDiaryRecordでDiaryRecordを1件削除する() throws Exception{
		DiaryRecord diary = new DiaryRecord();
		diary.setUserName("miho");
		diary.setCategoryId(1);
		diary.setDiaryDay(Date.valueOf("2022-02-15"));
		diary.setRecord1("食パン2枚");
		diary.setRecord2("目玉焼き");
		diary.setRecord3(null);
		diary.setPrice(0);
		diary.setMemo(null);
		doNothing().when(diaryRecordMapper).deleteDiaryRecord(diary);
		
		diaryRecordService.deleteDiaryRecord(diary);
		verify(diaryRecordMapper,times(1)).deleteDiaryRecord(diary);
	}
	
}
