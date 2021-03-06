package com.dining.boyaki.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.dining.boyaki.config.BeanConfig;
import com.dining.boyaki.config.SuccessHandler;
import com.dining.boyaki.model.service.AccountUserDetailsService;

@AutoConfigureMockMvc
@AutoConfigureMybatis
@WebMvcTest(controllers = AccessDeniedController.class,
			includeFilters = @ComponentScan.Filter
							(type = FilterType.ASSIGNABLE_TYPE,
							 value = {AccountUserDetailsService.class,BeanConfig.class,
							          SuccessHandler.class}))
public class AccessDeniedControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@BeforeEach()
	void setUp() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
				                      .apply(springSecurity())
				                      .build();
	}
	
	@Test
	@WithMockUser(username="????????????",authorities= {"ROLE_USER"})
	void ROLE_USER???ROLE_ADMIN????????????????????????????????????() throws Exception{
    	this.mockMvc.perform(get("/admin"))
    	            .andExpect(status().isForbidden())
    	            .andExpect(forwardedUrl("/accessdenied"));
    }
	
	@Test
	@WithMockUser(username="admin",authorities= {"ROLE_ADMIN"})
	void ROLE_ADMIN???ROLE_USER????????????????????????????????????() throws Exception{
    	this.mockMvc.perform(get("/index"))
    	            .andExpect(status().isForbidden())
    	            .andExpect(forwardedUrl("/accessdenied"));
    }

}
