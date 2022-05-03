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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.dining.boyaki.config.BeanConfig;
import com.dining.boyaki.config.SuccessHandler;
import com.dining.boyaki.model.entity.StatusList;
import com.dining.boyaki.model.form.AccountInfoForm;
import com.dining.boyaki.model.form.validation.UniqueNickNameValidator;
import com.dining.boyaki.model.service.AccountInfoService;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.util.WithMockCustomUser;

@AutoConfigureMockMvc
@AutoConfigureMybatis
@WebMvcTest(controllers = AccountInfoController.class,
            includeFilters = @ComponentScan.Filter
                            (type = FilterType.ASSIGNABLE_TYPE,
                             value = {AccountUserDetailsService.class,BeanConfig.class,
            		                  SuccessHandler.class}))
public class AccountInfoControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@MockBean
	UniqueNickNameValidator uniqueNickNameValidator;
	
	@MockBean
	AccountInfoService accountInfoService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
				                 .apply(springSecurity()).build();
		when(uniqueNickNameValidator.supports(any())).thenReturn(true);
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	void showIndexMyPageでマイページ画面が表示される() throws Exception{
		AccountInfoForm form = new AccountInfoForm();
		form.setUserName("miho");
		form.setNickName("匿名");
		form.setProfile("5000兆円欲しい！！！");
		form.setStatus(0);
		form.setGender(2);
		form.setAge(2);
		form.setHeight(161);
		form.setWeight(47);
		when(accountInfoService.findAccountInfo("miho")).thenReturn(form);
		
		mockMvc.perform(get("/index/mypage"))
				       .andExpect(status().is2xxSuccessful())
				       .andExpect(model().attribute("statusList",StatusList.values()))
				       .andExpect(model().attributeExists("bmi"))
				       .andExpect(view().name("MyPage/IndexMyPage"));
		verify(accountInfoService,times(1)).findAccountInfo("miho");
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	void showEditMyPageでプロフィール編集画面が表示される() throws Exception{
		AccountInfoForm form = new AccountInfoForm();
		form.setUserName("miho");
		form.setNickName("匿名");
		form.setProfile("5000兆円欲しい！！！");
		form.setStatus(0);
		form.setGender(2);
		form.setAge(2);
		when(accountInfoService.findAccountInfo("miho")).thenReturn(form);
		
		mockMvc.perform(get("/index/mypage/edit"))
				       .andExpect(status().is2xxSuccessful())
				       .andExpect(model().attribute("AccountInfoForm",
				    		                        hasProperty("userName",is("miho"))))
				       .andExpect(model().attribute("statusList",StatusList.values()))
				       .andExpect(view().name("MyPage/EditMyPage"));
		verify(accountInfoService,times(1)).findAccountInfo("miho");
	}
	
	@Nested
	@WithMockCustomUser(userName="加藤健",password="pinballs",role="ROLE_USER")
    class updateContent {
		AccountInfoForm form;
		
		@BeforeEach
		void setup() {
			form = new AccountInfoForm("加藤健","kenken","間食が止まらない",
					                   3,1,3,165,60);
		}
		
		@Test
		void updateMyPageでユーザ情報が更新される() throws Exception{
			doNothing().when(accountInfoService).updateAccountInfo(form);
			mockMvc.perform(post("/index/mypage/edit/update")
					       .flashAttr("AccountInfoForm", form)
					       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
					       .with(SecurityMockMvcRequestPostProcessors.csrf()))
			       .andExpect(status().is3xxRedirection())
			       .andExpect(model().hasNoErrors())
			       .andExpect(redirectedUrl("/index/mypage"));
			verify(accountInfoService,times(1)).updateAccountInfo(form);
		}
		
		@Test
		void updateMyPageでバリデーションエラーが発生する() throws Exception{
			form.setNickName("");
			form.setProfile("123456789012345678901234567890123456789012345678901");
			doNothing().when(accountInfoService).updateAccountInfo(form);
			mockMvc.perform(post("/index/mypage/edit/update")
					       .flashAttr("AccountInfoForm", form)
					       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
					       .with(SecurityMockMvcRequestPostProcessors.csrf()))
			       .andExpect(status().is2xxSuccessful())
			       .andExpect(model().attribute("statusList",StatusList.values()))
			       .andExpect(model().hasErrors())
			       .andExpect(model().attributeHasFieldErrors("AccountInfoForm"
			    		   , "nickName","profile"))
			       .andExpect(view().name("MyPage/EditMyPage"));
			verify(accountInfoService,times(0)).updateAccountInfo(form);
		}
	}
	
	@Test
	@WithMockCustomUser(userName="糸井",password="sigeSIGE",role="ROLE_USER")
	void showConfirmPageでアカウント削除確認ページが表示される() throws Exception{
		mockMvc.perform(get("/index/mypage/confirm"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("ConfirmDelete",
		    		                        hasProperty("userName",is("糸井"))))
		       .andExpect(view().name("MyPage/ConfirmDelete"));
	}
	
	@Test
	@WithMockCustomUser(userName="加藤健",password="pinballs",role="ROLE_USER")
	void deleteAccountでアカウント削除後にログインページへ遷移する() throws Exception{
		AccountInfoForm form = new AccountInfoForm();
		form.setUserName("加藤健");
		doNothing().when(accountInfoService).deleteAccount("加藤健");
		mockMvc.perform(post("/index/mypage/confirm/delete")
				       .flashAttr("AccountInfoForm", form)
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is3xxRedirection())
		       .andExpect(flash().attribute("register", "退会処理が完了しました"))
		       .andExpect(redirectedUrl("/login"));
		verify(accountInfoService,times(1)).deleteAccount("加藤健");
	}

}
