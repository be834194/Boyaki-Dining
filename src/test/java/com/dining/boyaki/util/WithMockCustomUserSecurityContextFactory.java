package com.dining.boyaki.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.AccountUserDetails;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser>{
	
	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		
		Account account = new Account();
		account.setUserName(customUser.userName());
		account.setPassword(customUser.password());
		account.setRole(customUser.role());
		AccountUserDetails principal =
				new AccountUserDetails(account, AuthorityUtils.createAuthorityList(account.getRole()));
		Authentication auth =
			new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
		context.setAuthentication(auth);
		return context;
	}

}
