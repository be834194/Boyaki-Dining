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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
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
	void showloginPageでログイン画面が表示される() throws Exception{
    	this.mockMvc.perform(get("/login"))
    	            .andExpect(view().name("Login/Login"));
    }
    
    @Test
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
    void ログイン済みユーザがログアウトするとログイン画面に戻る() throws Exception{
    	this.mockMvc.perform(logout())
    	            .andExpect(status().isFound())
    	            .andExpect(redirectedUrl("/login?logout"))
    	            .andExpect(unauthenticated());
    }

}