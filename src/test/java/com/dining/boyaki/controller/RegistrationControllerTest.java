package com.dining.boyaki.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.form.validation.UniqueUsernameValidator;
import com.dining.boyaki.model.service.RegistrationService;

@AutoConfigureMybatis
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class RegistrationControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Mock
	RegistrationService registrationService;
	
	@Mock
	UniqueUsernameValidator uniqueUsernameValidator;
	
	@InjectMocks
	RegistrationController registrationController;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(registrationController)
				                      .build();
		when(uniqueUsernameValidator.supports(any())).thenReturn(true);
	}
	
	@Test
	void showRegistrationで新規登録画面が表示される() throws Exception{
    	this.mockMvc.perform(get("/registration"))
    	            .andExpect(view().name("Login/Registration"))
    	            .andExpect(model().attributeExists("registerForm"));
    }
	
	@Test
	void registで新規登録後にログイン画面へ遷移する() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setUserName("マクベイ");
		form.setMail("north-east@gmail.com");
		form.setPassword("sun-flan-sis");
		form.setConfirmPassword("sun-flan-sis");
		doNothing().when(registrationService).insertAccount(form);
		
		this.mockMvc.perform(post("/regist")
				            .flashAttr("registerForm", form)
				            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
		            .andExpect(status().is3xxRedirection())
		            .andExpect(redirectedUrl("/login"));
		verify(registrationService,times(1)).insertAccount(form);
		
	}
	
	@Test
	void registで新規登録が失敗する() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setUserName("");
		form.setMail("");
		form.setPassword("");
		form.setConfirmPassword("sun-flan-sis");
		doNothing().when(registrationService).insertAccount(form);
		
		this.mockMvc.perform(post("/regist")
	                        .flashAttr("registerForm", form)
	                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
		            .andExpect(status().is2xxSuccessful()) 
		            .andExpect(view().name("Login/Registration"))
		            .andExpect(model().hasErrors())
		            .andExpect(model().attributeHasFieldErrors("registerForm"
		            		, "userName","mail","password","confirmPassword"));
       verify(registrationService,times(0)).insertAccount(form);
	}
}
