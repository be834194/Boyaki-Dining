package com.dining.boyaki.controller.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.dining.boyaki.config.BeanConfig;
import com.dining.boyaki.config.SuccessHandler;
import com.dining.boyaki.model.entity.CommentRecord;
import com.dining.boyaki.model.entity.PostRecord;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.model.service.PostService;
import com.dining.boyaki.util.WithMockCustomUser;

@AutoConfigureMockMvc
@AutoConfigureMybatis
@WebMvcTest(controllers = RestPostRecordController.class,
            includeFilters = @ComponentScan.Filter
                            (type = FilterType.ASSIGNABLE_TYPE,
                             value = {AccountUserDetailsService.class,BeanConfig.class,
            		                  SuccessHandler.class}))
public class RestPostRecordControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@MockBean
	PostService postService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
				                 .apply(springSecurity()).build();
	}
	
	@Test
	@WithMockCustomUser(userName="マクベイ",password="sun_flan_sis",role="ROLE_USER")
	void findPostRecordでユーザ一人の投稿情報を全件取得する() throws Exception{
		List<PostRecord> records = new ArrayList<PostRecord>();
		PostRecord record = new PostRecord();
		record.setPostId("1");
		record.setNickName("kenken");
		record.setContent("今日は疲れた");
		record.setStatus("尿酸値高め");
		record.setPostCategory("グチ・ぼやき");
		record.setCreateAt("2022-03-18 21:18:39");
		records.add(record);
		record = new PostRecord();
		record.setPostId("2");
		record.setNickName("kenken");
		record.setContent("朝にコンビニまで散歩、夕方に隣の駅まで散歩して非常に疲れた。我ながら体力がない！");
		record.setStatus("尿酸値高め");
		record.setPostCategory("運動・筋トレ");
		record.setCreateAt("2022-03-18 22:55:10");
		records.add(record);
		
		ObjectMapper mapper = new ObjectMapper();
		List<String> expect = new ArrayList<String>();
		expect.add("{postId=1, userName=null, nickName=kenken, status=尿酸値高め, postCategory=グチ・ぼやき, content=今日は疲れた, createAt=2022-03-18 21:18:39}");
		expect.add("{postId=2, userName=null, nickName=kenken, status=尿酸値高め, postCategory=運動・筋トレ, content=朝にコンビニまで散歩、夕方に隣の駅まで散歩して非常に疲れた。我ながら体力がない！, createAt=2022-03-18 22:55:10}");
		when(postService.findPostRecord("kenken", 0)).thenReturn(records);
		
		String readValue = mockMvc.perform(get("/api/find")
										  .param("nickName", "kenken")
										  .param("page","0"))
							      .andExpect(status().is2xxSuccessful())
							      .andReturn().getResponse().getContentAsString();
		List<?> result = mapper.readValue(readValue, List.class);
		assertEquals(expect.toString(),result.toString());
	}
	
	@Test
	@WithMockCustomUser(userName="マクベイ",password="sun_flan_sis",role="ROLE_USER")
	void searchPostRecordでjsonを取得する() throws Exception{
		List<PostRecord> records = new ArrayList<PostRecord>();
		PostRecord record = new PostRecord();
		record.setPostId("1");
		record.setNickName("kenken");
		record.setContent("今日は疲れた");
		record.setStatus("尿酸値高め");
		record.setPostCategory("グチ・ぼやき");
		record.setCreateAt("2022-03-18 21:18:39");
		records.add(record);
		record = new PostRecord();
		record.setPostId("2");
		record.setNickName("匿名");
		record.setContent("帰りの電車は一駅前に降りて、家まで歩く！少しでも運動したい");
		record.setStatus("ダイエット中");
		record.setPostCategory("運動・筋トレ");
		record.setCreateAt("2022-03-18 22:55:10");
		records.add(record);
		
		ObjectMapper mapper = new ObjectMapper();
		List<String> expect = new ArrayList<String>();
		expect.add("{postId=1, userName=null, nickName=kenken, status=尿酸値高め, postCategory=グチ・ぼやき, content=今日は疲れた, createAt=2022-03-18 21:18:39}");
		expect.add("{postId=2, userName=null, nickName=匿名, status=ダイエット中, postCategory=運動・筋トレ, content=帰りの電車は一駅前に降りて、家まで歩く！少しでも運動したい, createAt=2022-03-18 22:55:10}");
		when(postService.searchPostRecord(
				any(int[].class), any(int[].class), any(String.class), any(int.class))).thenReturn(records);
		
		String readValue = mockMvc.perform(post("/api/search")
										  .param("category", "")
										  .param("status", "")
										  .param("keyword", "")
										  .param("page","0")
										  .contentType(MediaType.APPLICATION_JSON_VALUE)
										  .with(SecurityMockMvcRequestPostProcessors.csrf()))
							      .andExpect(status().is2xxSuccessful())
							      .andReturn().getResponse().getContentAsString();
		List<?> result = mapper.readValue(readValue, List.class);
		assertEquals(expect.toString(),result.toString());
		verify(postService,times(1)).searchPostRecord(any(int[].class), any(int[].class), any(String.class), any(int.class));
	}
	
	@Test
	@WithMockCustomUser(userName="マクベイ",password="sun_flan_sis",role="ROLE_USER")
	void searchPostRecordで条件を絞ってjsonを取得する() throws Exception{
		List<PostRecord> records = new ArrayList<PostRecord>();
		PostRecord record = new PostRecord();
		record.setPostId("2");
		record.setNickName("kenken");
		record.setContent("今日は疲れた");
		record.setStatus("尿酸値高め");
		record.setPostCategory("グチ・ぼやき");
		record.setCreateAt("2022-03-18 21:18:39");
		records.add(record);
		
		ObjectMapper mapper = new ObjectMapper();
		List<String> expect = new ArrayList<String>();
		expect.add("{postId=2, userName=null, nickName=kenken, status=尿酸値高め, postCategory=グチ・ぼやき, content=今日は疲れた, createAt=2022-03-18 21:18:39}");
		when(postService.searchPostRecord(
				any(int[].class), any(int[].class), any(String.class), any(int.class))).thenReturn(records);
		
		String readValue = mockMvc.perform(post("/api/search")
										  .param("category", "0")
										  .param("status", "7")
										  .param("keyword", "疲れ")
										  .param("page","0")
										  .contentType(MediaType.APPLICATION_JSON_VALUE)
										  .with(SecurityMockMvcRequestPostProcessors.csrf()))
							      .andExpect(status().is2xxSuccessful())
							      .andReturn().getResponse().getContentAsString();
		
		List<?> result = mapper.readValue(readValue, List.class);
		assertEquals(expect.toString(),result.toString());
		verify(postService,times(1)).searchPostRecord(any(int[].class), any(int[].class), any(String.class), any(int.class));
	}
	
	@Test
	@WithMockCustomUser(userName="マクベイ",password="sun_flan_sis",role="ROLE_USER")
	void searchCommentRecordで投稿に対するコメントをjsonを取得する() throws Exception{
		List<CommentRecord> records = new ArrayList<CommentRecord>();
		CommentRecord record = new CommentRecord("sigeno","中性脂肪・コレステロール高め","test","2022-03-08 13:44:28");
		records.add(record);
		record = new CommentRecord("加藤健","尿酸値高め","応援してます","2022-03-07 09:52:28");
		records.add(record);
		when(postService.findCommentList(7, 0)).thenReturn(records);
		
		ObjectMapper mapper = new ObjectMapper();
		List<String> expect = new ArrayList<String>();
		expect.add("{nickName=sigeno, status=中性脂肪・コレステロール高め, content=test, createAt=2022-03-08 13:44:28}");
		expect.add("{nickName=加藤健, status=尿酸値高め, content=応援してます, createAt=2022-03-07 09:52:28}");
		String readValue = mockMvc.perform(post("/api/comments")
								  .param("postId","7")
								  .param("page","0")
								  .contentType(MediaType.APPLICATION_JSON_VALUE)
								  .with(SecurityMockMvcRequestPostProcessors.csrf()))
					       .andExpect(status().is2xxSuccessful())
					       .andReturn().getResponse().getContentAsString();
		List<?>result = mapper.readValue(readValue, List.class);
		assertEquals(expect.toString(),result.toString());
		verify(postService,times(1)).findCommentList(7, 0);
	}
	

}
