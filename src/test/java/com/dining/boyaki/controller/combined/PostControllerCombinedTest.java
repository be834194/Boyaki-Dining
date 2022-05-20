package com.dining.boyaki.controller.combined;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

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
import com.dining.boyaki.model.enums.PostCategory;
import com.dining.boyaki.model.enums.StatusList;
import com.dining.boyaki.model.form.CommentForm;
import com.dining.boyaki.model.form.PostForm;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.model.service.LikesService;
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
			                		 LikesService.class,PostService.class}))
@Transactional
public class PostControllerCombinedTest {
	
	private static MockedStatic<LocalDateTime> mock;
	
	@Autowired
	private MockMvc mockMvc;
	
	@BeforeEach
	void setUp() {
		mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
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
	void showUserProfileでユーザ一人のプロフィールが表示される() throws Exception{
		mockMvc.perform(get("/index/boyaki/profile/sigeno"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("accountInfo", 
	                                      hasProperty("nickName",is("sigeno"))))
		       .andExpect(model().attribute("statusList", StatusList.values()))
		       .andExpect(view().name("Post/Profile"));
	}
	
	@Test
	@WithMockUser(username="マクベイ",authorities= {"ROLE_USER"})
	@DatabaseSetup(value="/controller/Post/setup/")
	void showUserProfileでユーザ一が見つからない場合は404ページを返す() throws Exception{
		mockMvc.perform(get("/index/boyaki/profile/hogehuga"))
			   .andExpect(status().is2xxSuccessful())
		       .andExpect(model().hasNoErrors())
		       .andExpect(view().name("error/404"));
	}
	
	@Test
	@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
	void showUserProfileでユーザ一人のプロフィールが表示されない() throws Exception{
		mockMvc.perform(get("/index/boyaki/profile/sigeno"))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl("/accessdenied"));
	}
	
	@Test
	@WithMockCustomUser(userName="加藤健",password="pinballs",role="ROLE_USER")
	@DatabaseSetup(value="/controller/Post/setup/")
	void showPostDetailで投稿詳細画面が表示され削除ボタンは表示されない() throws Exception{
		mockMvc.perform(get("/index/boyaki/7"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("postRecord",
                                            hasProperty("nickName",is("匿名"))))
		       .andExpect(model().attribute("ableDeleted", "false"))
		       .andExpect(model().attribute("commentForm", 
				                            hasProperty("userName",is("加藤健"))))
				.andExpect(model().attribute("commentForm", 
				                             hasProperty("nickName",is("加藤健"))))
		       .andExpect(model().attribute("sumRate", 1))
		       .andExpect(model().attribute("currentRate", 1))
		       .andExpect(view().name("Post/PostDetail"));
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/Post/setup/")
	void showPostDetailで投稿詳細画面が表示され削除ボタンが表示される() throws Exception{
		mockMvc.perform(get("/index/boyaki/7"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("postRecord",
                                            hasProperty("nickName",is("匿名"))))
		       .andExpect(model().attribute("ableDeleted", "true"))
		       .andExpect(model().attribute("commentForm", 
				                            hasProperty("userName",is("miho"))))
				.andExpect(model().attribute("commentForm", 
				                             hasProperty("nickName",is("匿名"))))
		       .andExpect(model().attribute("sumRate", 1))
		       .andExpect(model().attribute("currentRate", -1))
		       .andExpect(view().name("Post/PostDetail"));
	}
	
	@Test
	@WithMockUser(username="マクベイ",authorities= {"ROLE_USER"})
	void showPostDetailで投稿が見つからない場合は404ページを返す() throws Exception{
		mockMvc.perform(get("/index/boyaki/333"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(view().name("error/404"));
	}
	
	@Test
	@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
	void showPostDetailで投稿詳細画面が表示されない() throws Exception{
		mockMvc.perform(get("/index/boyaki/7"))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl("/accessdenied"));
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/Post/setup/")
	@ExpectedDatabase(value = "/controller/Post/insert/comment/",table="comment"
			         ,assertionMode=DatabaseAssertionMode.NON_STRICT)
	void insertCommentで投稿に対してコメントが追加される() throws Exception{
		LocalDateTime datetime = LocalDateTime.parse("2022-03-10T16:27:38");
		mock.when(LocalDateTime::now).thenReturn(datetime);
		CommentForm form = new CommentForm(9,"miho","匿名","牛乳私も試してみます！");
		mockMvc.perform(post("/index/boyaki/comment")
		               .flashAttr("commentForm", form)
			           .contentType(MediaType.APPLICATION_FORM_URLENCODED)
			           .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().is3xxRedirection())
			   .andExpect(model().hasNoErrors())
			   .andExpect(redirectedUrl("http://localhost/index/boyaki/9"));
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/Post/setup/")
	void insertCommentでバリデーションエラーが発生する() throws Exception{
		CommentForm form = new CommentForm(9,"miho","匿名","");
		mockMvc.perform(post("/index/boyaki/comment")
		               .flashAttr("commentForm", form)
			           .contentType(MediaType.APPLICATION_FORM_URLENCODED)
			           .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().is3xxRedirection())
			   .andExpect(flash().attribute("validMessage", "1～100文字以内でコメントを入力してください"))
			   .andExpect(redirectedUrl("http://localhost/index/boyaki/9"));
	}
	
	@Test
	@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
	void insertCommentで投稿に対してコメントが追加されない() throws Exception{
		CommentForm form = new CommentForm();
		mockMvc.perform(post("/index/boyaki/comment")
	               .flashAttr("commentForm", form)
		           .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		           .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl("/accessdenied"));
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/Post/setup/")
	@ExpectedDatabase(value = "/controller/Post/likes/",table="likes")
	void updateRateでいいねが更新される() throws Exception{
		mockMvc.perform(post("/index/boyaki/rate") //1をinsert
				       .param("postId", "7")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
			           .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("sumRate", 2))
		       .andExpect(model().attribute("currentRate", 1))
		       .andExpect(view().name("Post/PostDetail :: rateFragment"));
		mockMvc.perform(post("/index/boyaki/rate") //0にupdate
				       .param("postId", "2")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
			           .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("sumRate", 1))
		       .andExpect(model().attribute("currentRate", 0));
		mockMvc.perform(post("/index/boyaki/rate") //1にupdate
				       .param("postId", "1")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
			           .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("sumRate", 1))
		       .andExpect(model().attribute("currentRate", 1));
	}
	
	@Test
	@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
	void updateRateでいいねが更新されない() throws Exception{
		mockMvc.perform(post("/index/boyaki/rate")
				       .param("postId", "7")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
			           .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl("/accessdenied"));
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/Post/setup/")
	@ExpectedDatabase(value = "/controller/Post/delete/")
	void deletePostDetailで投稿が削除される() throws Exception{
		mockMvc.perform(post("/index/boyaki/post/delete")
				       .param("postId", "7")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
			           .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is3xxRedirection())
		       .andExpect(model().hasNoErrors())
		       .andExpect(redirectedUrl("/index/boyaki"));
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
	@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
	void showPostCreateでぼやき投稿画面が表示されない() throws Exception{
		mockMvc.perform(get("/index/boyaki/post"))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl("/accessdenied"));
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/Post/setup/")
	@ExpectedDatabase(value = "/controller/Post/insert/post/",table="post"
			         ,assertionMode=DatabaseAssertionMode.NON_STRICT)
	void insertPostでぼやき投稿が1件追加される() throws Exception{
        LocalDateTime datetime = LocalDateTime.parse("2022-03-08T09:31:12");
		mock.when(LocalDateTime::now).thenReturn(datetime);
		
		PostForm form = new PostForm("miho","匿名","糖質制限ってどこまでやればいいの～？",2);
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
		PostForm form = new PostForm("miho","匿名",null,2);
		form.setContent("12345678901234567890123456789012345678901234567890"
				      + "123456789012345678901234567890123456789012345678901");
		this.mockMvc.perform(post("/index/boyaki/post/insert")
				            .flashAttr("postForm", form)
				            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				            .with(SecurityMockMvcRequestPostProcessors.csrf()))
		            .andExpect(status().is2xxSuccessful())
		            .andExpect(model().attributeHasFieldErrorCode("postForm", "content", "Size"))
					.andExpect(view().name("Post/PostCreate"));
	}
	
	@Test
	@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
	void insertPostでぼやき投稿が1件追加されない() throws Exception{
		PostForm form = new PostForm();
		mockMvc.perform(post("/index/boyaki/post/insert")
		               .flashAttr("postForm", form)
			           .contentType(MediaType.APPLICATION_FORM_URLENCODED)
			           .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl("/accessdenied"));
	}

}
