package com.dining.boyaki.controller.combined;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.sql.Date;
import java.time.LocalDateTime;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.config.BeanConfig;
import com.dining.boyaki.config.SuccessHandler;
import com.dining.boyaki.controller.DiaryRecordController;
import com.dining.boyaki.model.entity.DiaryRecordCategory;
import com.dining.boyaki.model.form.DiaryRecordForm;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.model.service.DiaryRecordService;
import com.dining.boyaki.util.CsvDataSetLoader;
import com.dining.boyaki.util.WithMockCustomUser;

@AutoConfigureMockMvc
@AutoConfigureMybatis
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class,
	                     WithSecurityContextTestExecutionListener.class})
@WebMvcTest(controllers = DiaryRecordController.class,
			includeFilters = @ComponentScan.Filter
			                (type = FilterType.ASSIGNABLE_TYPE,
			                 value = {AccountUserDetailsService.class,BeanConfig.class,SuccessHandler.class,
					                  DiaryRecordService.class}))
@Transactional
public class DiaryRecordControllerCombinedTest {
	
	private static LocalDateTime datetime;
	
	private static MockedStatic<LocalDateTime> mock;
	
	@Autowired
	private MockMvc mockMvc;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
	}
	
	@AfterEach
    void tearDown() throws Exception {
        mock.close();
	}
	
	@Test
	@WithMockUser(username="マクベイ",authorities= {"ROLE_USER"})
	void showUserCalendarでユーザトップ画面が表示される() throws Exception{
		mockMvc.perform(get("/index"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(view().name("UserCalendar/index"));
	}
	
	@Test
	@WithMockUser(username="マクベイ",authorities= {"ROLE_USER"})
	void showCreateContentで食事投稿記録画面が表示される() throws Exception{
		mockMvc.perform(get("/index/create"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attributeExists("diaryRecordForm"))
		       .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
	           .andExpect(view().name("UserCalendar/Create"));
	}
			
	@Test
	@WithMockCustomUser(userName="加藤健",password="pinballs",role="ROLE_USER")
	@DatabaseSetup(value="/controller/UserCalendar/setup/")
	@ExpectedDatabase(value="/controller/UserCalendar/insert/",table="diary_record")
	void createContentで食事記録が登録される() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		form.setCategoryId(2);
		form.setDiaryDay(Date.valueOf("2022-02-26"));
		form.setRecord1("白米");
		form.setRecord2("生姜焼き");
		form.setRecord3("きのこのマリネ");
		form.setPrice(0);
		form.setMemo(null);
		datetime = LocalDateTime.of(2022, 2, 26, 14, 01, 25);
		mock.when(LocalDateTime::now).thenReturn(datetime);
		
		mockMvc.perform(post("/index/create/insert")
		           	   .flashAttr("diaryRecordForm", form)
		               .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		               .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().is3xxRedirection())
			   .andExpect(model().hasNoErrors())
	           .andExpect(redirectedUrl("/index"));
		}
	
	@Test
	@WithMockCustomUser(userName="加藤健",password="pinballs",role="ROLE_USER")
	@DatabaseSetup(value="/controller/UserCalendar/setup/")
	void createContentでバリデーションエラーが発生する() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		form.setDiaryDay(null);
		form.setRecord1(null);
		form.setRecord2(null);
		form.setRecord3(null);
		
		mockMvc.perform(post("/index/create/insert")
   		       		   .flashAttr("diaryRecordForm", form)
		   		       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		   		       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attributeHasFieldErrors("diaryRecordForm"
		    			   ,"diaryDay","record1","record2","record3"))
		       .andExpect(view().name("UserCalendar/Create"));
	}
	
	@Test
	@WithMockCustomUser(userName="加藤健",password="pinballs",role="ROLE_USER")
	@DatabaseSetup(value="/controller/UserCalendar/setup/")
	void createContentでレコードの重複が発生する() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		form.setCategoryId(2);
		form.setDiaryDay(Date.valueOf("2022-02-11"));
		form.setRecord1("白米");
		form.setRecord2("生姜焼き");
		form.setRecord3("きのこのマリネ");
		form.setPrice(0);
		form.setMemo(null);
		
		mockMvc.perform(post("/index/create/insert")
				       .flashAttr("diaryRecordForm", form)
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
        	   .andExpect(status().is2xxSuccessful())
        	   .andExpect(model().hasNoErrors())
        	   .andExpect(model().attribute("message", "既に同じカテゴリ、同じ日付で登録されています"))
               .andExpect(view().name("UserCalendar/Create"));
	}
	
	@Test
	@WithMockCustomUser(userName="糸井",password="sigeSIGE",role="ROLE_USER")
	@DatabaseSetup(value="/controller/UserCalendar/setup/")
	void showUserEditContentで食事記録編集画面へ遷移する() throws Exception{
		mockMvc.perform(get("/index/record/2022-01-31/3"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("diaryRecordForm"
		     		                       ,hasProperty("createAt",is(LocalDateTime.parse("2022-02-02T10:22:57"))))
		 		          )
		       .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
		       .andExpect(view().name("UserCalendar/Edit"));
		
		mockMvc.perform(get("/index/record/2022-02-23/1"))
			       	   .andExpect(status().is2xxSuccessful())
		       .andExpect(view().name("error/404"));
	}
	
	@Test
	@WithMockCustomUser(userName="糸井",password="sigeSIGE",role="ROLE_USER")
	void showUserEditContentでParseExceptionかNumberFormatExceptionが発生した場合は404ページへ遷移する() throws Exception{
		mockMvc.perform(get("/index/record/2022-ab-cd/1"))
	           .andExpect(status().is2xxSuccessful())
               .andExpect(view().name("error/404"));
		mockMvc.perform(get("/index/record/2022-02-23/Number"))
 		   	   .andExpect(status().is2xxSuccessful())
 		   	   .andExpect(view().name("error/404"));
	}
	
	@Test
	@WithMockCustomUser(userName="糸井",password="sigeSIGE",role="ROLE_USER")
	@DatabaseSetup(value="/controller/UserCalendar/setup/")
	@ExpectedDatabase(value="/controller/UserCalendar/update/",table="diary_record")
	void updateContentで食事記録を更新する() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		form.setCategoryId(3);
		form.setDiaryDay(Date.valueOf("2022-01-31"));
		form.setRecord1("うどん");
		form.setRecord2("唐揚げ");
		form.setRecord3(null);
		form.setPrice(320);
		form.setMemo("冷凍食品");
		form.setCreateAt(LocalDateTime.parse("2022-02-02T10:22:57"));
		datetime = LocalDateTime.of(2022, 2, 02, 16, 23, 33);
		mock.when(LocalDateTime::now).thenReturn(datetime);
		
		mockMvc.perform(post("/index/record/commit")
				       .flashAttr("diaryRecordForm", form)
				       .param("update", "update")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
	           .andExpect(status().is3xxRedirection())
	           .andExpect(model().hasNoErrors())
	           .andExpect(redirectedUrl("/index"));
	}
	
	@Test
	@WithMockCustomUser(userName="糸井",password="sigeSIGE",role="ROLE_USER")
	@DatabaseSetup(value="/controller/UserCalendar/setup/")
	void updateContentでバリデーションエラーが発生する() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		form.setDiaryDay(null);
		form.setRecord1(null);
		form.setRecord2(null);
		form.setRecord3(null);
		
		mockMvc.perform(post("/index/record/commit")
		               .flashAttr("diaryRecordForm", form)
		               .param("update", "update")
		               .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		               .with(SecurityMockMvcRequestPostProcessors.csrf()))
               .andExpect(status().is2xxSuccessful())
	           .andExpect(model().attributeHasFieldErrors("diaryRecordForm"
	        		   ,"diaryDay","record1","record2","record3"))
		       .andExpect(view().name("UserCalendar/Edit"));
	}
	
	@Test
	@WithMockCustomUser(userName="糸井",password="sigeSIGE",role="ROLE_USER")
	@DatabaseSetup(value="/controller/UserCalendar/setup/")
	void updateContentでレコード重複エラーが発生する() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		form.setCategoryId(2);
		form.setDiaryDay(Date.valueOf("2022-01-31"));
		form.setRecord1("うどん");
		form.setRecord2("唐揚げ");
		form.setRecord3(null);
		form.setPrice(320);
		form.setMemo("冷凍食品");
		form.setCreateAt(LocalDateTime.parse("2022-02-02T10:22:57"));
		
		mockMvc.perform(post("/index/record/commit")
                       .flashAttr("diaryRecordForm", form)
                       .param("update", "update")
                       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                       .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().is2xxSuccessful())
			   .andExpect(model().hasNoErrors())
			   .andExpect(model().attribute("message", "既に同じカテゴリ、同じ日付で登録されています"))
			   .andExpect(view().name("UserCalendar/Edit"));
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/UserCalendar/setup/")
	@ExpectedDatabase(value="/controller/UserCalendar/delete/",table="diary_record")
	void deleteContentで食事記録を削除する() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		form.setCategoryId(3);
		form.setDiaryDay(Date.valueOf("2022-01-26"));
		form.setRecord1(null);
		form.setRecord2("チキンステーキ");
		form.setRecord3("余りもの野菜炒め");
		form.setPrice(0);
		form.setMemo(null);
		form.setCreateAt(LocalDateTime.parse("2022-01-26T18:42:15"));
		
		mockMvc.perform(post("/index/record/commit")
		               .flashAttr("diaryRecordForm", form)
		               .param("delete", "delete")
		               .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		               .with(SecurityMockMvcRequestPostProcessors.csrf()))
        	   .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/index"));
	}
}
