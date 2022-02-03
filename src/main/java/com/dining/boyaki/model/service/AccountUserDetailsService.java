package com.dining.boyaki.model.service;

import java.util.Optional;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.AccountUserDetails;
import com.dining.boyaki.model.mapper.LoginMapper;

@Service
public class AccountUserDetailsService implements UserDetailsService {
	
	private final LoginMapper loginMapper;
    
	public AccountUserDetailsService(LoginMapper loginMapper) {
		this.loginMapper = loginMapper;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = Optional.ofNullable(loginMapper.findAccount(username))
				                  .orElseThrow(() -> new UsernameNotFoundException("User not found."));
		return new AccountUserDetails(account,AuthorityUtils.createAuthorityList(account.getRole()));
	}

}
