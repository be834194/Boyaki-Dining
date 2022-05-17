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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import com.dining.boyaki.model.entity.Comment;
import com.dining.boyaki.model.entity.Post;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.model.service.AdminService;
import com.dining.boyaki.util.WithMockCustomUser;

@AutoConfigureMockMvc
@AutoConfigureMybatis
@WebMvcTest(controllers = AdminController.class,
            includeFilters = @ComponentScan.Filter
                            (type = FilterType.ASSIGNABLE_TYPE,
                             value = {AccountUserDetailsService.class,BeanConfig.class,
            		                  SuccessHandler.class}))
public class AdminControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@MockBean
	AdminService adminService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
				                 .apply(springSecurity()).build();
	}
	
	@Test
	@WithMockCustomUser(userName="admin_account",password="select*fromUser",role="ROLE_ADMIN")
	void showAdminIndexで管理トップ画面が表示される() throws Exception{
		mockMvc.perform(get("/admin"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().hasNoErrors())
		       .andExpect(view().name("Admin/AdminIndex"));
	}
	
	@Test
	@WithMockCustomUser(userName="admin_account",password="select*fromUser",role="ROLE_ADMIN")
	void showConfirmPostで投稿削除画面が表示される() throws Exception{
		Post post = new Post();
    	post.setUserName("糸井");
    	post.setNickName("sigeno");
    	post.setContent("sample");
		when(adminService.findPost(3)).thenReturn(post);
		when(adminService.findPost(30)).thenReturn(null);
		
		mockMvc.perform(post("/admin/post/confrom")
				       .param("postId", "3")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("post", 
		    		                        hasProperty("content",is("sample"))))
		       .andExpect(model().attribute("postId", 3L))
		       .andExpect(view().name("Admin/ConfirmPost"));
		verify(adminService,times(1)).findPost(3);
		
		mockMvc.perform(post("/admin/post/confrom")
				       .param("postId", "30")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().hasNoErrors())
		       .andExpect(view().name("error/404"));
		verify(adminService,times(1)).findPost(30);
	}
	
	@Test
	@WithMockCustomUser(userName="admin_account",password="select*fromUser",role="ROLE_ADMIN")
	void deletePostで投稿が1件削除される() throws Exception{
		doNothing().when(adminService).deletePost(3);
		mockMvc.perform(post("/admin/post/delete")
				       .param("postId", "3")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is3xxRedirection())
		       .andExpect(model().hasNoErrors())
		       .andExpect(redirectedUrl("/admin"));
		verify(adminService,times(1)).deletePost(3);
	}
	
	@Test
	@WithMockCustomUser(userName="admin_account",password="select*fromUser",role="ROLE_ADMIN")
	void showConfirmComemntでコメント削除画面が表示される() throws Exception{
		Comment comment = new Comment();
    	comment.setUserName("加藤健");
    	comment.setNickName("加藤健");
    	comment.setContent("応援してます");
		when(adminService.findComment(4)).thenReturn(comment);
		when(adminService.findComment(40)).thenReturn(null);
		
		mockMvc.perform(post("/admin/comment/confrom")
				       .param("commentId", "4")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("comment", 
		    		                        hasProperty("content",is("応援してます"))))
		       .andExpect(model().attribute("commentId", 4L))
		       .andExpect(view().name("Admin/ConfirmComment"));
		verify(adminService,times(1)).findComment(4);
		
		mockMvc.perform(post("/admin/comment/confrom")
				       .param("commentId", "40")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().hasNoErrors())
		       .andExpect(view().name("error/404"));
		verify(adminService,times(1)).findComment(40);
	}
	
	@Test
	@WithMockCustomUser(userName="admin_account",password="select*fromUser",role="ROLE_ADMIN")
	void deleteCommentでコメントが1件削除される() throws Exception{
		doNothing().when(adminService).deleteComment(4);
		mockMvc.perform(post("/admin/comment/delete")
				       .param("commentId", "4")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is3xxRedirection())
		       .andExpect(model().hasNoErrors())
		       .andExpect(redirectedUrl("/admin"));
		verify(adminService,times(1)).deleteComment(4);
	}

}
