package com.dining.boyaki.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockitoAnnotations;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.dining.boyaki.config.BeanConfig;
import com.dining.boyaki.config.SuccessHandler;
import com.dining.boyaki.model.service.AccountUserDetailsService;
import com.dining.boyaki.util.CsvDataSetLoader;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@AutoConfigureMockMvc
@AutoConfigureMybatis
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class,
	                     WithSecurityContextTestExecutionListener.class})
@WebMvcTest(controllers = LoginController.class,
            includeFilters = @ComponentScan.Filter
                            (type = FilterType.ASSIGNABLE_TYPE,
                             value = {AccountUserDetailsService.class,BeanConfig.class,
            		                  SuccessHandler.class}))
@Transactional
public class LoginControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
    
    @BeforeEach
    void setUp() {
    	MockitoAnnotations.openMocks(this);
    	this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
               //↑SecurityとMVCテストを統合するために必要なすべての初期セットアップを実行(by公式リファレンス)
                .build();
    	}
    
    @Test
	void showTopPageでトップ画面が表示される() throws Exception{
    	this.mockMvc.perform(get("/"))
    	            .andExpect(status().is2xxSuccessful())
    	            .andExpect(view().name("Login/Index"));
    }
    
    @Test
	void showLoginPageでログイン画面が表示される() throws Exception{
    	this.mockMvc.perform(get("/login"))
    	            .andExpect(status().is2xxSuccessful())
    	            .andExpect(view().name("Login/Login"));
    }
    
    @Test
    @DatabaseSetup(value="/controller/Login/setup/")
	void ROLE_USERがログインに成功するとUSER用のトップ画面が表示される() throws Exception{
    	this.mockMvc.perform(formLogin("/authenticate")
	                        .user("username", "加藤健")
	                        .password("password","pinballs")
                             )
    	            .andExpect(authenticated())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/index"));
    }
    
    @Test
    @DatabaseSetup(value="/controller/Login/setup/")
	void ROLE_ADMINがログインに成功するとADMIN用のトップ画面が表示される() throws Exception{
    	this.mockMvc.perform(formLogin("/authenticate")
	                        .user("username", "admin")
	                        .password("password","select*fromUser")
                             )
    	            .andExpect(authenticated())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin"));
    }
    
    @ParameterizedTest
	@CsvSource({"admin,select*fromuser",
		        "鈴木純也,zyunnzyunn"})
    @DatabaseSetup(value="/controller/Login/setup/")
	void PWを間違えたりDBに登録されていないユーザはログインできない(String username,String password) throws Exception{
    	this.mockMvc.perform(formLogin("/authenticate")
	                        .user("username", username)
	                        .password("password",password)
                            )
    	            .andExpect(unauthenticated())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login?error"));
    }
    
    @Test
    @WithMockUser(username="加藤健",authorities= {"ROLE_USER"})
    @DatabaseSetup(value="/controller/Login/setup/")
    void ログイン済みユーザがログアウトするとログイン画面に戻る() throws Exception{
    	this.mockMvc.perform(logout())
    	            .andExpect(status().isFound())
    	            .andExpect(redirectedUrl("/login?logout"))
    	            .andExpect(unauthenticated());
    }

}