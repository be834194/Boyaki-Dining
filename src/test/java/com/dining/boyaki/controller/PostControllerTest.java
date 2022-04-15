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
import com.dining.boyaki.model.entity.PostCategory;
import com.dining.boyaki.model.entity.PostRecord;
import com.dining.boyaki.model.entity.StatusList;
import com.dining.boyaki.model.form.CommentForm;
import com.dining.boyaki.model.form.PostForm;
import com.dining.boyaki.model.service.AccountUserDetailsService;
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
	PostService postService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
				                 .apply(springSecurity()).build();
	}
	
	@Test
	@WithMockUser(username="マクベイ",authorities= {"ROLE_USER"})
	void showPostIndexでぼやき一覧画面が表示される() throws Exception{
		mockMvc.perform(get("/index/boyaki"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("postCategory", PostCategory.values()))
		       .andExpect(model().attribute("statusList", StatusList.values()))
		       .andExpect(view().name("Post/PostIndex"));
	}
	
	@Nested
	class showPostDetail {
		PostRecord record;
		
		@BeforeEach
		void setUp(){
			record = new PostRecord("7","miho","匿名","ダイエット中","ダイエット",
					"先月から体重1キロ落ち増した！今月もダイエット頑張るぞ！！","2022-03-02 11:12:50");
			when(postService.findOnePostRecord(7)).thenReturn(record);
			when(postService.findOnePostRecord(333)).thenReturn(null);
			when(postService.findNickName("マクベイ")).thenReturn("マッキー");
			when(postService.findNickName("miho")).thenReturn("匿名");
			when(postService.sumRate(7)).thenReturn(4);
		}
		
		@Test
		@WithMockCustomUser(userName="マクベイ",password="sun-fla-cis",role="ROLE_USER")
		void showPostDetailで投稿詳細画面が表示され削除ボタンは表示されない() throws Exception{
			mockMvc.perform(get("/index/boyaki/7"))
			       .andExpect(status().is2xxSuccessful())
			       .andExpect(model().attribute("postRecord",
	                                            hasProperty("nickName",is("匿名"))))
			       .andExpect(model().attribute("ableDeleted", "false"))
			       .andExpect(model().attribute("commentForm", 
			    		                        hasProperty("userName",is("マクベイ"))))
			       .andExpect(model().attribute("commentForm", 
	                                            hasProperty("nickName",is("マッキー"))))
			       .andExpect(model().attribute("sumRate", 4))
			       .andExpect(view().name("Post/PostDetail"));
			verify(postService,times(1)).findOnePostRecord(7);
			verify(postService,times(1)).sumRate(7);
		}
		
		@Test
		@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
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
			       .andExpect(model().attribute("sumRate", 4))
			       .andExpect(view().name("Post/PostDetail"));
			verify(postService,times(1)).findOnePostRecord(7);
			verify(postService,times(1)).sumRate(7);
		}
		
		@Test
		@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
		void showPostDetailで投稿が見つからない場合は404ページを返す() throws Exception{
			mockMvc.perform(get("/index/boyaki/333"))
			       .andExpect(status().is2xxSuccessful())
			       .andExpect(model().hasNoErrors())
			       .andExpect(view().name("error/404"));
			verify(postService,times(1)).findOnePostRecord(333);
		}
	}
	
	@Nested
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
    class createComment {
		
		CommentForm form;
		
		@BeforeEach
		void setUp() {
			form = new CommentForm(5,"miho","匿名","応援してます");
			doNothing().when(postService).insertComment(form);
		}
		
		@Test
		void insertCommentで投稿に対してコメントが追加される() throws Exception{
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
		void insertCommentでバリデーションエラーが発生する() throws Exception{
			form.setContent("");
			mockMvc.perform(post("/index/boyaki/comment")
			               .flashAttr("commentForm", form)
				           .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				           .with(SecurityMockMvcRequestPostProcessors.csrf()))
				   .andExpect(status().is3xxRedirection())
				   .andExpect(flash().attribute("validMessage", "1～100文字以内でコメントを入力してください"))
				   .andExpect(redirectedUrl("http://localhost/index/boyaki/5"));
			verify(postService,times(0)).insertComment(form);
		}
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	void updateRateでいいねが更新される() throws Exception{
		doNothing().when(postService).updateRate(7,"miho");
		when(postService.sumRate(7)).thenReturn(5);
		
		mockMvc.perform(post("/index/boyaki/rate")
				       .param("postId", "7")
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
			           .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("sumRate", 5))
		       .andExpect(view().name("Post/PostDetail :: rateFragment"));
		verify(postService,times(1)).updateRate(7, "miho");
		verify(postService,times(1)).sumRate(7);
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	void deletePostDetailで投稿が削除される() throws Exception{
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
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	void showPostCreateでぼやき投稿画面が表示される() throws Exception{
		when(postService.findNickName("miho")).thenReturn("匿名");
		mockMvc.perform(get("/index/boyaki/post"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("postCategory", PostCategory.values()))
		       .andExpect(model().attribute("postForm",
		    		                        hasProperty("userName",is("miho"))))
		       .andExpect(model().attribute("postForm",
                                            hasProperty("nickName",is("匿名"))))
		       .andExpect(view().name("Post/PostCreate"));
		verify(postService,times(1)).findNickName("miho");
	}
	
	@Nested
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
    class createPost {
		
		PostForm form = new PostForm();
		
		@BeforeEach
		void setUp() {
			form.setUserName("miho");
			form.setNickName("匿名");
			form.setContent("糖質制限ってどこまでやればいいの～？");
			form.setPostCategory(2);
			doNothing().when(postService).insertPost(form);
		}
	
		@Test
		void insertPostでぼやき投稿が1件追加される() throws Exception{
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
		void insertPostでバリデーションエラーが発生する() throws Exception{
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
	}

}
