package com.dining.boyaki.controller.combined;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dining.boyaki.config.BeanConfig;
import com.dining.boyaki.config.SuccessHandler;
import com.dining.boyaki.config.S3Config;
import com.dining.boyaki.controller.DiaryRecordController;
import com.dining.boyaki.model.enums.DiaryRecordCategory;
import com.dining.boyaki.model.form.DiaryRecordForm;
import com.dining.boyaki.model.form.FileUploadForm;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.model.service.DiaryRecordService;
import com.dining.boyaki.model.service.FileUploadService;
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
			                		 S3Config.class,DiaryRecordService.class,FileUploadService.class}))
@Transactional
public class DiaryRecordControllerCombinedTest {
	
	private static LocalDateTime datetime;
	private static MockedStatic<LocalDateTime> time;
	
	private static UUID uuidName;
	private static MockedStatic<UUID> uuid;
	
	@Autowired
	private MockMvc mockMvc;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		time = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
		uuid = Mockito.mockStatic(UUID.class,Mockito.CALLS_REAL_METHODS);
	}
	
	@AfterEach
    void tearDown() throws Exception {
        time.close();
        uuid.close();
	}
	
	@Test
	@WithMockUser(username="????????????",authorities= {"ROLE_USER"})
	void showUserCalendar?????????????????????????????????????????????() throws Exception{
		mockMvc.perform(get("/index"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(view().name("UserCalendar/index"));
	}
	
	@Nested
	class showCreateContent {
		@Test
		@WithMockUser(username="????????????",authorities= {"ROLE_USER"})
		void showCreateContent?????????????????????????????????????????????() throws Exception{
			mockMvc.perform(get("/index/create"))
			       .andExpect(status().is2xxSuccessful())
			       .andExpect(model().attributeExists("diaryRecordForm"))
			       .andExpect(model().attributeExists("fileUploadForm"))
			       .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
		           .andExpect(view().name("UserCalendar/Create"));
		}
		
		@Test
		@WithMockCustomUser(userName="guestuser",password="gesugesudegesu",role="ROLE_USER")
		void showCreateContent????????????????????????????????????????????????() throws Exception{
			mockMvc.perform(get("/index/create"))
			       .andExpect(status().isForbidden())
		           .andExpect(forwardedUrl("/accessdenied"));
		}
	}
	
	@Test
	@WithMockCustomUser(userName="?????????",password="pinballs",role="ROLE_USER")
	@DatabaseSetup(value="/controller/DiaryRecord/setup/")
	@ExpectedDatabase(value="/controller/DiaryRecord/insert/",table="diary_record")
	void createContent?????????????????????????????????() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm("?????????",1,Date.valueOf("2022-02-26"),
                                                   "???????????????",null,null,null,null,null);
        FileUploadForm file = new FileUploadForm();
        MultipartFile multipartFile = new MockMultipartFile("file","".getBytes());
		file.setMultipartFile(multipartFile);
        datetime = LocalDateTime.of(2022, 2, 26, 14, 00, 31);
		time.when(LocalDateTime::now).thenReturn(datetime);
		
		mockMvc.perform(post("/index/create/insert") //????????????
		           	   .flashAttr("diaryRecordForm", form)
		           	   .flashAttr("fileUploadForm", file)
		               .contentType(MediaType.MULTIPART_FORM_DATA)
		               .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().is3xxRedirection())
			   .andExpect(model().hasNoErrors())
	           .andExpect(redirectedUrl("/index"));
		
		form = new DiaryRecordForm("?????????",2,Date.valueOf("2022-02-26"),
				                   "??????","????????????","?????????????????????",null,null,null);
		file = new FileUploadForm();
		File upFile = new File("src/test/resources/image/3840_2160.jpg");
		Path path = Paths.get(upFile.getCanonicalPath());
		byte[] bytes = Files.readAllBytes(path);
		multipartFile = new MockMultipartFile("file","3840_2160.jpg","multipart/form-data",bytes);
		file.setMultipartFile(multipartFile);
		uuidName = UUID.fromString("f3241f8f-006e-4429-8438-f42adb1d1869");
		uuid.when(UUID::randomUUID).thenReturn(uuidName);
		datetime = LocalDateTime.of(2022, 2, 26, 14, 01, 25);
		time.when(LocalDateTime::now).thenReturn(datetime);
		
		mockMvc.perform(post("/index/create/insert") //????????????
		           	   .flashAttr("diaryRecordForm", form)
		           	   .flashAttr("fileUploadForm", file)
		               .contentType(MediaType.MULTIPART_FORM_DATA)
		               .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().is3xxRedirection())
			   .andExpect(model().hasNoErrors())
	           .andExpect(redirectedUrl("/index"));
		}
	
	@Test
	@WithMockCustomUser(userName="?????????",password="pinballs",role="ROLE_USER")
	@DatabaseSetup(value="/controller/DiaryRecord/setup/")
	void createContent????????????????????????????????????????????????() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		form.setDiaryDay(null);
		form.setRecord1(null);
		form.setRecord2(null);
		form.setRecord3(null);
		FileUploadForm file = new FileUploadForm();
		File upFile = new File("src/test/resources/image/testApp.js");
		Path path = Paths.get(upFile.getCanonicalPath());
		byte[] bytes = Files.readAllBytes(path);
		MultipartFile multipartFile = new MockMultipartFile("file","testApp.js","multipart/form-data",bytes);
		file.setMultipartFile(multipartFile);
		
		mockMvc.perform(post("/index/create/insert")
   		       		   .flashAttr("diaryRecordForm", form)
   		       		   .flashAttr("fileUploadForm", file)
		   		       .contentType(MediaType.MULTIPART_FORM_DATA)
		   		       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
		       .andExpect(model().attributeHasFieldErrors("diaryRecordForm"
		    			   ,"diaryDay","record1","record2","record3"))
		       .andExpect(model().attributeHasFieldErrors("fileUploadForm"
	       			       ,"multipartFile"))
		       .andExpect(view().name("UserCalendar/Create"));
	}
	
	@Test
	@WithMockCustomUser(userName="?????????",password="pinballs",role="ROLE_USER")
	@DatabaseSetup(value="/controller/DiaryRecord/setup/")
	void createContent???????????????????????????????????????() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm("?????????",2,Date.valueOf("2022-02-11"),
                                                   "??????","????????????","?????????????????????",null,null,null);
		FileUploadForm file = new FileUploadForm();
		mockMvc.perform(post("/index/create/insert")
				       .flashAttr("diaryRecordForm", form)
				       .flashAttr("fileUploadForm", file)
				       .contentType(MediaType.MULTIPART_FORM_DATA)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
        	   .andExpect(status().is2xxSuccessful())
        	   .andExpect(model().hasNoErrors())
        	   .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
        	   .andExpect(model().attribute("message", "??????????????????????????????????????????????????????????????????"))
               .andExpect(view().name("UserCalendar/Create"));
	}
	
	@Test
	@WithMockCustomUser(userName="?????????",password="pinballs",role="ROLE_USER")
	@DatabaseSetup(value="/controller/DiaryRecord/setup/")
	void createContent?????????????????????????????????????????????() throws Exception{
		FileUploadForm file = new FileUploadForm();
		File upFile = new File("src/test/resources/image/aws.jpeg");
		Path path = Paths.get(upFile.getCanonicalPath());
		byte[] bytes = Files.readAllBytes(path);
		MultipartFile multipartFile = new MockMultipartFile("file","aws.jpeg","multipart/form-data",bytes);
		file.setMultipartFile(multipartFile);
		DiaryRecordForm form = new DiaryRecordForm("?????????",2,Date.valueOf("2022-02-26"),
                                                   "??????","????????????","?????????????????????",null,null,null);
		mockMvc.perform(post("/index/create/insert")
					   .flashAttr("diaryRecordForm", form)
					   .flashAttr("fileUploadForm", file)
	                   .contentType(MediaType.MULTIPART_FORM_DATA)
	                   .with(SecurityMockMvcRequestPostProcessors.csrf()))
               .andExpect(status().is2xxSuccessful())
               .andExpect(model().hasNoErrors())
               .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
               .andExpect(model().attribute("message", "?????????????????????????????????"))
               .andExpect(view().name("UserCalendar/Create"));
		}
	
	@Test
	@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
	void createContent????????????????????????????????????() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		FileUploadForm file = new FileUploadForm();
		mockMvc.perform(post("/index/create/insert")
			           .flashAttr("diaryRecordForm", form)
			           .flashAttr("fileUploadForm", file)
			           .contentType(MediaType.MULTIPART_FORM_DATA)
			           .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl("/accessdenied"));
	}
	
	@Test
	@WithMockCustomUser(userName="??????",password="sigeSIGE",role="ROLE_USER")
	@DatabaseSetup(value="/controller/DiaryRecord/setup/")
	void showUserEditContent??????????????????????????????????????????() throws Exception{
		mockMvc.perform(get("/index/record/2022-01-31/3")) //????????????
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("diaryRecordForm"
		     		                       ,hasProperty("createAt",is(LocalDateTime.parse("2022-02-02T10:22:57"))))
		 		          )
		       .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
	           .andExpect(model().attribute("exist", true))
	           .andExpect(model().attributeExists("image"))
		       .andExpect(view().name("UserCalendar/Edit"));
		
		mockMvc.perform(get("/index/record/2022-01-31/2")) //????????????
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("diaryRecordForm"
		     		                       ,hasProperty("createAt",is(LocalDateTime.parse("2022-02-02T10:22:01"))))
		 		          )
		       .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
	           .andExpect(model().attribute("exist", false))
	           .andExpect(model().attributeDoesNotExist("image"))
		       .andExpect(view().name("UserCalendar/Edit"));
		
		mockMvc.perform(get("/index/record/2022-02-23/1"))
			       	   .andExpect(status().is2xxSuccessful())
		       .andExpect(view().name("error/404"));
	}
	
	@Test
	@WithMockCustomUser(userName="??????",password="sigeSIGE",role="ROLE_USER")
	void showUserEditContent???ParseException???NumberFormatException????????????????????????404????????????????????????() throws Exception{
		mockMvc.perform(get("/index/record/2022-ab-cd/1"))
	           .andExpect(status().is2xxSuccessful())
               .andExpect(view().name("error/404"));
		mockMvc.perform(get("/index/record/2022-02-23/Number"))
 		   	   .andExpect(status().is2xxSuccessful())
 		   	   .andExpect(view().name("error/404"));
	}
	
	@Test
	@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
	void showUserEditContent?????????????????????????????????????????????() throws Exception{
		mockMvc.perform(get("/index/record/2022-02-27/1"))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl("/accessdenied"));
	}
	
	@Test
	@WithMockCustomUser(userName="??????",password="sigeSIGE",role="ROLE_USER")
	@DatabaseSetup(value="/controller/DiaryRecord/setup/")
	@ExpectedDatabase(value="/controller/DiaryRecord/update/",table="diary_record")
	void updateContent??????????????????????????????() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm("??????",2,Date.valueOf("2022-01-31"),
				                                   "????????????",null,null,
				                                   null,"??????",LocalDateTime.parse("2022-02-02T10:22:01"));
		FileUploadForm file = new FileUploadForm();
		MultipartFile multipartFile = new MockMultipartFile("file","".getBytes());
		file.setMultipartFile(multipartFile);
		datetime = LocalDateTime.of(2022, 2, 02, 10, 23, 06);
		time.when(LocalDateTime::now).thenReturn(datetime);
		
		mockMvc.perform(post("/index/record/commit") //????????????
				       .flashAttr("diaryRecordForm", form)
				       .flashAttr("fileUploadForm", file)
				       .param("update", "update")
				       .contentType(MediaType.MULTIPART_FORM_DATA)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
	           .andExpect(status().is3xxRedirection())
	           .andExpect(model().hasNoErrors())
	           .andExpect(redirectedUrl("/index"));
		
		form = new DiaryRecordForm("??????",3,Date.valueOf("2022-01-31"),"?????????","?????????",null,
		                           "2a7de85e-0234-423e-8aca-fc6dce1a753b 2022-02-24 20-13-59..jpg",
		                           "????????????",LocalDateTime.parse("2022-02-02T10:22:57"));
		file = new FileUploadForm();
		File upFile = new File("src/test/resources/image/3840_2160.jpg");
		Path path = Paths.get(upFile.getCanonicalPath());
		byte[] bytes = Files.readAllBytes(path);
		multipartFile = new MockMultipartFile("file","3840_2160.jpg","multipart/form-data",bytes);
		file.setMultipartFile(multipartFile);
		datetime = LocalDateTime.of(2022, 2, 02, 16, 23, 33);
		time.when(LocalDateTime::now).thenReturn(datetime);
		
		mockMvc.perform(post("/index/record/commit") //????????????
					   .flashAttr("diaryRecordForm", form)
					   .flashAttr("fileUploadForm", file)
					   .param("update", "update")
					   .contentType(MediaType.MULTIPART_FORM_DATA)
					   .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().is3xxRedirection())
			   .andExpect(model().hasNoErrors())
			   .andExpect(redirectedUrl("/index"));
	}
	
	@Test
	@WithMockCustomUser(userName="??????",password="sigeSIGE",role="ROLE_USER")
	@DatabaseSetup(value="/controller/DiaryRecord/setup/")
	void updateContent????????????????????????????????????????????????() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		form.setDiaryDay(null);
		form.setRecord1(null);
		form.setRecord2(null);
		form.setRecord3(null);
		FileUploadForm file = new FileUploadForm();
		File upFile = new File("src/test/resources/image/testApp.js");
		Path path = Paths.get(upFile.getCanonicalPath());
		byte[] bytes = Files.readAllBytes(path);
		MultipartFile multipartFile = new MockMultipartFile("file","testApp.js","multipart/form-data",bytes);
		file.setMultipartFile(multipartFile);
		
		mockMvc.perform(post("/index/record/commit")
		               .flashAttr("diaryRecordForm", form)
		               .flashAttr("fileUploadForm", file)
		               .param("update", "update")
		               .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		               .with(SecurityMockMvcRequestPostProcessors.csrf()))
               .andExpect(status().is2xxSuccessful())
               .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
		       .andExpect(model().attributeHasFieldErrors("diaryRecordForm"
		    		   ,"diaryDay","record1","record2","record3"))
		       .andExpect(model().attributeHasFieldErrors("fileUploadForm"
	       			   ,"multipartFile"))
		       .andExpect(view().name("UserCalendar/Edit"));
	}
	
	@Test
	@WithMockCustomUser(userName="??????",password="sigeSIGE",role="ROLE_USER")
	@DatabaseSetup(value="/controller/DiaryRecord/setup/")
	void updateContent?????????????????????????????????????????????() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm("??????",2,Date.valueOf("2022-01-31"),
				                                   "?????????","?????????",null,
				                                   null,"????????????",LocalDateTime.parse("2022-02-02T10:22:57"));
		FileUploadForm file = new FileUploadForm();
		mockMvc.perform(post("/index/record/commit")
                       .flashAttr("diaryRecordForm", form)
                       .flashAttr("fileUploadForm", file)
                       .param("update", "update")
                       .contentType(MediaType.MULTIPART_FORM_DATA)
                       .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().is2xxSuccessful())
			   .andExpect(model().hasNoErrors())
			   .andExpect(model().attribute("message", "??????????????????????????????????????????????????????????????????"))
			   .andExpect(view().name("UserCalendar/Edit"));
	}
	
	@Test
	@WithMockCustomUser(userName="??????",password="sigeSIGE",role="ROLE_USER")
	@DatabaseSetup(value="/controller/DiaryRecord/setup/")
	void updateContent?????????????????????????????????????????????() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm("??????",3,Date.valueOf("2022-01-31"),
                                                   "?????????","?????????","null",
                                                   null,"????????????",LocalDateTime.parse("2022-02-02T10:22:57"));
		FileUploadForm file = new FileUploadForm();
		File upFile = new File("src/test/resources/image/aws.jpeg");
		Path path = Paths.get(upFile.getCanonicalPath());
		byte[] bytes = Files.readAllBytes(path);
		MultipartFile multipartFile = new MockMultipartFile("file","aws.jpeg","multipart/form-data",bytes);
		file.setMultipartFile(multipartFile);
		mockMvc.perform(post("/index/record/commit")
	                   .flashAttr("diaryRecordForm", form)
	                   .flashAttr("fileUploadForm", file)
	                   .param("update", "update")
	                   .contentType(MediaType.MULTIPART_FORM_DATA)
	                   .with(SecurityMockMvcRequestPostProcessors.csrf()))
	           .andExpect(status().is2xxSuccessful())
	           .andExpect(model().hasNoErrors())
	           .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
	           .andExpect(model().attribute("message", "?????????????????????????????????"))
	           .andExpect(view().name("UserCalendar/Edit"));
	}
	
	@Test
	@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
	void updateContent????????????????????????????????????() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		FileUploadForm file = new FileUploadForm();
		mockMvc.perform(post("/index/record/commit")
	                   .flashAttr("diaryRecordForm", form)
	                   .flashAttr("fileUploadForm", file)
	                   .param("update", "update")
	                   .contentType(MediaType.MULTIPART_FORM_DATA)
	                   .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl("/accessdenied"));
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/DiaryRecord/setup/")
	@ExpectedDatabase(value="/controller/DiaryRecord/delete/",table="diary_record")
	void deleteContent??????????????????????????????() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm("miho",3,Date.valueOf("2022-01-26"),
				                                    null,"?????????????????????","????????????????????????",
				                                    null,null,LocalDateTime.parse("2022-01-26T18:42:15"));
		mockMvc.perform(post("/index/record/commit")
		               .flashAttr("diaryRecordForm", form)
		               .param("delete", "delete")
		               .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		               .with(SecurityMockMvcRequestPostProcessors.csrf()))
        	   .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/index"));
	}
	
	@Test
	@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
	void deleteContent????????????????????????????????????() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		mockMvc.perform(post("/index/record/commit")
                       .flashAttr("diaryRecordForm", form)
	                    .param("delete", "delete")
	                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	                    .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl("/accessdenied"));
	}
}
