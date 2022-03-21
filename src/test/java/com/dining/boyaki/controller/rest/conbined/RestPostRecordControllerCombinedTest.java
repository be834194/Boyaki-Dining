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
	void findPostRecordでユーザ一人の投稿情報を全件取得する() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		List<String> expect = new ArrayList<String>();
		expect.add("{nickName=sigeno, status=中性脂肪・コレステロール高め, postCategory=グチ・ぼやき, content=ラジオで聞いた話ですが、睡眠時間が8→6時間に減ると毛穴が二倍に広がるそうです, createAt=2022-03-07 22:17:49}");
		expect.add("{nickName=sigeno, status=中性脂肪・コレステロール高め, postCategory=ダイエット, content=ノンアル飽きた！, createAt=2022-03-03 19:32:44}");
		expect.add("{nickName=sigeno, status=中性脂肪・コレステロール高め, postCategory=グチ・ぼやき, content=全然お腹周りが改善されなくてダイエットめげそう, createAt=2022-03-02 12:55:08}");
		expect.add("{nickName=sigeno, status=中性脂肪・コレステロール高め, postCategory=ダイエット, content=在宅だと毎日の料理が大変。。, createAt=2022-03-01 18:07:15}");
		expect.add("{nickName=sigeno, status=中性脂肪・コレステロール高め, postCategory=中性脂肪・コレステロール, content=ドーナツは穴が開いてるからゼロカロリーって本当？, createAt=2022-03-01 12:07:27}");
		String readValue = mockMvc.perform(get("/api/find")
										  .param("nickName", "sigeno")
										  .param("page","0"))
							      .andExpect(status().is2xxSuccessful())
							      .andReturn().getResponse().getContentAsString();
		List<?> result = mapper.readValue(readValue, List.class);
		assertEquals(expect.toString(),result.toString());
		
		expect = new ArrayList<String>();
		expect.add("{nickName=sigeno, status=中性脂肪・コレステロール高め, postCategory=グチ・ぼやき, content=最近アカウントをつくりました、よろしく, createAt=2022-03-01 12:06:21}");
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
	void searchPostRecordでjsonを全件取得する() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		List<String> expect = new ArrayList<String>();
		expect.add("{nickName=sigeno, status=中性脂肪・コレステロール高め, postCategory=グチ・ぼやき, content=ラジオで聞いた話ですが、睡眠時間が8→6時間に減ると毛穴が二倍に広がるそうです, createAt=2022-03-07 22:17:49}");
		expect.add("{nickName=sigeno, status=中性脂肪・コレステロール高め, postCategory=ダイエット, content=ノンアル飽きた！, createAt=2022-03-03 19:32:44}");
		expect.add("{nickName=加藤健, status=尿酸値高め, postCategory=尿酸値, content=血液検査で尿酸値がやっと下がってきました！やっぱ牛乳のお陰？, createAt=2022-03-03 18:41:36}");
		expect.add("{nickName=sigeno, status=中性脂肪・コレステロール高め, postCategory=グチ・ぼやき, content=全然お腹周りが改善されなくてダイエットめげそう, createAt=2022-03-02 12:55:08}");
		expect.add("{nickName=匿名, status=ダイエット中, postCategory=ダイエット, content=先月から体重1キロ落ち増した！今月もダイエット頑張るぞ！！, createAt=2022-03-02 11:12:50}");
		String readValue = mockMvc.perform(post("/api/search")
										  .param("category","")
										  .param("status","")
										  .param("keyword", "　")
										  .param("page","0")
										  .contentType(MediaType.APPLICATION_JSON_VALUE)
										  .with(SecurityMockMvcRequestPostProcessors.csrf()))
							      .andExpect(status().is2xxSuccessful())
							      .andReturn().getResponse().getContentAsString();
		List<?> result = mapper.readValue(readValue, List.class);
		assertEquals(expect.toString(),result.toString());
		
		expect = new ArrayList<String>();
		expect.add("{nickName=加藤健, status=尿酸値高め, postCategory=ダイエット, content=サイゼリヤのサラダがダイエットに効果あるらしい, createAt=2022-02-28 23:30:34}");
		readValue = mockMvc.perform(post("/api/search")
								   .param("category","")
								   .param("status","")
								   .param("keyword", "　")
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
	void searchPostRecordで条件を絞ってjsonを取得する() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		List<String> expect = new ArrayList<String>();
		expect.add("{nickName=加藤健, status=尿酸値高め, postCategory=尿酸値, content=血液検査で尿酸値がやっと下がってきました！やっぱ牛乳のお陰？, createAt=2022-03-03 18:41:36}");
		expect.add("{nickName=匿名, status=ダイエット中, postCategory=塩分, content=検査で高血圧気味と言われた母のためにレシピを模索中・・・, createAt=2022-03-01 18:29:51}");
		String readValue = mockMvc.perform(post("/api/search")
										  .param("category","3","7")
										  .param("status","1","7")
										  .param("keyword", "検査　と")
										  .param("page","0")
										  .contentType(MediaType.APPLICATION_JSON_VALUE)
										  .with(SecurityMockMvcRequestPostProcessors.csrf()))
							       .andExpect(status().is2xxSuccessful())
							       .andReturn().getResponse().getContentAsString();
		List<?>result = mapper.readValue(readValue, List.class);
		assertEquals(expect.toString(),result.toString());
	}

}
