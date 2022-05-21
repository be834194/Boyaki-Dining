package com.dining.boyaki.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.form.validation.ExistMailValidator;
import com.dining.boyaki.model.service.UpdatePasswordService;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class ForgetPasswordControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Mock
	UpdatePasswordService updatePasswordService;
	
	@Mock
	ExistMailValidator existMailValidator;
	
	@InjectMocks
	ForgetPasswordController forgetPasswordController;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(forgetPasswordController)
				                 .build();
		when(existMailValidator.supports(any())).thenReturn(true);
	}
	
	@Test
	void showResettingPasswordでPW更新画面が表示される() throws Exception{
    	this.mockMvc.perform(get("/resetpassword"))
    	            .andExpect(status().is2xxSuccessful())
    	            .andExpect(view().name("Login/ResettingPassword"))
    	            .andExpect(model().attributeExists("registerForm"));
    }
	
	@Test
	void updatePasswordでPW更新後にログイン画面へ遷移する() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setMail("miho@gmail.com");
		form.setPassword("script-Java");
		form.setConfirmPassword("script-Java");
		doNothing().when(updatePasswordService).updatePassword(form);
		
		this.mockMvc.perform(post("/updatePassword")
				             .flashAttr("registerForm", form)
				             .contentType(MediaType.APPLICATION_FORM_URLENCODED))
		            .andExpect(status().is3xxRedirection())
		            .andExpect(model().hasNoErrors())
		            .andExpect(redirectedUrl("/login"))
		            .andExpect(flash().attribute("register", "パスワードを再設定いたしました"));
		verify(updatePasswordService,times(1)).updatePassword(form);
	}
	
	@Test
	void updatePasswordでゲストユーザのPW更新処理が失敗する() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setMail("guest@gmail.com");
		form.setPassword("hogehoge");
		form.setConfirmPassword("hogehoge");
		doNothing().when(updatePasswordService).updatePassword(form);
		
		this.mockMvc.perform(post("/updatePassword")
			                .flashAttr("registerForm", form)
			                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
		            .andExpect(status().is3xxRedirection())
		            .andExpect(model().hasNoErrors())
		            .andExpect(flash().attribute("guestError", "ゲストログイン用のユーザはパスワードを変更出来ません！"))
		            .andExpect(redirectedUrl("/resetpassword"));
		verify(updatePasswordService,times(0)).updatePassword(form);
	}
	
	@Test
	void updatePasswordでPW更新処理が失敗する() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setMail("");
		form.setPassword("");
		form.setConfirmPassword("script-Java");
		doNothing().when(updatePasswordService).updatePassword(form);
		
		this.mockMvc.perform(post("/updatePassword")
				             .flashAttr("registerForm", form)
				             .contentType(MediaType.APPLICATION_FORM_URLENCODED))
		            .andExpect(status().is2xxSuccessful())
		            .andExpect(view().name("Login/ResettingPassword"))
		            .andExpect(model().hasErrors())
		            .andExpect(model().attributeHasFieldErrors("registerForm"
		            		, "mail","password","confirmPassword"));
		verify(updatePasswordService,times(0)).updatePassword(form);
	}

}
