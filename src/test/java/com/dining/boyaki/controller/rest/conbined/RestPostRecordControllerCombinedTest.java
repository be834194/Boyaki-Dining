package com.dining.boyaki.controller.rest.conbined;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.config.BeanConfig;
import com.dining.boyaki.config.SuccessHandler;
import com.dining.boyaki.controller.rest.RestPostRecordController;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.model.service.PostService;
import com.dining.boyaki.util.CsvDataSetLoader;
import com.dining.boyaki.util.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@AutoConfigureMockMvc
@AutoConfigureMybatis
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class,
	                     WithSecurityContextTestExecutionListener.class})
@WebMvcTest(controllers = RestPostRecordController.class,
            includeFilters = @ComponentScan.Filter
                            (type = FilterType.ASSIGNABLE_TYPE,
                             value = {AccountUserDetailsService.class,BeanConfig.class,SuccessHandler.class,
                            		  PostService.class}))
@Transactional
public class RestPostRecordControllerCombinedTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_Nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/Post/setup/")
	void findPostRecord??????????????????????????????????????????????????????() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		List<String> expect = new ArrayList<String>();
		expect.add("{postId=11, userName=null, nickName=sigeno, status=??????????????????????????????????????????, postCategory=??????????????????, content=???????????????????????????????????????????????????8???6?????????????????????????????????????????????????????????, createAt=2022-03-07 22:17:49}");
		expect.add("{postId=10, userName=null, nickName=sigeno, status=??????????????????????????????????????????, postCategory=???????????????, content=????????????????????????, createAt=2022-03-03 19:32:44}");
		expect.add("{postId=8, userName=null, nickName=sigeno, status=??????????????????????????????????????????, postCategory=??????????????????, content=?????????????????????????????????????????????????????????????????????, createAt=2022-03-02 12:55:08}");
		expect.add("{postId=4, userName=null, nickName=sigeno, status=??????????????????????????????????????????, postCategory=???????????????, content=??????????????????????????????????????????, createAt=2022-03-01 18:07:15}");
		expect.add("{postId=3, userName=null, nickName=sigeno, status=??????????????????????????????????????????, postCategory=????????????????????????????????????, content=????????????????????????????????????????????????????????????????????????, createAt=2022-03-01 12:07:27}");
		String readValue = mockMvc.perform(get("/api/find")
										  .param("nickName", "sigeno")
										  .param("page","0"))
							      .andExpect(status().is2xxSuccessful())
							      .andReturn().getResponse().getContentAsString();
		List<?> result = mapper.readValue(readValue, List.class);
		assertEquals(expect.toString(),result.toString());
		
		expect = new ArrayList<String>();
		expect.add("{postId=2, userName=null, nickName=sigeno, status=??????????????????????????????????????????, postCategory=??????????????????, content=?????????????????????????????????????????????????????????, createAt=2022-03-01 12:06:21}");
		readValue = mockMvc.perform(get("/api/find")
								   .param("nickName", "sigeno")
								   .param("page","1"))
					       .andExpect(status().is2xxSuccessful())
					       .andReturn().getResponse().getContentAsString();
		result = mapper.readValue(readValue, List.class);
		assertEquals(expect.toString(),result.toString());
		
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_Nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/Post/setup/")
	void searchPostRecord???json?????????????????????() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		List<String> expect = new ArrayList<String>();
		expect.add("{postId=11, userName=null, nickName=sigeno, status=??????????????????????????????????????????, postCategory=??????????????????, content=???????????????????????????????????????????????????8???6?????????????????????????????????????????????????????????, createAt=2022-03-07 22:17:49}");
		expect.add("{postId=10, userName=null, nickName=sigeno, status=??????????????????????????????????????????, postCategory=???????????????, content=????????????????????????, createAt=2022-03-03 19:32:44}");
		expect.add("{postId=9, userName=null, nickName=?????????, status=???????????????, postCategory=?????????, content=??????????????????????????????????????????????????????????????????????????????????????????, createAt=2022-03-03 18:41:36}");
		expect.add("{postId=8, userName=null, nickName=sigeno, status=??????????????????????????????????????????, postCategory=??????????????????, content=?????????????????????????????????????????????????????????????????????, createAt=2022-03-02 12:55:08}");
		expect.add("{postId=7, userName=null, nickName=??????, status=??????????????????, postCategory=???????????????, content=??????????????????1??????????????????????????????????????????????????????????????????, createAt=2022-03-02 11:12:50}");
		String readValue = mockMvc.perform(post("/api/search")
										  .param("category","")
										  .param("status","")
										  .param("keyword", "???")
										  .param("page","0")
										  .contentType(MediaType.APPLICATION_JSON_VALUE)
										  .with(SecurityMockMvcRequestPostProcessors.csrf()))
							      .andExpect(status().is2xxSuccessful())
							      .andReturn().getResponse().getContentAsString();
		List<?> result = mapper.readValue(readValue, List.class);
		assertEquals(expect.toString(),result.toString());
		
		expect = new ArrayList<String>();
		expect.add("{postId=1, userName=null, nickName=?????????, status=???????????????, postCategory=???????????????, content=?????????????????????????????????????????????????????????????????????, createAt=2022-02-28 23:30:34}");
		readValue = mockMvc.perform(post("/api/search")
								   .param("category","")
								   .param("status","")
								   .param("keyword", "???")
								   .param("page","2")
								   .contentType(MediaType.APPLICATION_JSON_VALUE)
								   .with(SecurityMockMvcRequestPostProcessors.csrf()))
					       .andExpect(status().is2xxSuccessful())
					       .andReturn().getResponse().getContentAsString();
		result = mapper.readValue(readValue, List.class);
		assertEquals(expect.toString(),result.toString());
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_Nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/Post/setup/")
	void searchPostRecord?????????????????????json???????????????() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		List<String> expect = new ArrayList<String>();
		expect.add("{postId=9, userName=null, nickName=?????????, status=???????????????, postCategory=?????????, content=??????????????????????????????????????????????????????????????????????????????????????????, createAt=2022-03-03 18:41:36}");
		expect.add("{postId=5, userName=null, nickName=??????, status=??????????????????, postCategory=??????, content=????????????????????????????????????????????????????????????????????????????????????, createAt=2022-03-01 18:29:51}");
		String readValue = mockMvc.perform(post("/api/search")
										  .param("category","3","7")
										  .param("status","1","7")
										  .param("keyword", "????????????")
										  .param("page","0")
										  .contentType(MediaType.APPLICATION_JSON_VALUE)
										  .with(SecurityMockMvcRequestPostProcessors.csrf()))
							       .andExpect(status().is2xxSuccessful())
							       .andReturn().getResponse().getContentAsString();
		List<?>result = mapper.readValue(readValue, List.class);
		assertEquals(expect.toString(),result.toString());
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_Nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/Post/setup/")
	void searchCommentRecord????????????????????????????????????json???????????????() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		List<String> expect = new ArrayList<String>();
		expect.add("{nickName=sigeno, status=??????????????????????????????????????????, content=test, createAt=2022-03-10 19:44:28}");
		expect.add("{nickName=sigeno, status=??????????????????????????????????????????, content=test, createAt=2022-03-09 06:44:28}");
		expect.add("{nickName=sigeno, status=??????????????????????????????????????????, content=test, createAt=2022-03-08 22:44:28}");
		expect.add("{nickName=sigeno, status=??????????????????????????????????????????, content=test, createAt=2022-03-08 13:44:28}");
		expect.add("{nickName=?????????, status=???????????????, content=??????????????????, createAt=2022-03-07 09:52:28}");
		String readValue = mockMvc.perform(post("/api/comments")
								  .param("postId","7")
								  .param("page","0")
								  .contentType(MediaType.APPLICATION_JSON_VALUE)
								  .with(SecurityMockMvcRequestPostProcessors.csrf()))
					       .andExpect(status().is2xxSuccessful())
					       .andReturn().getResponse().getContentAsString();
		List<?>result = mapper.readValue(readValue, List.class);
		assertEquals(expect.toString(),result.toString());
		
		expect = new ArrayList<String>();
		expect.add("{nickName=sigeno, status=??????????????????????????????????????????, content=??????????????????, createAt=2022-03-07 09:44:28}");
		readValue = mockMvc.perform(post("/api/comments")
								  .param("postId","7")
								  .param("page","1")
								  .contentType(MediaType.APPLICATION_JSON_VALUE)
								  .with(SecurityMockMvcRequestPostProcessors.csrf()))
					       .andExpect(status().is2xxSuccessful())
					       .andReturn().getResponse().getContentAsString();
		result = mapper.readValue(readValue, List.class);
		assertEquals(expect.toString(),result.toString());
	}

}
