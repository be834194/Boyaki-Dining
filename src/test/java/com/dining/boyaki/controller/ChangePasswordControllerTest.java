package com.dining.boyaki.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.mockito.Mockito.any;
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
import com.dining.boyaki.model.form.PasswordChangeForm;
import com.dining.boyaki.model.form.validation.NotReusedPasswordValidator;
import com.dining.boyaki.model.form.validation.OldPasswordValidator;
import com.dining.boyaki.model.service.AccountInfoService;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.util.WithMockCustomUser;

@AutoConfigureMockMvc
@AutoConfigureMybatis
@WebMvcTest(controllers = ChangePasswordController.class,
            includeFilters = @ComponentScan.Filter
                            (type = FilterType.ASSIGNABLE_TYPE,
                             value = {AccountUserDetailsService.class,BeanConfig.class,
            		                  SuccessHandler.class}))
public class ChangePasswordControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@MockBean
	AccountInfoService accountInfoService;
	
	@MockBean
	NotReusedPasswordValidator notReusedPasswordValidator;
	
	@MockBean
	OldPasswordValidator oldPasswordValidator;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
				                 .apply(springSecurity()).build();
		when(notReusedPasswordValidator.supports(any())).thenReturn(true);
		when(oldPasswordValidator.supports(any())).thenReturn(true);
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	void showChangePasswordPageでパスワード変更画面が表示される() throws Exception{
		mockMvc.perform(get("/index/mypage/changepassword"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("PasswordChangeForm",
		    		                        hasProperty("userName",is("miho"))))
		       .andExpect(view().name("MyPage/ChangePassword"));
	}
	
	@Test
	@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
	void showChangePasswordPageでパスワード変更画面が表示されない() throws Exception{
		mockMvc.perform(get("/index/mypage/changepassword"))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl("/accessdenied"));
	}
	
	@Nested
	class ChangePassword{
		PasswordChangeForm form;
		
		@BeforeEach
		void setUp() {
			form = new PasswordChangeForm("加藤健","example@ezweb.ne.jp",
					                      "pinballs","wonderSong","wonderSong");
		}
		
		@Test
		@WithMockCustomUser(userName="加藤健",password="pinballs",role="ROLE_USER")
		void changePasswordでパスワードが更新される() throws Exception{
			doNothing().when(accountInfoService).updatePassword(form);
			
			mockMvc.perform(post("/index/mypage/changepassword/update")
					       .flashAttr("PasswordChangeForm", form)
					       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
					       .with(SecurityMockMvcRequestPostProcessors.csrf()))
			       .andExpect(status().is3xxRedirection())
			       .andExpect(model().hasNoErrors())
			       .andExpect(flash().attribute("message", "更新が完了しました"))
			       .andExpect(redirectedUrl("/index/mypage"));
		    verify(accountInfoService,times(1)).updatePassword(form);
		}
		
		@Test
		@WithMockCustomUser(userName="加藤健",password="pinballs",role="ROLE_USER")
		void changePasswordでバリデーションエラーが発生する() throws Exception{
			form.setMail("hogehoge");
			form.setOldPassword("pinballs");
			form.setPassword("");
			form.setConfirmPassword("wonder");
			doNothing().when(accountInfoService).updatePassword(form);
			
			mockMvc.perform(post("/index/mypage/changepassword/update")
					       .flashAttr("PasswordChangeForm", form)
					       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
					       .with(SecurityMockMvcRequestPostProcessors.csrf()))
			       .andExpect(status().is2xxSuccessful())
			       .andExpect(model().hasErrors())
			       .andExpect(model().attributeHasFieldErrors("PasswordChangeForm"
			    	   , "password","confirmPassword"))
			       .andExpect(view().name("MyPage/ChangePassword"));
		    verify(accountInfoService,times(0)).updatePassword(form);
		}
		
		@Test
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void changePasswordでパスワードが更新されない() throws Exception{	
			mockMvc.perform(post("/index/mypage/changepassword/update")
					       .flashAttr("PasswordChangeForm", form)
					       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
					       .with(SecurityMockMvcRequestPostProcessors.csrf()))
				   .andExpect(status().isForbidden())
				   .andExpect(forwardedUrl("/accessdenied"));
		}
	}

}
