package com.dining.boyaki.controller.combined;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.config.BeanConfig;
import com.dining.boyaki.config.SuccessHandler;
import com.dining.boyaki.controller.RegistrationController;
import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.form.validation.UniqueMailValidator;
import com.dining.boyaki.model.form.validation.UniqueUsernameValidator;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.model.service.FindDataSharedService;
import com.dining.boyaki.model.service.ChangeEntitySharedService;
import com.dining.boyaki.model.service.RegistrationService;
import com.dining.boyaki.util.CsvDataSetLoader;

@AutoConfigureMockMvc
@AutoConfigureMybatis
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
			             TransactionalTestExecutionListener.class,
			             DbUnitTestExecutionListener.class}) 
@WebMvcTest(controllers = RegistrationController.class,
			includeFilters = @ComponentScan.Filter
			                (type = FilterType.ASSIGNABLE_TYPE,
			                 value = {AccountUserDetailsService.class,BeanConfig.class,SuccessHandler.class,
			                          ChangeEntitySharedService.class,RegistrationService.class,
			                          FindDataSharedService.class,
			                          UniqueMailValidator.class,UniqueUsernameValidator.class}))
@Transactional
public class RegistrationControllerCombinedTest {
	
	private static final LocalDateTime datetime = LocalDateTime.of(2022, 2, 8, 11, 00, 52);
	
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
	void tearDown() {
		mock.close();
	}
	
	@Test
	void showRegistrationで新規登録画面が表示される() throws Exception{
		this.mockMvc.perform(get("/registration"))
		            .andExpect(status().is2xxSuccessful())
                    .andExpect(view().name("Login/Registration"))
                    .andExpect(model().attributeExists("registerForm"));
	}
	
	@Test
	@DatabaseSetup(value="/controller/Registration/setup/")
	@ExpectedDatabase(value="/controller/Registration/insert/",assertionMode=DatabaseAssertionMode.NON_STRICT)
	void registで新規登録後にログイン画面へ遷移する() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setUserName("マクベイ");
		form.setMail("north-east@gmail.com");
		form.setPassword("sun-flan-sis");
		form.setConfirmPassword("sun-flan-sis");
		this.mockMvc.perform(post("/regist")
				            .flashAttr("registerForm", form)
				            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				            .with(SecurityMockMvcRequestPostProcessors.csrf()))
		            .andExpect(status().is3xxRedirection())
		            .andExpect(model().hasNoErrors())
		            .andExpect(redirectedUrl("/login"))
		            .andExpect(flash().attribute("register", "ユーザ登録が完了しました"));
	}
	
	@ParameterizedTest
	@CsvSource({"加藤健,example@ezweb.ne.jp",
		       "sigeno,example@ezweb.ne.jp"})
	@DatabaseSetup(value="/controller/Registration/setup/")
	void registで新規登録が失敗する(String userName,String mail) throws Exception{
		RegisterForm form = new RegisterForm();
		form.setUserName(userName);
		form.setMail(mail);
		form.setPassword("");
		form.setConfirmPassword("hogehoge");
		this.mockMvc.perform(post("/regist")
				            .flashAttr("registerForm", form)
				            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				            .with(SecurityMockMvcRequestPostProcessors.csrf()))
		            .andExpect(status().is2xxSuccessful())
		            .andExpect(view().name("Login/Registration"))
		            .andExpect(model().hasErrors())
		            .andExpect(model().attributeHasFieldErrors("registerForm"
		            		, "userName","mail","password","confirmPassword"));
	}

}
