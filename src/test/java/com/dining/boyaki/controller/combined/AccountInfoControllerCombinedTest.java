package com.dining.boyaki.controller.combined;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

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
import com.dining.boyaki.controller.AccountInfoController;
import com.dining.boyaki.model.entity.StatusList;
import com.dining.boyaki.model.form.AccountInfoForm;
import com.dining.boyaki.model.form.validation.UniqueNickNameValidator;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.model.service.AccountInfoService;
import com.dining.boyaki.model.service.FindDataSharedService;
import com.dining.boyaki.util.CsvDataSetLoader;
import com.dining.boyaki.util.WithMockCustomUser;

@AutoConfigureMockMvc
@AutoConfigureMybatis
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
			             TransactionalTestExecutionListener.class,
			             DbUnitTestExecutionListener.class,
			             WithSecurityContextTestExecutionListener.class}) 
@WebMvcTest(controllers = AccountInfoController.class,
includeFilters = @ComponentScan.Filter
                (type = FilterType.ASSIGNABLE_TYPE,
                 value = {AccountUserDetailsService.class,BeanConfig.class,SuccessHandler.class,
                		  AccountInfoService.class,
                          FindDataSharedService.class,UniqueNickNameValidator.class}))
@Transactional
public class AccountInfoControllerCombinedTest {
	
	private static final LocalDateTime datetime = LocalDateTime.of(2022, 2, 10, 20, 35, 12);
	
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
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/AccountInfo/setup/")
	void showIndexMyPageでマイページ画面が表示される() throws Exception{
		mockMvc.perform(get("/index/mypage"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("statusList",StatusList.values()))
		       .andExpect(model().attributeExists("bmi"))
		       .andExpect(view().name("MyPage/IndexMyPage"));
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/AccountInfo/setup/")
	void showIndexMyPageでマイページ画面が表示されない() throws Exception{
		mockMvc.perform(get("/index/mypage"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("statusList",StatusList.values()))
		       .andExpect(model().attributeExists("bmi"))
		       .andExpect(view().name("MyPage/IndexMyPage"));
	}
	
	@Test
	@WithMockCustomUser(userName="miho",password="ocean_nu",role="ROLE_USER")
	@DatabaseSetup(value="/controller/AccountInfo/setup/")
	void showEditMyPageでプロフィール編集画面が表示される() throws Exception{
		mockMvc.perform(get("/index/mypage/edit"))
		       .andExpect(status().is2xxSuccessful())
		       .andExpect(model().attribute("AccountInfoForm",
		    		                        hasProperty("userName",is("miho"))))
		       .andExpect(model().attribute("statusList",StatusList.values()))
		       .andExpect(view().name("MyPage/EditMyPage"));
	}
	
	@Test
	@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
	void showEditMyPageでプロフィール編集画面が表示されない() throws Exception{
		mockMvc.perform(get("/index/mypage/edit"))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl("/accessdenied"));
	}
	
	@Test
	@WithMockCustomUser(userName="加藤健",password="pinballs",role="ROLE_USER")
	@DatabaseSetup(value="/controller/AccountInfo/setup/")
	@ExpectedDatabase(value="/controller/AccountInfo/update/")
	void updateMyPageでユーザ情報が更新される() throws Exception{
		AccountInfoForm form = new AccountInfoForm("加藤健","kenken","間食が止まらない",
				                                   3,1,3,167,64);
		
		mockMvc.perform(post("/index/mypage/edit/update")
				       .flashAttr("AccountInfoForm", form)
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().is3xxRedirection())
			   .andExpect(model().hasNoErrors())
			   .andExpect(redirectedUrl("/index/mypage"));
	}
	
	@Test
	@WithMockCustomUser(userName="加藤健",password="pinballs",role="ROLE_USER")
	@DatabaseSetup(value="/controller/AccountInfo/setup/")
	void updateMyPageでバリデーションエラーが発生する() throws Exception{
		AccountInfoForm form = new AccountInfoForm("加藤健","匿名",null,
				                                   3,1,3,167,64);
		form.setProfile("123456789012345678901234567890123456789012345678901");
		
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
	}
	
	@Test
	@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
	void updateMyPageでユーザ情報が更新されない() throws Exception{
		AccountInfoForm form = new AccountInfoForm();
		mockMvc.perform(post("/index/mypage/edit/update")
				       .flashAttr("AccountInfoForm", form)
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl("/accessdenied"));
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
	@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
	void showConfirmPageでアカウント削除確認ページが表示されない() throws Exception{
		mockMvc.perform(get("/index/mypage/confirm"))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl("/accessdenied"));
	}
	
	@Test
	@WithMockCustomUser(userName="加藤健",password="pinballs",role="ROLE_USER")
	@DatabaseSetup(value="/controller/AccountInfo/setup/")
	@ExpectedDatabase(value="/controller/AccountInfo/delete/")
	void deleteAccountでアカウント削除後にログインページへ遷移する() throws Exception{
		AccountInfoForm form = new AccountInfoForm();
		form.setUserName("加藤健");
		mockMvc.perform(post("/index/mypage/confirm/delete")
				       .flashAttr("AccountInfoForm", form)
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
		       .andExpect(status().is3xxRedirection())
		       .andExpect(model().hasNoErrors())
		       .andExpect(flash().attribute("register", "退会処理が完了しました"))
		       .andExpect(redirectedUrl("/login"));
	}
	
	@Test
	@WithMockUser(username="guestuser",authorities= {"ROLE_USER"})
	void showConfirmPageでアカウント削除されない() throws Exception{
		AccountInfoForm form = new AccountInfoForm();
		mockMvc.perform(post("/index/mypage/confirm/delete")
				       .flashAttr("AccountInfoForm", form)
				       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				       .with(SecurityMockMvcRequestPostProcessors.csrf()))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl("/accessdenied"));
	}
	
}
