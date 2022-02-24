package com.dining.boyaki.controller;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.dining.boyaki.model.entity.AccountUserDetails;
import com.dining.boyaki.model.entity.CalendarRecord;
import com.dining.boyaki.model.service.DiaryRecordService;

@RestController
@RequestMapping("/api/events")
public class RestCalendarController {
	
	private final DiaryRecordService diaryRecordService;
	
	public RestCalendarController(DiaryRecordService diaryRecordService) {
		this.diaryRecordService = diaryRecordService;
	}
	
	@GetMapping(value = "/all")
    public String getEvents(@AuthenticationPrincipal AccountUserDetails details) {
        String jsonMsg = null;
        List<CalendarRecord> records = diaryRecordService.findAllCalendarRecords(details.getUsername());
        ObjectMapper mapper = new ObjectMapper();
        try {
        	jsonMsg =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(records);
        } catch(JsonProcessingException e){
        	e.printStackTrace();
        }
        return jsonMsg;
	}

}
