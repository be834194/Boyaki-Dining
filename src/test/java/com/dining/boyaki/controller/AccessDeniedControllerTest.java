package com.dining.boyaki.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class AccessDeniedControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@BeforeEach()
	void setUp() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
	}
	
	@Test
	@WithMockUser(username="マクベイ",authorities= {"ROLE_USER"})
	void ROLE_USERはROLE_ADMIN用ページには遷移できない() throws Exception{
    	this.mockMvc.perform(get("/admin"))
    	            .andExpect(status().isForbidden())
    	            .andExpect(forwardedUrl("/accessdenied"));
    }
	
	@Test
	@WithMockUser(username="admin",authorities= {"ROLE_ADMIN"})
	void ROLE_ADMINはROLE_USER用ページには遷移できない() throws Exception{
    	this.mockMvc.perform(get("/index"))
    	            .andExpect(status().isForbidden())
    	            .andExpect(forwardedUrl("/accessdenied"));
    }

}
