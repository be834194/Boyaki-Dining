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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.dining.boyaki.config.BeanConfig;
import com.dining.boyaki.config.SuccessHandler;
import com.dining.boyaki.model.enums.StatusList;
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
	void showIndexMyPage??????????????????????????????????????????() throws Exception{
		AccountInfoForm form = new AccountInfoForm("miho","??????","5000????????????????????????",
				                                   0,2,2,161,47);
		when(accountInfoService.findAccountInfo("miho")).thenReturn(form);
		
		mockMvc.perform(get("/index/mypage"))
				       .andExpect(status().is2xxSuccessful())
				       .andExpect(model().attribute("statusList",StatusList.values()))
				       .andExpect(model().attributeExists("bmi"))
				       .andExpect(view().name("MyPage/IndexMyPage"));
		verify(accountInfoService,times(1)).findAccountInfo("miho");
	}
	
	@Nested
	class showEditMyPage{
		@Test
		@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
		void showEditMyPage???????????????????????????????????????????????????() throws Exception{
			AccountInfoForm form = new AccountInfoForm("miho","??????","5000????????????????????????",
	                                                   0,2,2,161,47);
			when(accountInfoService.findAccountInfo("miho")).thenReturn(form);
			
			mockMvc.perform(get("/index/mypage/edit"))
					       .andExpect(status().is2xxSuccessful())
					       .andExpect(model().attribute("AccountInfoForm",
					    		                        hasProperty("userName",is("miho"))))
					       .andExpect(model().attribute("statusList",StatusList.values()))
					       .andExpect(view().name("MyPage/EditMyPage"));
			verify(accountInfoService,times(1)).findAccountInfo("miho");
		}
		
		@Test
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void showEditMyPage??????????????????????????????????????????????????????() throws Exception{
			mockMvc.perform(get("/index/mypage/edit"))
				   .andExpect(status().isForbidden())
				   .andExpect(forwardedUrl("/accessdenied"));
		}
	}
	
	@Nested
	@WithMockCustomUser(userName="?????????",password="pinballs",role="ROLE_USER")
    class updateContent {
		AccountInfoForm form;
		
		@BeforeEach
		void setup() {
			form = new AccountInfoForm("?????????","kenken","????????????????????????",
					                   3,1,3,165,60);
		}
		
		@Test
		void updateMyPage????????????????????????????????????() throws Exception{
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
		void updateMyPage????????????????????????????????????????????????() throws Exception{
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
		
		@Test
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void updateMyPage???????????????????????????????????????() throws Exception{
			mockMvc.perform(post("/index/mypage/edit/update")
					       .flashAttr("AccountInfoForm", form)
					       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
					       .with(SecurityMockMvcRequestPostProcessors.csrf()))
				   .andExpect(status().isForbidden())
				   .andExpect(forwardedUrl("/accessdenied"));
		}
	}
	
	@Nested
	class showConfirmPage{
		@Test
		@WithMockCustomUser(userName="??????",password="sigeSIGE",role="ROLE_USER")
		void showConfirmPage?????????????????????????????????????????????????????????() throws Exception{
			mockMvc.perform(get("/index/mypage/confirm"))
			       .andExpect(status().is2xxSuccessful())
			       .andExpect(model().attribute("ConfirmDelete",
			    		                        hasProperty("userName",is("??????"))))
			       .andExpect(view().name("MyPage/ConfirmDelete"));
		}
		
		@Test
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void showConfirmPage????????????????????????????????????????????????????????????() throws Exception{
			mockMvc.perform(get("/index/mypage/confirm"))
				   .andExpect(status().isForbidden())
				   .andExpect(forwardedUrl("/accessdenied"));
		}
	}
	
	@Nested
	class deleteAccount{
		@Test
		@WithMockCustomUser(userName="?????????",password="pinballs",role="ROLE_USER")
		void deleteAccount??????????????????????????????????????????????????????????????????() throws Exception{
			AccountInfoForm form = new AccountInfoForm();
			form.setUserName("?????????");
			doNothing().when(accountInfoService).deleteAccount("?????????");
			mockMvc.perform(post("/index/mypage/confirm/delete")
					       .flashAttr("AccountInfoForm", form)
					       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
					       .with(SecurityMockMvcRequestPostProcessors.csrf()))
			       .andExpect(status().is3xxRedirection())
			       .andExpect(flash().attribute("register", "?????????????????????????????????"))
			       .andExpect(redirectedUrl("/login"));
			verify(accountInfoService,times(1)).deleteAccount("?????????");
		}
		
		@Test
		@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
		void deleteAccount????????????????????????????????????() throws Exception{
			AccountInfoForm form = new AccountInfoForm();
			mockMvc.perform(post("/index/mypage/confirm/delete")
					       .flashAttr("AccountInfoForm", form)
					       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
					       .with(SecurityMockMvcRequestPostProcessors.csrf()))
				   .andExpect(status().isForbidden())
				   .andExpect(forwardedUrl("/accessdenied"));
		}
	}

}
