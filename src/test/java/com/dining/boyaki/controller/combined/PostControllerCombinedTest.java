package com.dining.boyaki.controller.combined;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import com.dining.boyaki.controller.PostController;
import com.dining.boyaki.model.entity.PostCategory;
import com.dining.boyaki.model.entity.StatusList;
import com.dining.boyaki.model.form.PostForm;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.model.service.PostService;
import com.dining.boyaki.util.CsvDataSetLoader;
import com.dining.boyaki.util.WithMockCustomUser;

@AutoConfigureMockMvc
@AutoConfigureMybatis
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class,
	                     WithSecurityContextTestExecutionListener.class})
@WebMvcTest(controllers = PostController.class,
			includeFilters = @ComponentScan.Filter
			                (type = FilterType.ASSIGNABLE_TYPE,
			                 value = {AccountUserDetailsService.class,BeanConfig.class,SuccessHandler.class,
			                		  PostService.class}))
@Transactional
public class PostControllerCombinedTest {
	
	private static LocalDateTime datetime = LocalDateTime.parse("2022-03-08T09:31:12");
	
	private static MockedStatic<LocalDateTime> mock;
	
	@Autowired
	private MockMvc mockMvc;
	
	@BeforeEach
	void setUp() {
		mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
		mock.when(LocalDateTime::now).thenReturn(datetime);
	}
	
	@AfterEach
    void tearDown() throws Exception {
        mock.close();
	}
	
	@Test
	@WithMockUser(username="マクベイ",authorities= {"ROLE_USER"})
	void showPostIndexでぼやき一覧画面が表示される() throws Exception{
		this.mockMvc.perform(get("/index/boyaki"))
			        .andExpect(status().is2xxSuccessful())
			        .andExpect(model().attribute("postCategory", PostCategory.values()))
			        .andExpect(model().attribute("statusList", StatusList.values()))
			        .andExpect(view().name("Post/PostIndex"));
	}
	
	@Test
	@WithMockUser(username="マクベイ",authorities= {"ROLE_USER"})
	@DatabaseSetup(value="/controller/Post/setup/")
	void showProfileでプロフィール画面が表示される() throws Exception{
		this.mockMvc.perform(get("/index/boyaki/sigeno"))
			        .andExpect(status().is2xxSuccessful())
			        .andExpect(model().attribute("accountInfo",
		                                      hasProperty("nickName",is("sigeno"))))
			        .andExpect(model().attribute("statusList", StatusList.values()))
			        .andExpect(view().name("Post/Profile"));
	}
	
	@Test
	@WithMockUser(username="マクベイ",authorities= {"ROLE_USER"})
	void showProfileでプロフィールが見つからない場合は404ページを返す() throws Exception{
		this.mockMvc.perform(get("/index/boyaki/hoge"))
				    .andExpect(status().is2xxSuccessful())
				    .andExpect(view().name("Common/404"));
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/Post/setup/")
	void showPostCreateでぼやき投稿画面が表示される() throws Exception{
		this.mockMvc.perform(get("/index/boyaki/post"))
		       		.andExpect(status().is2xxSuccessful())
			        .andExpect(model().attribute("postCategory", PostCategory.values()))
			        .andExpect(model().attribute("postForm",
			    	    	                     hasProperty("userName",is("miho"))))
			        .andExpect(model().attribute("postForm",
		                                         hasProperty("nickName",is("匿名"))))
			        .andExpect(view().name("Post/PostCreate"));
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/Post/setup/")
	@ExpectedDatabase(value = "/controller/Post/insert/",table="post")
	void insertPostでぼやき投稿が1件追加される() throws Exception{
		PostForm form = new PostForm();
		form.setUserName("miho");
		form.setNickName("匿名");
		form.setContent("糖質制限ってどこまでやればいいの～？");
		form.setPostCategory(2);
		this.mockMvc.perform(post("/index/boyaki/post/insert")
			                .flashAttr("postForm", form)
				            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				           . with(SecurityMockMvcRequestPostProcessors.csrf()))
				    .andExpect(status().is3xxRedirection())
				    .andExpect(model().hasNoErrors())
				    .andExpect(redirectedUrl("/index/boyaki"));
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_Nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/Post/setup/")
	void insertPostでバリデーションエラーが発生する() throws Exception{
		PostForm form = new PostForm();
		form.setUserName("miho");
		form.setNickName("匿名");
		form.setContent("12345678901234567890123456789012345678901234567890"
				      + "123456789012345678901234567890123456789012345678901");
		form.setPostCategory(2);
		this.mockMvc.perform(post("/index/boyaki/post/insert")
				            .flashAttr("postForm", form)
				            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				            .with(SecurityMockMvcRequestPostProcessors.csrf()))
		            .andExpect(status().is2xxSuccessful())
		            .andExpect(model().attributeHasFieldErrorCode("postForm", "content", "Size"))
					.andExpect(view().name("Post/PostCreate"));
	}

}
