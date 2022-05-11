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

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
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

import com.dining.boyaki.config.BeanConfig;
import com.dining.boyaki.config.SuccessHandler;
import com.dining.boyaki.model.entity.DiaryRecordCategory;
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
	@WithMockUser(username="マクベイ",authorities= {"ROLE_USER"})
	void showUserCalendarでユーザトップ画面が表示される() throws Exception{
		mockMvc.perform(get("/index"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(view().name("UserCalendar/index"));
	}
	
	@Nested
	class showCreateContent {
		@Test
		@WithMockUser(username="マクベイ",authorities= {"ROLE_USER"})
		void showCreateContentで食事投稿記録画面が表示される() throws Exception{
			mockMvc.perform(get("/index/create"))
			       .andExpect(status().is2xxSuccessful())
			       .andExpect(model().attributeExists("diaryRecordForm"))
			       .andExpect(model().attributeExists("fileUploadForm"))
			       .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
		           .andExpect(view().name("UserCalendar/Create"));
		}
		
		@Test
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void showCreateContentで食事投稿記録画面が表示されない() throws Exception{
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
			MultipartFile multipartFile = new MockMultipartFile("file","".getBytes());
			file.setMultipartFile(multipartFile);
			form = new DiaryRecordForm(null,4,Date.valueOf("2022-02-19"),
					                   "ハイボール一缶",null,"スクワット20回",
					                   null,null,null);
			doNothing().when(diaryRecordService).insertDiaryRecord(form);
			when(diaryRecordService.findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-19"))).thenReturn(null);
			when(fileUploadService.fileValid(file)).thenReturn(true);
			when(fileUploadService.fileUpload(any(FileUploadForm.class), any(String.class), any()))
			                      .thenReturn("2022-02-19 22-19-37.jpg");
        }
		
		@Test
		void createContentで食事記録が登録される() throws Exception{
			//画像無し
			mockMvc.perform(post("/index/create/insert")
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
			
			//画像有り
			File upFile = new File("src/test/resources/image/3840_2160.jpg");
			Path path = Paths.get(upFile.getCanonicalPath());
			byte[] bytes = Files.readAllBytes(path);
			MultipartFile multipartFile = new MockMultipartFile("file","3840_2160.jpg","multipart/form-data",bytes);
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
		void createContentでバリデーションエラーが発生する() throws Exception{
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
		void createContentでレコード重複エラーが発生する() throws Exception{
			when(diaryRecordService.findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-19"))).thenReturn(form);
			
			mockMvc.perform(post("/index/create/insert")
						   .flashAttr("diaryRecordForm", form)
						   .flashAttr("fileUploadForm", file)
		                   .contentType(MediaType.MULTIPART_FORM_DATA)
		                   .with(SecurityMockMvcRequestPostProcessors.csrf()))
                   .andExpect(status().is2xxSuccessful())
                   .andExpect(model().hasNoErrors())
                   .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
                   .andExpect(model().attribute("message", "既に同じカテゴリ、同じ日付で登録されています"))
                   .andExpect(view().name("UserCalendar/Create"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-19"));
			verify(diaryRecordService,times(0)).insertDiaryRecord(form);
			verify(fileUploadService,times(0)).fileValid(file);
			verify(fileUploadService,times(0)).fileUpload(any(FileUploadForm.class), any(String.class), any());
		}
		
		@Test
		void createContentでファイル形式エラーが発生する() throws Exception{
			File upFile = new File("src/test/resources/image/aws.jpeg");
			Path path = Paths.get(upFile.getCanonicalPath());
			byte[] bytes = Files.readAllBytes(path);
			MultipartFile multipartFile = new MockMultipartFile("file","aws.jpeg","multipart/form-data",bytes);
			file.setMultipartFile(multipartFile);
			when(fileUploadService.fileValid(file)).thenReturn(false);
			
			mockMvc.perform(post("/index/create/insert")
						   .flashAttr("diaryRecordForm", form)
						   .flashAttr("fileUploadForm", file)
		                   .contentType(MediaType.MULTIPART_FORM_DATA)
		                   .with(SecurityMockMvcRequestPostProcessors.csrf()))
                   .andExpect(status().is2xxSuccessful())
                   .andExpect(model().hasNoErrors())
                   .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
                   .andExpect(model().attribute("message", "ファイル形式が不正です"))
                   .andExpect(view().name("UserCalendar/Create"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-19"));
			verify(diaryRecordService,times(0)).insertDiaryRecord(form);
			verify(fileUploadService,times(1)).fileValid(file);
			verify(fileUploadService,times(0)).fileUpload(any(FileUploadForm.class), any(String.class), any());
		}
		
		@Test
		void fileValidでIOExceptionが発生する() throws Exception{
			File upFile = new File("src/test/resources/image/aws.jpeg");
			Path path = Paths.get(upFile.getCanonicalPath());
			byte[] bytes = Files.readAllBytes(path);
			MultipartFile multipartFile = new MockMultipartFile("file","aws.jpeg","multipart/form-data",bytes);
			file.setMultipartFile(multipartFile);
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
		void fileUploadでIOExceptionが発生する() throws Exception{
			File upFile = new File("src/test/resources/image/aws.jpeg");
			Path path = Paths.get(upFile.getCanonicalPath());
			byte[] bytes = Files.readAllBytes(path);
			MultipartFile multipartFile = new MockMultipartFile("file","aws.jpeg","multipart/form-data",bytes);
			file.setMultipartFile(multipartFile);
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
		void fileUploadでAmazonServiceExceptionが発生する() throws Exception{
			File upFile = new File("src/test/resources/image/aws.jpeg");
			Path path = Paths.get(upFile.getCanonicalPath());
			byte[] bytes = Files.readAllBytes(path);
			MultipartFile multipartFile = new MockMultipartFile("file","aws.jpeg","multipart/form-data",bytes);
			file.setMultipartFile(multipartFile);
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
		void fileUploadでImageWriteExceptionが発生する() throws Exception{
			File upFile = new File("src/test/resources/image/aws.jpeg");
			Path path = Paths.get(upFile.getCanonicalPath());
			byte[] bytes = Files.readAllBytes(path);
			MultipartFile multipartFile = new MockMultipartFile("file","aws.jpeg","multipart/form-data",bytes);
			file.setMultipartFile(multipartFile);
			doThrow(new ImageWriteException("")).when(fileUploadService).fileUpload(any(FileUploadForm.class),any(String.class), any());
			
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
		void fileUploadでImageReadExceptionが発生する() throws Exception{
			File upFile = new File("src/test/resources/image/aws.jpeg");
			Path path = Paths.get(upFile.getCanonicalPath());
			byte[] bytes = Files.readAllBytes(path);
			MultipartFile multipartFile = new MockMultipartFile("file","aws.jpeg","multipart/form-data",bytes);
			file.setMultipartFile(multipartFile);
			doThrow(new ImageReadException("")).when(fileUploadService).fileUpload(any(FileUploadForm.class),any(String.class), any());
			
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
		void createContentで食事記録が登録されない() throws Exception{
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
	@WithMockCustomUser(userName="糸井",password="sigeSIGE",role="ROLE_USER")
	class showUserEditContent{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		DiaryRecordForm form;
		FileUploadForm file;
		@BeforeEach
        void setUp() throws Exception{
			form  = new DiaryRecordForm("糸井",1,Date.valueOf("2022-02-23"),
					                   null,"グラノーラ",null,
					                   null,null,LocalDateTime.parse("2022-02-23T09:43:28"));
		}
	
		@Test
		void showUserEditContentで食事記録編集画面へ遷移する() throws Exception{
			//画像無し
			when(diaryRecordService.findOneDiaryRecord("糸井", 1, format.parse("2022-02-23"))).thenReturn(form);
			when(fileUploadService.fileDownload(any(String.class), any(String.class))).thenReturn("hogehoge.jpg");
			mockMvc.perform(get("/index/record/2022-02-23/1"))
		           .andExpect(status().is2xxSuccessful())
		           .andExpect(model().attribute("diaryRecordForm"
		        		                       ,hasProperty("createAt",is(LocalDateTime.parse("2022-02-23T09:43:28"))))
		    		          )
		           .andExpect(model().attributeExists("fileUploadForm"))
		           .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
		           .andExpect(model().attribute("exist", false))
		           .andExpect(view().name("UserCalendar/Edit"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("糸井", 1, Date.valueOf("2022-02-23"));
			verify(fileUploadService,times(0)).fileDownload(any(String.class), any(String.class));
			
			//画像有り
			form.setImageName("hogehoge.jpg");
			mockMvc.perform(get("/index/record/2022-02-23/1"))
		           .andExpect(status().is2xxSuccessful())
		           .andExpect(model().attribute("diaryRecordForm"
		        		                       ,hasProperty("createAt",is(LocalDateTime.parse("2022-02-23T09:43:28"))))
		    		          )
		           .andExpect(model().attributeExists("fileUploadForm"))
		           .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
		           .andExpect(model().attribute("exist", true))
		           .andExpect(model().attributeExists("image"))
		           .andExpect(view().name("UserCalendar/Edit"));
			verify(diaryRecordService,times(2)).findOneDiaryRecord("糸井", 1, Date.valueOf("2022-02-23"));
			verify(fileUploadService,times(1)).fileDownload(any(String.class), any(String.class));
		}
		
		@Test
		void showUserEditContentでレコードが見つからない場合は404ページへ遷移する() throws Exception{
			when(diaryRecordService.findOneDiaryRecord("糸井", 1, format.parse("2022-02-23"))).thenReturn(null);
			
			mockMvc.perform(get("/index/record/2022-02-23/1"))
	           	   .andExpect(status().is2xxSuccessful())
	               .andExpect(view().name("error/404"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("糸井", 1, Date.valueOf("2022-02-23"));
		}
		
		@Test
		void showUserEditContentでParseExceptionかNumberFormatExceptionが発生した場合は404ページへ遷移する() throws Exception{
			mockMvc.perform(get("/index/record/2022-ab-cd/1"))
        	       .andExpect(status().is2xxSuccessful())
                   .andExpect(view().name("error/404"));
			mockMvc.perform(get("/index/record/2022-02-23/Number"))
 	       		   .andExpect(status().is2xxSuccessful())
 	       		   .andExpect(view().name("error/404"));
		}
		
		@Test
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void showUserEditContentで食事記録編集画面へ遷移しない() throws Exception{
			mockMvc.perform(get("/index/record/2022-02-27/1"))
				   .andExpect(status().isForbidden())
				   .andExpect(forwardedUrl("/accessdenied"));
		}
	}
	
	@Nested
	@WithMockCustomUser(userName="糸井",password="sigeSIGE",role="ROLE_USER")
    class updateContent {
		DiaryRecordForm form;
		FileUploadForm file;
		@BeforeEach
		void setUp() throws Exception{
			file = new FileUploadForm();
			MultipartFile multipartFile = new MockMultipartFile("file","".getBytes());
			file.setMultipartFile(multipartFile);
			form = new DiaryRecordForm("糸井",1,Date.valueOf("2022-02-23"),
					                   null,"グラノーラ",null,
					                   null,"自販機でコーヒー買った",LocalDateTime.parse("2022-02-23T09:43:28"));
			doNothing().when(diaryRecordService).insertDiaryRecord(form);
			when(diaryRecordService.findOneDiaryRecord("糸井", 1, Date.valueOf("2022-02-23"))).thenReturn(form);
			when(fileUploadService.fileValid(file)).thenReturn(true);
			when(fileUploadService.fileUpload(any(FileUploadForm.class), any(String.class), any()))
			                      .thenReturn("isorepublic-breakfast-table-1.jpg");
		}
		
		@Test
		void updateContentで食事記録を更新する() throws Exception{
			//画像無し
			mockMvc.perform(post("/index/record/commit")
					       .flashAttr("diaryRecordForm", form)
					       .flashAttr("fileUploadForm", file)
					       .param("update", "update")
					       .contentType(MediaType.MULTIPART_FORM_DATA)
					       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		           .andExpect(status().is3xxRedirection())
		           .andExpect(model().hasNoErrors())
		           .andExpect(redirectedUrl("/index"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("糸井", 1, Date.valueOf("2022-02-23"));
			verify(diaryRecordService,times(1)).updateDiaryRecord(form);
			verify(fileUploadService,times(0)).fileValid(file);
			verify(fileUploadService,times(0)).fileUpload(any(FileUploadForm.class), any(String.class), any());
			
			//画像有り
			File upFile = new File("src/test/resources/image/isorepublic-breakfast-table-1.jpg");
			Path path = Paths.get(upFile.getCanonicalPath());
			byte[] bytes = Files.readAllBytes(path);
			MultipartFile multipartFile = new MockMultipartFile("file","isorepublic-breakfast-table-1.jpg","multipart/form-data",bytes);
			file.setMultipartFile(multipartFile);
			mockMvc.perform(post("/index/record/commit")
					       .flashAttr("diaryRecordForm", form)
					       .flashAttr("fileUploadForm", file)
					       .param("update", "update")
					       .contentType(MediaType.MULTIPART_FORM_DATA)
					       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		           .andExpect(status().is3xxRedirection())
		           .andExpect(model().hasNoErrors())
		           .andExpect(redirectedUrl("/index"));
			verify(diaryRecordService,times(2)).findOneDiaryRecord("糸井", 1, Date.valueOf("2022-02-23"));
			verify(diaryRecordService,times(2)).updateDiaryRecord(form);
			verify(fileUploadService,times(1)).fileValid(file);
			verify(fileUploadService,times(1)).fileUpload(any(FileUploadForm.class), any(String.class), any());
		}
	
		@Test
		void updateContentでバリデーションエラーが発生する() throws Exception{
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
		                   .flashAttr("fileUploadForm", file)
		                   .param("update", "update")
		                   .contentType(MediaType.MULTIPART_FORM_DATA)
		                   .with(SecurityMockMvcRequestPostProcessors.csrf()))
	               .andExpect(status().is2xxSuccessful())
	               .andExpect(model().hasNoErrors())
	               .andExpect(model().attribute("lists", DiaryRecordCategory.values()))
	               .andExpect(model().attribute("message", "既に同じカテゴリ、同じ日付で登録されています"))
	               .andExpect(view().name("UserCalendar/Edit"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("糸井", 1, Date.valueOf("2022-02-23"));
			verify(diaryRecordService,times(0)).insertDiaryRecord(form);
			verify(fileUploadService,times(0)).fileValid(file);
			verify(fileUploadService,times(0)).fileUpload(any(FileUploadForm.class), any(String.class), any());
		}
		
		@Test
		void updateContentでファイル形式エラーが発生する() throws Exception{
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
		            .andExpect(model().attribute("message", "ファイル形式が不正です"))
		            .andExpect(view().name("UserCalendar/Edit"));
			verify(diaryRecordService,times(1)).findOneDiaryRecord("糸井", 1, Date.valueOf("2022-02-23"));
			verify(diaryRecordService,times(0)).insertDiaryRecord(form);
			verify(fileUploadService,times(1)).fileValid(file);
			verify(fileUploadService,times(0)).fileUpload(any(FileUploadForm.class), any(String.class), any());
		}
		
		@Test
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void updateContentで食事記録が更新されない() throws Exception{
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
			form = new DiaryRecordForm("糸井",1,Date.valueOf("2022-02-23"),
                    null,"グラノーラ",null,
                    null,"自販機でコーヒー買った",LocalDateTime.parse("2022-02-23T09:43:28"));
			doNothing().when(diaryRecordService).deleteDiaryRecord(form);
		}
		
		@Test
		@WithMockCustomUser(userName="糸井",password="sigeSIGE",role="ROLE_USER")
		void deleteContentで食事記録を削除する() throws Exception{
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
		void deleteContentで食事記録が削除されない() throws Exception{
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