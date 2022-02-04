package com.dining.boyaki.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.dining.boyaki.model.entity.AccountUserDetails;

@Component
public class SuccessHandler implements AuthenticationSuccessHandler {
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		boolean isAdmin = false;
		
		AccountUserDetails details = (AccountUserDetails)authentication.getPrincipal();
		for(GrantedAuthority authority :details.getAuthorities()) {
			if(authority.getAuthority().equals("ROLE_ADMIN")) {
				isAdmin = true;
			}
		}
		if(isAdmin) {
			response.sendRedirect("/admin");
			return;
		}
	    response.sendRedirect("/index");
	    return;
	}

}
