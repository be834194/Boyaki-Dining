package com.dining.boyaki.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;

import com.dining.boyaki.config.BeanConfig;
import com.dining.boyaki.config.SuccessHandler;
import com.dining.boyaki.model.enums.DiaryRecordCategory;
import com.dining.boyaki.model.form.DiaryRecordForm;
import com.dining.boyaki.model.form.FileUploadForm;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.model.service.DiaryRecordService;
import com.dining.boyaki.model.service.FileUploadService;
import com.dining.boyaki.util.WithMockCustomUser;

@AutoConfigureMockMvc
@AutoConfigureMybatis
@WebMvcTest(controllers = DiaryRecordController.class,
            includeFilters = @ComponentScan.Filter
                            (type = FilterType.ASSIGNABLE_TYPE,
                             value = {AccountUserDetailsService.class,BeanConfig.class,
            		                  SuccessHandler.class}))
public class DiaryRecordControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@MockBean
	DiaryRecordService diaryRecordService;
	
	@MockBean
	FileUploadService fileUploadService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
				                 .apply(springSecurity()).build();
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
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void showCreateContent????????????????????????????????????????????????() throws Exception{
			mockMvc.perform(get("/index/create"))
				   .andExpect(status().isForbidden())
				   .andExpect(forwardedUrl("/accessdenied"));
		}
	}
	
	@Nested
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
    class createContent {
		DiaryRecordForm form;
		FileUploadForm file;
		@BeforeEach
        void setUp() throws Exception{
			file = new FileUploadForm();
			File upFile = new File("src/test/resources/image/aws.jpeg");
			Path path = Paths.get(upFile.getCanonicalPath());
			byte[] bytes = Files.readAllBytes(path);
			MultipartFile multipartFile = new MockMultipartFile("file","aws.jpeg","multipart/form-data",bytes);
			file.setMultipartFile(multipartFile);
			when(fileUploadService.fileValid(file)).thenReturn(false);
			form = new DiaryRecordForm(null,4,Date.valueOf("2022-02-19"),
					                   "?????????????????????",null,"???????????????20???",
					                   null,null,null);
			doNothing().when(diaryRecordService).insertDiaryRecord(form);
			when(diaryRecordService.findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-19"))).thenReturn(null);
			
			when(fileUploadService.fileUpload(any(FileUploadForm.class), any(String.class), any()))
			                      .thenReturn("2022-02-19 22-19-37.jpg");
        }
		
		@Test
		void createContent?????????????????????????????????() throws Exception{
			MultipartFile multipartFile = new MockMultipartFile("file","".getBytes());
			file.setMultipartFile(multipartFile);
			when(fileUploadService.fileValid(file)).thenReturn(true);
			
			mockMvc.perform(post("/index/create/insert") //????????????
				           .flashAttr("diaryRecordForm", form)
				           .flashAttr("fileUploadForm", file)
				           .contentType(MediaType.MULTIPART_FORM_DATA)
				           .with(SecurityMockMvcRequestPostProcessors.csrf()))
			       .andExpect(model().hasNoErrors())
				   .andExpect(status().is3xxRedirection())
		           .andExpect(redirectedUrl("/index"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-19"));
			verify(diaryRecordService,times(1)).insertDiaryRecord(form);
			verify(fileUploadService,times(0)).fileValid(file);
			verify(fileUploadService,times(0)).fileUpload(any(FileUploadForm.class), any(String.class), any());
			
			File upFile = new File("src/test/resources/image/3840_2160.jpg"); //????????????
			Path path = Paths.get(upFile.getCanonicalPath());
			byte[] bytes = Files.readAllBytes(path);
			multipartFile = new MockMultipartFile("file","3840_2160.jpg","multipart/form-data",bytes);
			file.setMultipartFile(multipartFile);
			mockMvc.perform(post("/index/create/insert")
				           .flashAttr("diaryRecordForm", form)
				           .flashAttr("fileUploadForm", file)
				           .contentType(MediaType.MULTIPART_FORM_DATA)
				           .with(SecurityMockMvcRequestPostProcessors.csrf()))
			       .andExpect(model().hasNoErrors())
				   .andExpect(status().is3xxRedirection())
		           .andExpect(redirectedUrl("/index"));
			verify(diaryRecordService,times(2)).findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-19"));
			verify(diaryRecordService,times(2)).insertDiaryRecord(form);
			verify(fileUploadService,times(1)).fileValid(file);
			verify(fileUploadService,times(1)).fileUpload(any(FileUploadForm.class), any(String.class), any());
		}
		
		@Test
		void createContent????????????????????????????????????????????????() throws Exception{
			form.setDiaryDay(null);
			form.setRecord1(null);
			form.setRecord2(null);
			form.setRecord3(null);
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
			verify(diaryRecordService,times(0)).findOneDiaryRecord(any(), anyInt(), any());
			verify(diaryRecordService,times(0)).insertDiaryRecord(form);
			verify(fileUploadService,times(0)).fileValid(file);
			verify(fileUploadService,times(0)).fileUpload(any(FileUploadForm.class), any(String.class), any());
		}
	
		@Test
		void createContent?????????????????????????????????????????????() throws Exception{
			when(diaryRecordService.findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-19"))).thenReturn(form);
			
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
			verify(diaryRecordService,times(1)).findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-19"));
			verify(diaryRecordService,times(0)).insertDiaryRecord(form);
			verify(fileUploadService,times(0)).fileValid(file);
			verify(fileUploadService,times(0)).fileUpload(any(FileUploadForm.class), any(String.class), any());
		}
		
		@Test
		void createContent?????????????????????????????????????????????() throws Exception{
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
			verify(diaryRecordService,times(1)).findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-19"));
			verify(diaryRecordService,times(0)).insertDiaryRecord(form);
			verify(fileUploadService,times(1)).fileValid(file);
			verify(fileUploadService,times(0)).fileUpload(any(FileUploadForm.class), any(String.class), any());
		}
		
		@Test
		void fileValid???IOException???????????????() throws Exception{
			when(fileUploadService.fileValid(file)).thenReturn(true);
			doThrow(new IOException("")).when(fileUploadService).fileValid(file);
			
			mockMvc.perform(post("/index/create/insert")
						   .flashAttr("diaryRecordForm", form)
						   .flashAttr("fileUploadForm", file)
		                   .contentType(MediaType.MULTIPART_FORM_DATA)
		                   .with(SecurityMockMvcRequestPostProcessors.csrf()))
	               .andExpect(status().is2xxSuccessful())
	               .andExpect(model().hasNoErrors())
	               .andExpect(view().name("error/Other"));
		}
		
		@Test
		void fileUpload???IOException???????????????() throws Exception{
			when(fileUploadService.fileValid(file)).thenReturn(true);
			doThrow(new IOException("")).when(fileUploadService).fileUpload(any(FileUploadForm.class),any(String.class), any());
			
			mockMvc.perform(post("/index/create/insert")
						   .flashAttr("diaryRecordForm", form)
						   .flashAttr("fileUploadForm", file)
		                   .contentType(MediaType.MULTIPART_FORM_DATA)
		                   .with(SecurityMockMvcRequestPostProcessors.csrf()))
	               .andExpect(status().is2xxSuccessful())
	               .andExpect(model().hasNoErrors())
	               .andExpect(view().name("error/Other"));
		}
		
		@Test
		void fileUpload???AmazonServiceException???????????????() throws Exception{
			when(fileUploadService.fileValid(file)).thenReturn(true);
			doThrow(new AmazonServiceException("")).when(fileUploadService).fileUpload(any(FileUploadForm.class),any(String.class), any());
			
			mockMvc.perform(post("/index/create/insert")
						   .flashAttr("diaryRecordForm", form)
						   .flashAttr("fileUploadForm", file)
		                   .contentType(MediaType.MULTIPART_FORM_DATA)
		                   .with(SecurityMockMvcRequestPostProcessors.csrf()))
	               .andExpect(status().is2xxSuccessful())
	               .andExpect(model().hasNoErrors())
	               .andExpect(view().name("error/Other"));
		}
		
		@Test
		void fileUpload???SdkClientException???????????????() throws Exception{
			when(fileUploadService.fileValid(file)).thenReturn(true);
			doThrow(new SdkClientException("")).when(fileUploadService).fileUpload(any(FileUploadForm.class),any(String.class), any());
			
			mockMvc.perform(post("/index/create/insert")
						   .flashAttr("diaryRecordForm", form)
						   .flashAttr("fileUploadForm", file)
		                   .contentType(MediaType.MULTIPART_FORM_DATA)
		                   .with(SecurityMockMvcRequestPostProcessors.csrf()))
	               .andExpect(status().is2xxSuccessful())
	               .andExpect(model().hasNoErrors())
	               .andExpect(view().name("error/Other"));
		}
		
		@Test
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void createContent????????????????????????????????????() throws Exception{
			mockMvc.perform(post("/index/create/insert")
			           .flashAttr("diaryRecordForm", form)
			           .flashAttr("fileUploadForm", file)
			           .contentType(MediaType.MULTIPART_FORM_DATA)
			           .with(SecurityMockMvcRequestPostProcessors.csrf()))
				   .andExpect(status().isForbidden())
				   .andExpect(forwardedUrl("/accessdenied"));
		}
	}
	
	@Nested
	@WithMockCustomUser(userName="??????",password="sigeSIGE",role="ROLE_USER")
	class showUserEditContent{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		DiaryRecordForm form;
		FileUploadForm file;
		@BeforeEach
        void setUp() throws Exception{
			form  = new DiaryRecordForm("??????",1,Date.valueOf("2022-02-23"),null,"???????????????",null,
					                   null,null,LocalDateTime.parse("2022-02-23T09:43:28"));
		}
	
		@Test
		void showUserEditContent??????????????????????????????????????????() throws Exception{
			when(diaryRecordService.findOneDiaryRecord("??????", 1, format.parse("2022-02-23"))).thenReturn(form);
			when(fileUploadService.fileDownload(any(String.class), any(String.class))).thenReturn("hogehoge.jpg");
			mockMvc.perform(get("/index/record/2022-02-23/1")) //????????????
		           .andExpect(status().is2xxSuccessful())
		           .andExpect(model().attribute("diaryRecordForm"
		        		                       ,hasProperty("createAt",is(LocalDateTime.parse("2022-02-23T09:43:28"))))
		    		          )
		           .andExpect(model().attributeExists("fileUploadForm"))
		           .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
		           .andExpect(model().attribute("exist", false))
		           .andExpect(view().name("UserCalendar/Edit"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("??????", 1, Date.valueOf("2022-02-23"));
			verify(fileUploadService,times(0)).fileDownload(any(String.class), any(String.class));
			
			form.setImageName("hogehoge.jpg");
			mockMvc.perform(get("/index/record/2022-02-23/1")) //????????????
		           .andExpect(status().is2xxSuccessful())
		           .andExpect(model().attribute("diaryRecordForm"
		        		                       ,hasProperty("createAt",is(LocalDateTime.parse("2022-02-23T09:43:28"))))
		    		          )
		           .andExpect(model().attributeExists("fileUploadForm"))
		           .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
		           .andExpect(model().attribute("exist", true))
		           .andExpect(model().attributeExists("image"))
		           .andExpect(view().name("UserCalendar/Edit"));
			verify(diaryRecordService,times(2)).findOneDiaryRecord("??????", 1, Date.valueOf("2022-02-23"));
			verify(fileUploadService,times(1)).fileDownload(any(String.class), any(String.class));
		}
		
		@Test
		void showUserEditContent?????????????????????????????????????????????404????????????????????????() throws Exception{
			when(diaryRecordService.findOneDiaryRecord("??????", 1, format.parse("2022-02-23"))).thenReturn(null);
			
			mockMvc.perform(get("/index/record/2022-02-23/1"))
	           	   .andExpect(status().is2xxSuccessful())
	               .andExpect(view().name("error/404"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("??????", 1, Date.valueOf("2022-02-23"));
		}
		
		@Test
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
	}
	
	@Nested
	@WithMockCustomUser(userName="??????",password="sigeSIGE",role="ROLE_USER")
    class updateContent {
		DiaryRecordForm form;
		FileUploadForm file;
		@BeforeEach
		void setUp() throws Exception{
			file = new FileUploadForm();
			MultipartFile multipartFile = new MockMultipartFile("file","".getBytes());
			file.setMultipartFile(multipartFile);
			form = new DiaryRecordForm("??????",1,Date.valueOf("2022-02-23"),
					                   null,"???????????????",null,
					                   null,"?????????????????????????????????",LocalDateTime.parse("2022-02-23T09:43:28"));
			doNothing().when(diaryRecordService).insertDiaryRecord(form);
			when(diaryRecordService.findOneDiaryRecord("??????", 1, Date.valueOf("2022-02-23"))).thenReturn(form);
			when(fileUploadService.fileValid(file)).thenReturn(true);
			when(fileUploadService.fileUpload(any(FileUploadForm.class), any(String.class), any()))
			                      .thenReturn("isorepublic-breakfast-table-1.jpg");
		}
		
		@Test
		void updateContent??????????????????????????????() throws Exception{
			mockMvc.perform(post("/index/record/commit") //????????????
					       .flashAttr("diaryRecordForm", form)
					       .flashAttr("fileUploadForm", file)
					       .param("update", "update")
					       .contentType(MediaType.MULTIPART_FORM_DATA)
					       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		           .andExpect(status().is3xxRedirection())
		           .andExpect(model().hasNoErrors())
		           .andExpect(redirectedUrl("/index"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("??????", 1, Date.valueOf("2022-02-23"));
			verify(diaryRecordService,times(1)).updateDiaryRecord(form);
			verify(fileUploadService,times(0)).fileValid(file);
			verify(fileUploadService,times(0)).fileUpload(any(FileUploadForm.class), any(String.class), any());
			
			File upFile = new File("src/test/resources/image/isorepublic-breakfast-table-1.jpg");
			Path path = Paths.get(upFile.getCanonicalPath());
			byte[] bytes = Files.readAllBytes(path);
			MultipartFile multipartFile = new MockMultipartFile("file","isorepublic-breakfast-table-1.jpg","multipart/form-data",bytes);
			file.setMultipartFile(multipartFile);
			mockMvc.perform(post("/index/record/commit") //????????????
					       .flashAttr("diaryRecordForm", form)
					       .flashAttr("fileUploadForm", file)
					       .param("update", "update")
					       .contentType(MediaType.MULTIPART_FORM_DATA)
					       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		           .andExpect(status().is3xxRedirection())
		           .andExpect(model().hasNoErrors())
		           .andExpect(redirectedUrl("/index"));
			verify(diaryRecordService,times(2)).findOneDiaryRecord("??????", 1, Date.valueOf("2022-02-23"));
			verify(diaryRecordService,times(2)).updateDiaryRecord(form);
			verify(fileUploadService,times(1)).fileValid(file);
			verify(fileUploadService,times(1)).fileUpload(any(FileUploadForm.class), any(String.class), any());
		}
	
		@Test
		void updateContent????????????????????????????????????????????????() throws Exception{
			form.setDiaryDay(null);
			form.setRecord1(null);
			form.setRecord2(null);
			form.setRecord3(null);
			form.setCreateAt(LocalDateTime.parse("2022-02-23T09:43:28"));
			File upFile = new File("src/test/resources/image/testApp.js");
			Path path = Paths.get(upFile.getCanonicalPath());
			byte[] bytes = Files.readAllBytes(path);
			MultipartFile multipartFile = new MockMultipartFile("file","testApp.js","multipart/form-data",bytes);
			file.setMultipartFile(multipartFile);
			
			mockMvc.perform(post("/index/record/commit")
	                       .flashAttr("diaryRecordForm", form)
	                       .flashAttr("fileUploadForm", file)
	                       .param("update", "update")
	                       .contentType(MediaType.MULTIPART_FORM_DATA)
	                       .with(SecurityMockMvcRequestPostProcessors.csrf()))
	               .andExpect(status().is2xxSuccessful())
	               .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
			       .andExpect(model().attributeHasFieldErrors("diaryRecordForm"
			    		   ,"diaryDay","record1","record2","record3"))
			       .andExpect(model().attributeHasFieldErrors("fileUploadForm"
		       			   ,"multipartFile"))
			       .andExpect(view().name("UserCalendar/Edit"));
			verify(diaryRecordService,times(0)).findOneDiaryRecord("??????", 1, Date.valueOf("2022-02-23"));
			verify(diaryRecordService,times(0)).insertDiaryRecord(form);
		}
	
		@Test
		void updateContent?????????????????????????????????????????????() throws Exception{
			DiaryRecordForm exist = new DiaryRecordForm();
			exist.setUserName("??????");
			exist.setCategoryId(1);
			exist.setDiaryDay(Date.valueOf("2022-02-22"));
			exist.setCreateAt(LocalDateTime.parse("2022-02-22T08:39:50"));
			when(diaryRecordService.findOneDiaryRecord("??????", 1, Date.valueOf("2022-02-23"))).thenReturn(exist);
			
			mockMvc.perform(post("/index/record/commit")
		                   .flashAttr("diaryRecordForm", form)
		                   .flashAttr("fileUploadForm", file)
		                   .param("update", "update")
		                   .contentType(MediaType.MULTIPART_FORM_DATA)
		                   .with(SecurityMockMvcRequestPostProcessors.csrf()))
	               .andExpect(status().is2xxSuccessful())
	               .andExpect(model().hasNoErrors())
	               .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
	               .andExpect(model().attribute("message", "??????????????????????????????????????????????????????????????????"))
	               .andExpect(view().name("UserCalendar/Edit"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("??????", 1, Date.valueOf("2022-02-23"));
			verify(diaryRecordService,times(0)).insertDiaryRecord(form);
			verify(fileUploadService,times(0)).fileValid(file);
			verify(fileUploadService,times(0)).fileUpload(any(FileUploadForm.class), any(String.class), any());
		}
		
		@Test
		void updateContent?????????????????????????????????????????????() throws Exception{
			File upFile = new File("src/test/resources/image/aws.jpeg");
			Path path = Paths.get(upFile.getCanonicalPath());
			byte[] bytes = Files.readAllBytes(path);
			MultipartFile multipartFile = new MockMultipartFile("file","aws.jpeg","multipart/form-data",bytes);
			file.setMultipartFile(multipartFile);
			when(fileUploadService.fileValid(file)).thenReturn(false);
			
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
			verify(diaryRecordService,times(1)).findOneDiaryRecord("??????", 1, Date.valueOf("2022-02-23"));
			verify(diaryRecordService,times(0)).insertDiaryRecord(form);
			verify(fileUploadService,times(1)).fileValid(file);
			verify(fileUploadService,times(0)).fileUpload(any(FileUploadForm.class), any(String.class), any());
		}
		
		@Test
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void updateContent????????????????????????????????????() throws Exception{
			mockMvc.perform(post("/index/record/commit")
		                   .flashAttr("diaryRecordForm", form)
		                   .flashAttr("fileUploadForm", file)
		                   .param("update", "update")
		                   .contentType(MediaType.MULTIPART_FORM_DATA)
		                   .with(SecurityMockMvcRequestPostProcessors.csrf()))
				   .andExpect(status().isForbidden())
				   .andExpect(forwardedUrl("/accessdenied"));
		}
		
	}
	
	
	@Nested
	class deleteContent{
		DiaryRecordForm form;
		@BeforeEach
		void setUp() throws Exception{
			form = new DiaryRecordForm("??????",1,Date.valueOf("2022-02-23"),null,"???????????????",null,
                                       null,"?????????????????????????????????",LocalDateTime.parse("2022-02-23T09:43:28"));
			doNothing().when(diaryRecordService).deleteDiaryRecord(form);
		}
		
		@Test
		@WithMockCustomUser(userName="??????",password="sigeSIGE",role="ROLE_USER")
		void deleteContent??????????????????????????????() throws Exception{
			mockMvc.perform(post("/index/record/commit")
	                       .flashAttr("diaryRecordForm", form)
	                       .param("delete", "delete")
	                       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	                       .with(SecurityMockMvcRequestPostProcessors.csrf()))
	               .andExpect(status().is3xxRedirection())
	               .andExpect(redirectedUrl("/index"));
			verify(diaryRecordService,times(1)).deleteDiaryRecord(form);
		}
		
		@Test
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void deleteContent????????????????????????????????????() throws Exception{
			mockMvc.perform(post("/index/record/commit")
	                       .flashAttr("diaryRecordForm", form)
		                    .param("delete", "delete")
		                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		                    .with(SecurityMockMvcRequestPostProcessors.csrf()))
				   .andExpect(status().isForbidden())
				   .andExpect(forwardedUrl("/accessdenied"));
		}
		
	}

}