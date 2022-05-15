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
import com.dining.boyaki.controller.ChangePasswordController;
import com.dining.boyaki.model.form.PasswordChangeForm;
import com.dining.boyaki.model.form.validation.NotReusedPasswordValidator;
import com.dining.boyaki.model.form.validation.OldPasswordValidator;
import com.dining.boyaki.model.service.AccountInfoService;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.model.service.PasswordHistoryService;
import com.dining.boyaki.model.service.FindDataSharedService;
import com.dining.boyaki.util.CsvDataSetLoader;
import com.dining.boyaki.util.WithMockCustomUser;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@AutoConfigureMockMvc
@AutoConfigureMybatis
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
			             TransactionalTestExecutionListener.class,
			             DbUnitTestExecutionListener.class,
			             WithSecurityContextTestExecutionListener.class}) 
@WebMvcTest(controllers = ChangePasswordController.class,
			includeFilters = @ComponentScan.Filter
			                (type = FilterType.ASSIGNABLE_TYPE,
			                 value = {AccountUserDetailsService.class,BeanConfig.class,SuccessHandler.class,
			                		  AccountInfoService.class,
			                          FindDataSharedService.class,PasswordHistoryService.class,
			                          NotReusedPasswordValidator.class,OldPasswordValidator.class}))
@Transactional
public class ChangePasswordControllerCombinedTest {
	
	private static final LocalDateTime datetime = LocalDateTime.of(2022, 2, 10, 20, 39, 45);
	
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
	@WithMockCustomUser(userName="糸井",password="sigeSIGE",role="ROLE_USER")
	void showChangePasswordPageでパスワード変更画面が表示される() throws Exception{
		mockMvc.perform(get("/index/mypage/changepassword"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("PasswordChangeForm",
		    		                        hasProperty("userName",is("糸井"))))
		       .andExpect(view().name("MyPage/ChangePassword"));
	}
	
	@Test
	@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
	void showChangePasswordPageでパスワード変更画面が表示されない() throws Exception{
		mockMvc.perform(get("/index/mypage/changepassword"))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl("/accessdenied"));
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean-Nu",role="ROLE_USER")
	@DatabaseSetup(value = "/controller/ChangePassword/setup/")
	@ExpectedDatabase(value = "/controller/ChangePassword/update/",assertionMode=DatabaseAssertionMode.NON_STRICT)
	void changePasswordでパスワードが更新される() throws Exception{
		PasswordChangeForm form = new PasswordChangeForm("miho","miho@gmail.com",
				                                         "ocean-Nu","script-Java","script-Java");
		mockMvc.perform(post("/index/mypage/changepassword/update")
				       .flashAttr("PasswordChangeForm", form)
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is3xxRedirection())
		       .andExpect(model().hasNoErrors())
		       .andExpect(flash().attribute("message", "更新が完了しました"))
		       .andExpect(redirectedUrl("/index/mypage"));
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean-Nu",role="ROLE_USER")
	@DatabaseSetup(value = "/controller/ChangePassword/setup/")
	void changePasswordでバリデーションエラーが発生する() throws Exception{
		PasswordChangeForm form = new PasswordChangeForm("miho","hogehoge",
                                                         "pinballs","","wonder");
		mockMvc.perform(post("/index/mypage/changepassword/update")
				       .flashAttr("PasswordChangeForm", form)
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().hasErrors())
		       .andExpect(model().attributeHasFieldErrors("PasswordChangeForm"
		    	   , "mail","oldPassword","password","confirmPassword"))
		       .andExpect(view().name("MyPage/ChangePassword"));
	}
	
	@Test
	@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
	void changePasswordでパスワードが更新されない() throws Exception{
		PasswordChangeForm form = new PasswordChangeForm();
		mockMvc.perform(post("/index/mypage/changepassword/update")
				       .flashAttr("PasswordChangeForm", form)
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl("/accessdenied"));
	}

}
