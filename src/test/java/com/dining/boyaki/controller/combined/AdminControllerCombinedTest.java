package com.dining.boyaki.controller.combined;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import com.dining.boyaki.controller.AdminController;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.model.service.AdminService;
import com.dining.boyaki.util.CsvDataSetLoader;
import com.dining.boyaki.util.WithMockCustomUser;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

@AutoConfigureMockMvc
@AutoConfigureMybatis
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class,
	                     WithSecurityContextTestExecutionListener.class})
@WebMvcTest(controllers = AdminController.class,
			includeFilters = @ComponentScan.Filter
			                (type = FilterType.ASSIGNABLE_TYPE,
			                 value = {AccountUserDetailsService.class,BeanConfig.class,SuccessHandler.class,
			                		 AdminService.class}))
@Transactional
public class AdminControllerCombinedTest {
	
	@Autowired
	private MockMvc mockMvc;
	
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
	@DatabaseSetup(value="/controller/Admin/setup/")
	void showConfirmPostで投稿削除画面が表示される() throws Exception{
		mockMvc.perform(post("/admin/post/confrom")
				       .param("postId", "3")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("post", 
		    		                        hasProperty("content",is("ドーナツは穴が開いてるからゼロカロリーって本当？"))))
		       .andExpect(model().attribute("postId", 3L))
		       .andExpect(view().name("Admin/ConfirmPost"));
		
		mockMvc.perform(post("/admin/post/confrom")
				       .param("postId", "30")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().hasNoErrors())
		       .andExpect(view().name("error/404"));
	}
	
	@Test
	@WithMockCustomUser(userName="admin_account",password="select*fromUser",role="ROLE_ADMIN")
	@DatabaseSetup(value="/controller/Admin/setup/")
	@ExpectedDatabase(value="/controller/Admin/delete/post/",table="post")
	void deletePostで投稿が1件削除される() throws Exception{
		mockMvc.perform(post("/admin/post/delete")
				       .param("postId", "3")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is3xxRedirection())
		       .andExpect(model().hasNoErrors())
		       .andExpect(redirectedUrl("/admin"));
	}
	
	@Test
	@WithMockCustomUser(userName="admin_account",password="select*fromUser",role="ROLE_ADMIN")
	@DatabaseSetup(value="/controller/Admin/setup/")
	void showConfirmComemntでコメント削除画面が表示される() throws Exception{
		mockMvc.perform(post("/admin/comment/confrom")
				       .param("commentId", "4")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("comment", 
		    		                        hasProperty("content",is("応援してます"))))
		       .andExpect(model().attribute("commentId", 4L))
		       .andExpect(view().name("Admin/ConfirmComment"));
		
		mockMvc.perform(post("/admin/comment/confrom")
				       .param("commentId", "40")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().hasNoErrors())
		       .andExpect(view().name("error/404"));
	}
	
	@Test
	@WithMockCustomUser(userName="admin_account",password="select*fromUser",role="ROLE_ADMIN")
	@DatabaseSetup(value="/controller/Admin/setup/")
	@ExpectedDatabase(value="/controller/Admin/delete/comment/",table="comment")
	void deleteCommentでコメントが1件削除される() throws Exception{
		mockMvc.perform(post("/admin/comment/delete")
				       .param("commentId", "4")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is3xxRedirection())
		       .andExpect(model().hasNoErrors())
		       .andExpect(redirectedUrl("/admin"));
	}
}
