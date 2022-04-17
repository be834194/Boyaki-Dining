package com.dining.boyaki.model.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.CalendarRecord;
import com.dining.boyaki.model.entity.DiaryRecord;
import com.dining.boyaki.model.form.DiaryRecordForm;
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
		
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
		for(int i = 0; i < diaryRecords.size(); i++) {
			DiaryRecord diary = diaryRecords.get(i);
			CalendarRecord calendar = new CalendarRecord();
			calendar.setStart(simpleDate.format(diary.getDiaryDay()));
			calendar.setEnd(simpleDate.format(diary.getDiaryDay()));
			
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
				 +"飲酒-間食-運動" + "</a>");
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
    public DiaryRecordForm findOneDiaryRecord(String userName,int categoryId,Date diaryDay) {
    	DiaryRecord diary = diaryRecordMapper.findOneDiaryRecord(userName, categoryId, diaryDay);
    	if(Objects.isNull(diary)) {
    		return null;
    	}
    	DiaryRecordForm form = new DiaryRecordForm(diary.getUserName(),diary.getCategoryId(),diary.getDiaryDay(),
    			                                   diary.getRecord1(),diary.getRecord2(),diary.getRecord3(),
    			                                   diary.getPrice(),diary.getMemo(),diary.getCreateAt());
    	return form;
    	
    }
    
    @Transactional(readOnly = false)
    public void insertDiaryRecord(DiaryRecordForm form) {
    	DiaryRecord diary = new DiaryRecord(form.getUserName(),form.getCategoryId(),form.getDiaryDay(),
                                            form.getRecord1(),form.getRecord2(),form.getRecord3(),
                                            form.getPrice(),form.getMemo(),form.getCreateAt(),form.getCreateAt());
    	diaryRecordMapper.insertDiaryRecord(diary);
    }
    
    @Transactional(readOnly = false)
    public void updateDiaryRecord(DiaryRecordForm form) {
    	DiaryRecord diary = new DiaryRecord(form.getUserName(),form.getCategoryId(),form.getDiaryDay(),
    			                            form.getRecord1(),form.getRecord2(),form.getRecord3(),
    			                            form.getPrice(),form.getMemo(),form.getCreateAt(),LocalDateTime.now());
    	diaryRecordMapper.updateDiaryRecord(diary);
    }
    
    @Transactional(readOnly = false)
    public void deleteDiaryRecord(DiaryRecordForm form) {
    	DiaryRecord diary = new DiaryRecord(form.getUserName(),form.getCategoryId(),form.getDiaryDay(),
                                            form.getRecord1(),form.getRecord2(),form.getRecord3(),
                                            form.getPrice(),form.getMemo(),form.getCreateAt(),LocalDateTime.now());
    	diaryRecordMapper.deleteDiaryRecord(diary);
    }

}
