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
	
	DiaryRecord diary;
	DiaryRecordForm form = new DiaryRecordForm();
	
	@BeforeEach
    void setUp() {
    	MockitoAnnotations.openMocks(this);
    	diary = new DiaryRecord("miho",1,Date.valueOf("2022-02-15"),
    			                "食パン2枚","目玉焼き",null,
    			                "20220215.jpg",null,LocalDateTime.parse("2022-02-15T11:22:33"),null);
    	form = new DiaryRecordForm("mih0",1,Date.valueOf("2022-02-15"),
    			                   "食パン2枚","目玉焼き",null,
                                   "20220215.jpg",null,LocalDateTime.parse("2022-02-15T11:22:33"));
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
		DiaryRecord diary = new DiaryRecord("加藤健",1,Date.valueOf("2022-02-16"),
				                             null,null,"昨日のサラダ",null,null,null,null);
		diaryRecords.add(diary);
		diary = new DiaryRecord("加藤健",2,Date.valueOf("2022-02-17"),
				                "白米","生姜焼き","きゅうりの浅漬け",null,null,null,null);
		diaryRecords.add(diary);
		diary = new DiaryRecord("加藤健",3,Date.valueOf("2022-02-18"),
				                "牛丼",null,"生野菜のサラダ",null,null,null,null);
		diaryRecords.add(diary);
		diary = new DiaryRecord("加藤健",4,Date.valueOf("2022-02-18"),
				                "ビール一缶","肉まん","自宅からお花茶屋まで徒歩で往復30分",null,null,null,null);
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
		when(diaryRecordMapper.findOneDiaryRecord("miho", 0, Date.valueOf("2022-02-15"))).thenReturn(diary);
		DiaryRecordForm result = diaryRecordService.findOneDiaryRecord("miho", 0, Date.valueOf("2022-02-15"));
		assertEquals(1,result.getCategoryId());
		assertEquals("2022-02-15",result.getDiaryDay().toString());
		assertEquals("食パン2枚",result.getRecord1());
		assertEquals("目玉焼き",result.getRecord2());
		assertNull(result.getRecord3());
		assertEquals("20220215.jpg",result.getImageName());
		assertNull(result.getMemo());
		assertEquals(LocalDateTime.parse("2022-02-15T11:22:33"),result.getCreateAt());
		verify(diaryRecordMapper,times(1)).findOneDiaryRecord("miho", 0, Date.valueOf("2022-02-15"));
		
		when(diaryRecordMapper.findOneDiaryRecord("加藤健", 4, Date.valueOf("2022-02-01"))).thenReturn(null);
		result = diaryRecordService.findOneDiaryRecord("加藤健", 4, Date.valueOf("2022-02-01"));
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
