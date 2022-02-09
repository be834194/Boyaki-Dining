package com.dining.boyaki.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dining.boyaki.model.service.AccountUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final SuccessHandler successHandler;
	
	private final AccountUserDetailsService accountUserDetailsService;
	
	private final PasswordEncoder passwordEncoder;
	
	public WebSecurityConfig(SuccessHandler successHandler,
			                 AccountUserDetailsService accountUserDetailsService,
			                 PasswordEncoder passwordEncoder) {
		this.successHandler = successHandler;
		this.accountUserDetailsService = accountUserDetailsService;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override //全体に対するセキュリティ設定を行う
    public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**");
    }

    @Override //URLごとに異なるセキュリティ設定を行う
    protected void configure(HttpSecurity http) throws Exception {
    	http.formLogin()
    	    .loginPage("/login")
    	    .loginProcessingUrl("/authenticate")
    	    .usernameParameter("username")
    	    .passwordParameter("password")
    	    .successHandler(successHandler)
    	    .failureUrl("/login?error")
    	    .permitAll();
    	http.logout()
    	    .logoutUrl("/logout")
    	    .logoutSuccessUrl("/login?logout")
    	    .permitAll();
    	http.authorizeRequests()// アクセス権限の設定
    	    .antMatchers("/registration").permitAll()
		    .antMatchers("/regist").permitAll()
    	    .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
            .antMatchers("/index/**").hasAuthority("ROLE_USER")
            .anyRequest().authenticated();
    }

    @Override //認証方法の実装の設定を行う
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth.userDetailsService(accountUserDetailsService).passwordEncoder(passwordEncoder);
    }

}
