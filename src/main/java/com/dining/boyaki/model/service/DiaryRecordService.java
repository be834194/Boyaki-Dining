package com.dining.boyaki.model.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.CalendarRecord;
import com.dining.boyaki.model.entity.DiaryRecord;
import com.dining.boyaki.model.mapper.DiaryRecordMapper;

@Service
public class DiaryRecordService {
	
	private final DiaryRecordMapper diaryRecordMapper;
	
	public DiaryRecordService(DiaryRecordMapper diaryRecordMapper) {
		this.diaryRecordMapper = diaryRecordMapper;
	}
	
	@Transactional(readOnly = true)
	public List<CalendarRecord> findAllCalendarRecords(String userName) {
		List<DiaryRecord> diaryRecords = diaryRecordMapper.findAllDiaryRecords(userName);
		List<CalendarRecord> calendarRecords = new ArrayList<CalendarRecord>();
		for(int i = 0; i < diaryRecords.size(); i++) {
			DiaryRecord diary = diaryRecords.get(i);
			CalendarRecord calendar = new CalendarRecord();
			calendar.setStart(diary.getDiaryDay().toString());
			calendar.setEnd(diary.getDiaryDay().toString());
			
			switch(diary.getCategoryId()) {
			case 1:
				calendar.setTitle
				("<a href=\"/index/record/" + calendar.getStart() + "/1\">" 
				 +"朝食:"+ returnTitle(diary) + "</a>");
				break;
			case 2:
				calendar.setTitle
				("<a href=\"/index/record/" + calendar.getStart() + "/2\">" 
				 +"昼食:"+ returnTitle(diary) + "</a>");
				break;
			case 3:
				calendar.setTitle
				("<a href=\"/index/record/" + calendar.getStart() + "/3\">" 
				 +"夕食:"+ returnTitle(diary) + "</a>");
				break;
			case 4:
				calendar.setTitle
				("<a href=\"/index/record/" + calendar.getStart() + "/4\">" 
				 +"飲酒ー間食ー運動" + "</a>");
				break;
			}
			calendarRecords.add(calendar);
		}
		return calendarRecords;
	}
	
    public String returnTitle(DiaryRecord diary) {
    	if(diary.getRecord2() != null) {
    		return diary.getRecord2();
    	}else if(diary.getRecord1() != null) {
    		return diary.getRecord1();
    	} else {
    		return diary.getRecord3();
    	}
    }
    
    @Transactional(readOnly = true)
    public DiaryRecord findOneDiaryRecord(String userName,int categoryId,Date diaryDay) {
    	return diaryRecordMapper.findOneDiaryRecord(userName, categoryId, diaryDay);
    }
    
    @Transactional(readOnly = false)
    public void insertDiaryRecord(DiaryRecord diary) {
    	diaryRecordMapper.insertDiaryRecord(diary);
    }
    
    @Transactional(readOnly = false)
    public void updateDiaryRecord(DiaryRecord diary) {
    	diaryRecordMapper.updateDiaryRecord(diary);
    }
    
    @Transactional(readOnly = false)
    public void deleteDiaryRecord(DiaryRecord diary) {
    	diaryRecordMapper.deleteDiaryRecord(diary);
    }

}
