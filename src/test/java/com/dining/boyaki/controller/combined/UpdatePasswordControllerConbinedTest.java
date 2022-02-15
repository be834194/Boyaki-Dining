package com.dining.boyaki.controller.combined;

import java.time.LocalDateTime;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class})
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class UpdatePasswordControllerConbinedTest {
	
	private static final LocalDateTime datetime = LocalDateTime.of(2022, 2, 10, 20, 39, 45);
	
	private static MockedStatic<LocalDateTime> mock;
	
	@Autowired
	private MockMvc mockMvc;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
		mock.when(LocalDateTime::now).thenReturn(datetime);
	}
	
	@AfterEach
    void tearDown() throws Exception {
        mock.close();
	}
	
	@Test
	void showResettingPasswordでPW更新画面が表示される() throws Exception{
    	this.mockMvc.perform(get("/resetpassword"))
    	            .andExpect(view().name("Login/ResettingPassword"))
    	            .andExpect(model().attributeExists("registerForm"));
    }
	
	@Test
	@DatabaseSetup(value="/controller/UpdatePassword/setup/")
	@ExpectedDatabase(value="/controller/UpdatePassword/update/",assertionMode=DatabaseAssertionMode.NON_STRICT)
	void updatePasswordでPW更新後にログイン画面へ遷移する() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setMail("miho@gmail.com");
		form.setPassword("script-Java");
		form.setConfirmPassword("script-Java");
		this.mockMvc.perform(post("/updatePassword")
	                        .flashAttr("registerForm", form)
	                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(model().hasNoErrors())
                    .andExpect(redirectedUrl("/login"))
                    .andExpect(flash().attribute("register", "パスワードを再設定いたしました"));
	}
	
	@Test
	@DatabaseSetup(value="/controller/UpdatePassword/setup/")
	void updatePasswordでPW更新処理が失敗する() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setMail("disney@gmail.com");
		form.setPassword("");
		form.setConfirmPassword("script-Java");
		this.mockMvc.perform(post("/updatePassword")
				            .flashAttr("registerForm", form)
				            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				            .with(SecurityMockMvcRequestPostProcessors.csrf()))
		            .andExpect(status().is2xxSuccessful())
		            .andExpect(view().name("Login/ResettingPassword"))
		            .andExpect(model().hasErrors())
		            .andExpect(model().attributeHasFieldErrors("registerForm"
		            		, "mail","password","confirmPassword"));
	}

}
