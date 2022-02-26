package com.dining.boyaki.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.dining.boyaki.config.BeanConfig;
import com.dining.boyaki.config.SuccessHandler;
import com.dining.boyaki.config.WebSecurityConfig;
import com.dining.boyaki.model.entity.DiaryRecordCategory;
import com.dining.boyaki.model.form.DiaryRecordForm;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.model.service.DiaryRecordService;
import com.dining.boyaki.util.WithMockCustomUser;

@AutoConfigureMockMvc
@AutoConfigureMybatis
@WebMvcTest(controllers = UserCalendarController.class,
            includeFilters = @ComponentScan.Filter
                            (type = FilterType.ASSIGNABLE_TYPE,
                             value = {AccountUserDetailsService.class,BeanConfig.class,
            		                  SuccessHandler.class,WebSecurityConfig.class}))
public class UserCalendarControllerTest {
	
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
	
	@Nested
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
    class createContent {
		DiaryRecordForm form = new DiaryRecordForm();
		
		@BeforeEach
        void setUp() {
			form.setCategoryId(4);
			form.setDiaryDay(Date.valueOf("2022-02-19"));
			form.setRecord1("ハイボール一缶");
			form.setRecord2(null);
			form.setRecord3("スクワット20回");
			form.setPrice(0);
			form.setMemo(null);
			doNothing().when(diaryRecordService).insertDiaryRecord(form);
        }
		
		@Test
		void createContentで食事記録が登録される() throws Exception{
			when(diaryRecordService.findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-19"))).thenReturn(null);
			mockMvc.perform(post("/index/create/insert")
				           .flashAttr("diaryRecordForm", form)
				           .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				           .with(SecurityMockMvcRequestPostProcessors.csrf()))
				   .andExpect(status().is3xxRedirection())
		           .andExpect(redirectedUrl("/index"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-19"));
			verify(diaryRecordService,times(1)).insertDiaryRecord(form);
		}
		
		@Test
		void createContentでバリデーションエラーが発生する() throws Exception{
			DiaryRecordForm form = new DiaryRecordForm();
			form.setDiaryDay(null);
			form.setRecord1(null);
			form.setRecord2(null);
			form.setRecord3(null);
			when(diaryRecordService.findOneDiaryRecord(any(), anyInt(), any())).thenReturn(null);
			
			mockMvc.perform(post("/index/create/insert")
		      		       .flashAttr("diaryRecordForm", form)
		      		       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		      		       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       	   .andExpect(status().is2xxSuccessful())
		       	   .andExpect(model().attributeHasFieldErrors("diaryRecordForm"
		       			   ,"diaryDay","record1","record2","record3"))
		           .andExpect(view().name("UserCalendar/Create"));
			verify(diaryRecordService,times(0)).findOneDiaryRecord(any(), anyInt(), any());
			verify(diaryRecordService,times(0)).insertDiaryRecord(form);
		}
	
		@Test
		void createContentでレコードの重複が発生する() throws Exception{
			when(diaryRecordService.findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-19"))).thenReturn(form);
			
			mockMvc.perform(post("/index/create/insert")
						   .flashAttr("diaryRecordForm", form)
		                   .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		                   .with(SecurityMockMvcRequestPostProcessors.csrf()))
                   .andExpect(status().is2xxSuccessful())
                   .andExpect(model().hasNoErrors())
                   .andExpect(model().attribute("message", "既に同じカテゴリ、同じ日付で登録されています"))
                   .andExpect(view().name("UserCalendar/Create"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-19"));
			verify(diaryRecordService,times(0)).insertDiaryRecord(form);
		}
	}
	
	@Nested
	@WithMockCustomUser(userName="糸井",password="sigeSIGE",role="ROLE_USER")
	class showUserEditContent{
		DiaryRecordForm form = new DiaryRecordForm();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		@BeforeEach
		void setUp() {
			form.setCategoryId(1);
			form.setDiaryDay(Date.valueOf("2022-02-23"));
			form.setRecord1(null);
			form.setRecord2("グラノーラ");
			form.setRecord3(null);
			form.setPrice(100);
			form.setMemo(null);
			form.setCreateAt(LocalDateTime.parse("2022-02-23T09:43:28"));
		}
	
		@Test
		void showUserEditContentで食事記録編集画面へ遷移する() throws Exception{
			when(diaryRecordService.findOneDiaryRecord("糸井", 1, format.parse("2022-02-23"))).thenReturn(form);
		
			mockMvc.perform(get("/index/record/2022-02-23/1"))
		           .andExpect(status().is2xxSuccessful())
		           .andExpect(model().attribute("diaryRecordForm"
		        		                       ,hasProperty("createAt",is(LocalDateTime.parse("2022-02-23T09:43:28"))))
		    		          )
		           .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
		           .andExpect(view().name("UserCalendar/Edit"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("糸井", 1, Date.valueOf("2022-02-23"));
		}
		
		@Test
		void showUserEditContentでレコードが見つからない場合は404ページへ遷移する() throws Exception{
			when(diaryRecordService.findOneDiaryRecord("糸井", 1, format.parse("2022-02-23"))).thenReturn(null);
			
			mockMvc.perform(get("/index/record/2022-02-23/1"))
	           	   .andExpect(status().is2xxSuccessful())
	               .andExpect(view().name("Common/404"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("糸井", 1, Date.valueOf("2022-02-23"));
		}
		
		@Test
		void showUserEditContentでParseExceptionかNumberFormatExceptionが発生した場合は404ページへ遷移する() throws Exception{
			mockMvc.perform(get("/index/record/2022-ab-cd/1"))
        	       .andExpect(status().is2xxSuccessful())
                   .andExpect(view().name("Common/404"));
			mockMvc.perform(get("/index/record/2022-02-23/Number"))
 	       		   .andExpect(status().is2xxSuccessful())
 	       		   .andExpect(view().name("Common/404"));
		}
	}
	
	@Nested
	@WithMockCustomUser(userName="糸井",password="sigeSIGE",role="ROLE_USER")
    class updateContent {
		DiaryRecordForm form = new DiaryRecordForm();
		
		@BeforeEach
		void setUp() {
			form.setCategoryId(1);
			form.setDiaryDay(Date.valueOf("2022-02-23"));
			form.setRecord1(null);
			form.setRecord2("グラノーラ");
			form.setRecord3(null);
			form.setPrice(100);
			form.setMemo("自販機でコーヒー買った");
			form.setCreateAt(LocalDateTime.parse("2022-02-23T09:43:28"));
			doNothing().when(diaryRecordService).insertDiaryRecord(form);
		}
		
		@Test
		void updateContentで食事記録を更新する() throws Exception{
			when(diaryRecordService.findOneDiaryRecord("糸井", 1, Date.valueOf("2022-02-23"))).thenReturn(form);
		
			mockMvc.perform(post("/index/record/commit")
					       .flashAttr("diaryRecordForm", form)
					       .param("update", "update")
					       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
					       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		           .andExpect(status().is3xxRedirection())
		           .andExpect(redirectedUrl("/index"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("糸井", 1, Date.valueOf("2022-02-23"));
			verify(diaryRecordService,times(1)).updateDiaryRecord(form);
		}
	
		@Test
		void updateContentでバリデーションエラーが発生する() throws Exception{
			form.setCategoryId(1);
			form.setDiaryDay(null);
			form.setRecord1(null);
			form.setRecord2(null);
			form.setRecord3(null);
			form.setPrice(100);
			form.setMemo("自販機でコーヒー買った");
			form.setCreateAt(LocalDateTime.parse("2022-02-23T09:43:28"));
			
			when(diaryRecordService.findOneDiaryRecord("糸井", 1, Date.valueOf("2022-02-23"))).thenReturn(form);
			
			mockMvc.perform(post("/index/record/commit")
	                       .flashAttr("diaryRecordForm", form)
	                       .param("update", "update")
	                       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	                       .with(SecurityMockMvcRequestPostProcessors.csrf()))
	               .andExpect(status().is2xxSuccessful())
			       .andExpect(model().attributeHasFieldErrors("diaryRecordForm"
			    		   ,"diaryDay","record1","record2","record3"))
			       .andExpect(view().name("UserCalendar/Edit"));
			verify(diaryRecordService,times(0)).findOneDiaryRecord("糸井", 1, Date.valueOf("2022-02-23"));
			verify(diaryRecordService,times(0)).insertDiaryRecord(form);
		}
	
		@Test
		void updateContentでレコード重複エラーが発生する() throws Exception{
			DiaryRecordForm exist = new DiaryRecordForm();
			exist.setUserName("糸井");
			exist.setCategoryId(1);
			exist.setDiaryDay(Date.valueOf("2022-02-22"));
			exist.setCreateAt(LocalDateTime.parse("2022-02-22T08:39:50"));
			
			when(diaryRecordService.findOneDiaryRecord("糸井", 1, Date.valueOf("2022-02-23"))).thenReturn(exist);
			
			mockMvc.perform(post("/index/record/commit")
		                   .flashAttr("diaryRecordForm", form)
		                   .param("update", "update")
		                   .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		                   .with(SecurityMockMvcRequestPostProcessors.csrf()))
	               .andExpect(status().is2xxSuccessful())
	               .andExpect(model().hasNoErrors())
	               .andExpect(model().attribute("message", "既に同じカテゴリ、同じ日付で登録されています"))
	               .andExpect(view().name("UserCalendar/Create"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("糸井", 1, Date.valueOf("2022-02-23"));
			verify(diaryRecordService,times(0)).insertDiaryRecord(form);
		}
	}
	
	@Test
	@WithMockCustomUser(userName="糸井",password="sigeSIGE",role="ROLE_USER")
	void deleteContentで食事記録を削除する() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		form.setCategoryId(1);
		form.setDiaryDay(Date.valueOf("2022-02-23"));
		form.setRecord1(null);
		form.setRecord2("グラノーラ");
		form.setRecord3(null);
		form.setPrice(100);
		form.setMemo("自販機でコーヒー買った");
		form.setCreateAt(LocalDateTime.parse("2022-02-23T09:43:28"));
		
		doNothing().when(diaryRecordService).deleteDiaryRecord(form);
		
		mockMvc.perform(post("/index/record/commit")
                       .flashAttr("diaryRecordForm", form)
                       .param("delete", "delete")
                       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                       .with(SecurityMockMvcRequestPostProcessors.csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/index"));
		verify(diaryRecordService,times(1)).deleteDiaryRecord(form);
	}

}
