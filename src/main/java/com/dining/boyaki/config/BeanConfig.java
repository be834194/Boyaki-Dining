package com.dining.boyaki.config;

import org.passay.PasswordValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dining.boyaki.model.form.validation.rule.EncodedPasswordHistoryRule;

@Configuration
public class BeanConfig {
	
	@Bean
	//PasswordValidator：複数のパスワードルールを評価する
	public PasswordValidator encodedPasswordHistoryValidator() throws Exception{
        return new PasswordValidator(new EncodedPasswordHistoryRule(passwordEncoder()));
    }
	
	@Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
