package com.dining.boyaki.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.PostRecord;
import com.dining.boyaki.model.enums.PostCategory;
import com.dining.boyaki.model.enums.StatusList;
import com.dining.boyaki.model.form.CommentForm;
import com.dining.boyaki.model.form.PostForm;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.model.service.LikesService;
import com.dining.boyaki.model.service.PostService;
import com.dining.boyaki.util.WithMockCustomUser;

@AutoConfigureMockMvc
@AutoConfigureMybatis
@WebMvcTest(controllers = PostController.class,
            includeFilters = @ComponentScan.Filter
                            (type = FilterType.ASSIGNABLE_TYPE,
                             value = {AccountUserDetailsService.class,BeanConfig.class,
            		                  SuccessHandler.class}))
public class PostControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@MockBean
	LikesService likesService;
	
	@MockBean
	PostService postService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
				                 .apply(springSecurity()).build();
	}
	
	@Test
	@WithMockUser(username="????????????",authorities= {"ROLE_USER"})
	void showPostIndex??????????????????????????????????????????() throws Exception{
		mockMvc.perform(get("/index/boyaki"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("postCategory", PostCategory.values()))
		       .andExpect(model().attribute("statusList", StatusList.values()))
		       .andExpect(view().name("Post/PostIndex"));
	}
	
	@Nested
	class showUserProfile{
		@Test
		@WithMockUser(username="????????????",authorities= {"ROLE_USER"})
		void showUserProfile?????????????????????????????????????????????????????????() throws Exception{
			AccountInfo info = new AccountInfo("??????","sigeno","???????????????",0,0,0,165,70,null,null);
			when(postService.findProfile("sigeno")).thenReturn(info);
			mockMvc.perform(get("/index/boyaki/profile/sigeno"))
			       .andExpect(status().is2xxSuccessful())
			       .andExpect(model().attribute("accountInfo", 
	                                             hasProperty("nickName",is("sigeno"))))
			       .andExpect(model().attribute("statusList", StatusList.values()))
			       .andExpect(view().name("Post/Profile"));
			verify(postService,times(1)).findProfile("sigeno");
		}
		
		@Test
		@WithMockUser(username="????????????",authorities= {"ROLE_USER"})
		void showUserProfile?????????????????????????????????????????????404??????????????????() throws Exception{
			when(postService.findProfile("sigeno")).thenReturn(null);
			mockMvc.perform(get("/index/boyaki/profile/sigeno"))
				   .andExpect(status().is2xxSuccessful())
			       .andExpect(model().hasNoErrors())
			       .andExpect(view().name("error/404"));
			verify(postService,times(1)).findProfile("sigeno");
		}
		
		@Test
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void showUserProfile????????????????????????????????????????????????????????????() throws Exception{
			mockMvc.perform(get("/index/boyaki/profile/sigeno"))
				   .andExpect(status().isForbidden())
				   .andExpect(forwardedUrl("/accessdenied"));
		}
	}
	
	@Nested
	class showPostDetail {
		PostRecord record;
		
		@BeforeEach
		void setUp(){
			record = new PostRecord("7","miho","??????","??????????????????","???????????????",
					"??????????????????1??????????????????????????????????????????????????????????????????","2022-03-02 11:12:50");
			when(postService.findOnePostRecord(7)).thenReturn(record);
			when(postService.findOnePostRecord(333)).thenReturn(null);
			when(postService.findNickName("????????????")).thenReturn("????????????");
			when(postService.findNickName("miho")).thenReturn("??????");
			when(likesService.sumRate(7)).thenReturn(4);
			when(likesService.currentRate(7,"????????????")).thenReturn(1);
			when(likesService.currentRate(7,"miho")).thenReturn(-1);
		}
		
		@Test
		@WithMockCustomUser(userName="????????????",password="sun-fla-cis",role="ROLE_USER")
		void showPostDetail????????????????????????????????????????????????????????????????????????() throws Exception{
			mockMvc.perform(get("/index/boyaki/7"))
			       .andExpect(status().is2xxSuccessful())
			       .andExpect(model().attribute("postRecord",
	                                            hasProperty("nickName",is("??????"))))
			       .andExpect(model().attribute("ableDeleted", "false"))
			       .andExpect(model().attribute("commentForm", 
			    		                        hasProperty("userName",is("????????????"))))
			       .andExpect(model().attribute("commentForm", 
	                                            hasProperty("nickName",is("????????????"))))
			       .andExpect(model().attribute("sumRate", 4))
			       .andExpect(model().attribute("currentRate", 1))
			       .andExpect(view().name("Post/PostDetail"));
			verify(postService,times(1)).findOnePostRecord(7);
			verify(postService,times(1)).findNickName("????????????");
			verify(likesService,times(1)).sumRate(7);
			verify(likesService,times(1)).currentRate(7, "????????????");
		}
		
		@Test
		@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
		void showPostDetail?????????????????????????????????????????????????????????????????????() throws Exception{
			mockMvc.perform(get("/index/boyaki/7"))
			       .andExpect(status().is2xxSuccessful())
			       .andExpect(model().attribute("postRecord",
	                                            hasProperty("nickName",is("??????"))))
			       .andExpect(model().attribute("ableDeleted", "true"))
			       .andExpect(model().attribute("commentForm", 
	                                            hasProperty("userName",is("miho"))))
			       .andExpect(model().attribute("commentForm", 
                                                hasProperty("nickName",is("??????"))))
			       .andExpect(model().attribute("sumRate", 4))
			       .andExpect(model().attribute("currentRate", -1))
			       .andExpect(view().name("Post/PostDetail"));
			verify(postService,times(1)).findOnePostRecord(7);
			verify(postService,times(1)).findNickName("miho");
			verify(likesService,times(1)).sumRate(7);
			verify(likesService,times(1)).currentRate(7, "miho");
		}
		
		@Test
		@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
		void showPostDetail???????????????????????????????????????404??????????????????() throws Exception{
			mockMvc.perform(get("/index/boyaki/333"))
			       .andExpect(status().is2xxSuccessful())
			       .andExpect(model().hasNoErrors())
			       .andExpect(view().name("error/404"));
			verify(postService,times(1)).findOnePostRecord(333);
		}
		
		@Test
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void showPostDetail??????????????????????????????????????????() throws Exception{
			mockMvc.perform(get("/index/boyaki/7"))
				   .andExpect(status().isForbidden())
				   .andExpect(forwardedUrl("/accessdenied"));
		}
	}
	
	@Nested
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
    class createComment {
		CommentForm form;
		@BeforeEach
		void setUp() {
			form = new CommentForm(5,"miho","??????","??????????????????");
			doNothing().when(postService).insertComment(form);
		}
		
		@Test
		void insertComment???????????????????????????????????????????????????() throws Exception{
			mockMvc.perform(post("/index/boyaki/comment")
			               .flashAttr("commentForm", form)
				           .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				           .with(SecurityMockMvcRequestPostProcessors.csrf()))
				   .andExpect(status().is3xxRedirection())
				   .andExpect(model().hasNoErrors())
				   .andExpect(redirectedUrl("http://localhost/index/boyaki/5"));
			verify(postService,times(1)).insertComment(form);
		}
		
		@Test
		void insertComment????????????????????????????????????????????????() throws Exception{
			form.setContent("");
			mockMvc.perform(post("/index/boyaki/comment")
			               .flashAttr("commentForm", form)
				           .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				           .with(SecurityMockMvcRequestPostProcessors.csrf()))
				   .andExpect(status().is3xxRedirection())
				   .andExpect(flash().attribute("validMessage", "1???100??????????????????????????????????????????????????????"))
				   .andExpect(redirectedUrl("http://localhost/index/boyaki/5"));
			verify(postService,times(0)).insertComment(form);
		}
		
		@Test
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void insertComment??????????????????????????????????????????????????????() throws Exception{
			mockMvc.perform(post("/index/boyaki/comment")
		               .flashAttr("commentForm", form)
			           .contentType(MediaType.APPLICATION_FORM_URLENCODED)
			           .with(SecurityMockMvcRequestPostProcessors.csrf()))
				   .andExpect(status().isForbidden())
				   .andExpect(forwardedUrl("/accessdenied"));
		}
	}
	
	@Nested
    class updateRate {
		@Test
		@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
		void updateRate??????????????????????????????() throws Exception{
			doNothing().when(likesService).updateRate(7,"miho");
			when(likesService.sumRate(7)).thenReturn(5);
			when(likesService.currentRate(7, "miho")).thenReturn(1);
			
			mockMvc.perform(post("/index/boyaki/rate")
					       .param("postId", "7")
					       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				           .with(SecurityMockMvcRequestPostProcessors.csrf()))
			       .andExpect(status().is2xxSuccessful())
			       .andExpect(model().attribute("sumRate", 5))
			       .andExpect(model().attribute("currentRate", 1))
			       .andExpect(view().name("Post/PostDetail :: rateFragment"));
			verify(likesService,times(1)).updateRate(7, "miho");
			verify(likesService,times(1)).sumRate(7);
			verify(likesService,times(1)).currentRate(7, "miho");
		}
		
		@Test
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void updateRate?????????????????????????????????() throws Exception{
			mockMvc.perform(post("/index/boyaki/rate")
					       .param("postId", "7")
					       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				           .with(SecurityMockMvcRequestPostProcessors.csrf()))
				   .andExpect(status().isForbidden())
				   .andExpect(forwardedUrl("/accessdenied"));
		}
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	void deletePostDetail???????????????????????????() throws Exception{
		doNothing().when(postService).deletePost("miho", 7);
		mockMvc.perform(post("/index/boyaki/post/delete")
				       .param("postId", "7")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
			           .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is3xxRedirection())
		       .andExpect(model().hasNoErrors())
		       .andExpect(redirectedUrl("/index/boyaki"));
		verify(postService,times(1)).deletePost("miho", 7);
	}
	
	@Nested
	class showPostCreate{
		@Test
		@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
		void showPostCreate??????????????????????????????????????????() throws Exception{
			when(postService.findNickName("miho")).thenReturn("??????");
			mockMvc.perform(get("/index/boyaki/post"))
			       .andExpect(status().is2xxSuccessful())
			       .andExpect(model().attribute("postCategory", PostCategory.values()))
			       .andExpect(model().attribute("postForm",
			    		                        hasProperty("userName",is("miho"))))
			       .andExpect(model().attribute("postForm",
	                                            hasProperty("nickName",is("??????"))))
			       .andExpect(view().name("Post/PostCreate"));
			verify(postService,times(1)).findNickName("miho");
		}
		
		@Test
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void showPostCreate?????????????????????????????????????????????() throws Exception{
			mockMvc.perform(get("/index/boyaki/post"))
				   .andExpect(status().isForbidden())
				   .andExpect(forwardedUrl("/accessdenied"));
		}
	}
	
	@Nested
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
    class createPost {
		PostForm form;
		
		@BeforeEach
		void setUp() {
			form = new PostForm("miho","??????","??????????????????????????????????????????????????????",2);
			doNothing().when(postService).insertPost(form);
		}
	
		@Test
		void insertPost?????????????????????1??????????????????() throws Exception{
			mockMvc.perform(post("/index/boyaki/post/insert")
			               .flashAttr("postForm", form)
				           .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				           .with(SecurityMockMvcRequestPostProcessors.csrf()))
				   .andExpect(status().is3xxRedirection())
				   .andExpect(model().hasNoErrors())
				   .andExpect(redirectedUrl("/index/boyaki"));
			verify(postService,times(1)).insertPost(form);
		}
		
		@Test
		void insertPost????????????????????????????????????????????????() throws Exception{
			form.setContent("12345678901234567890123456789012345678901234567890"
					      + "123456789012345678901234567890123456789012345678901");
			mockMvc.perform(post("/index/boyaki/post/insert")
			               .flashAttr("postForm", form)
				           .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				           .with(SecurityMockMvcRequestPostProcessors.csrf()))
				   .andExpect(status().is2xxSuccessful())
				   .andExpect(model().hasErrors())
				   .andExpect(model().attributeHasFieldErrorCode("postForm", "content", "Size"))
				   .andExpect(view().name("Post/PostCreate"));
			verify(postService,times(0)).insertPost(form);
		}
		
		@Test
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void insertPost?????????????????????1?????????????????????() throws Exception{
			mockMvc.perform(post("/index/boyaki/post/insert")
			               .flashAttr("postForm", form)
				           .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				           .with(SecurityMockMvcRequestPostProcessors.csrf()))
				   .andExpect(status().isForbidden())
				   .andExpect(forwardedUrl("/accessdenied"));
		}
	}

}
