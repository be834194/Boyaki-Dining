package com.dining.boyaki.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.dining.boyaki.config.BeanConfig;
import com.dining.boyaki.config.SuccessHandler;
import com.dining.boyaki.model.entity.CalendarRecord;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.model.service.DiaryRecordService;
import com.dining.boyaki.util.WithMockCustomUser;

@AutoConfigureMockMvc
@AutoConfigureMybatis
@WebMvcTest(controllers = RestCalendarController.class,
            includeFilters = @ComponentScan.Filter
                            (type = FilterType.ASSIGNABLE_TYPE,
                             value = {AccountUserDetailsService.class,BeanConfig.class,
            		                  SuccessHandler.class}))
public class RestCalendarControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@MockBean
	DiaryRecordService diaryRecordService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
				                 .apply(springSecurity()).build();
	}
	
	@Test
	@WithMockCustomUser(userName="マクベイ",password="sun_flan_sis",role="ROLE_USER")
	void getCalendarRecordでFullCalendarに表示させるjsonを取得する() throws Exception{
		List<CalendarRecord> records = new ArrayList<CalendarRecord>();
		CalendarRecord record = new CalendarRecord();
		record.setTitle("<a href=\"/index/record/2022-02-26/1\">朝食:ハムエッグ</a>");
		record.setStart("2022-02-26");
		record.setEnd("2022-02-26");
		records.add(record);
		record = new CalendarRecord();
		record.setTitle("<a href=\"/index/record/2022-02-27/4\">飲酒-間食-運動</a>");
		record.setStart("2022-02-27");
		record.setEnd("2022-02-27");
		records.add(record);
		
		ObjectMapper mapper = new ObjectMapper();
		List<String> expect = new ArrayList<String>();
		expect.add("{title=<a href=\"/index/record/2022-02-26/1\">朝食:ハムエッグ</a>, start=2022-02-26, end=2022-02-26}");
		expect.add("{title=<a href=\"/index/record/2022-02-27/4\">飲酒-間食-運動</a>, start=2022-02-27, end=2022-02-27}");
		when(diaryRecordService.findAllCalendarRecords("マクベイ")).thenReturn(records);
		
		String readValue = mockMvc.perform(get("/api/events/all"))
							      .andExpect(status().is2xxSuccessful())
							      .andReturn().getResponse().getContentAsString();
		List<?> result = mapper.readValue(readValue, List.class);
		assertEquals(expect.toString(),result.toString());
	}
	
}
