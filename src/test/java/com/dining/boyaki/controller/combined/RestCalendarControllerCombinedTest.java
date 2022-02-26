package com.dining.boyaki.controller.combined;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.util.CsvDataSetLoader;
import com.dining.boyaki.util.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class,
	                     WithSecurityContextTestExecutionListener.class})
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class RestCalendarControllerCombinedTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_Nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/RestCalendar/setup/")
	void getCalendarRecordでFullCalendarに表示させるjsonを取得する() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		List<String> expect = new ArrayList<String>();
		expect.add("{title=<a href=\"/index/record/2022-01-26/1\">朝食:グラノーラ</a>, start=2022-01-26, end=2022-01-26}");
		expect.add("{title=<a href=\"/index/record/2022-01-26/2\">昼食:グラタン</a>, start=2022-01-26, end=2022-01-26}");
		expect.add("{title=<a href=\"/index/record/2022-01-26/3\">夕食:チキンステーキ</a>, start=2022-01-26, end=2022-01-26}");
		expect.add("{title=<a href=\"/index/record/2022-02-15/4\">飲酒-間食-運動</a>, start=2022-02-15, end=2022-02-15}");
		
		String readValue = mockMvc.perform(get("/api/events/all"))
			                      .andExpect(status().is2xxSuccessful())
			                      .andReturn().getResponse().getContentAsString();
		List<?> result = mapper.readValue(readValue, List.class);
		assertEquals(expect.toString(),result.toString());
	}

}
