package com.dining.boyaki.config;

import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.passay.PasswordValidator;
import org.springframework.http.HttpStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.firewall.HttpStatusRequestRejectedHandler;
import org.springframework.security.web.firewall.RequestRejectedHandler;
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
	
	@Bean  //Jpeg,JFIFイメージのExif書き込み,更新,削除機能のインタフェース
	public ExifRewriter exifRewriter() {
		return new ExifRewriter();
	}
	
	@Bean //RequestRejectedExceptionをハンドリング
	public RequestRejectedHandler requestRejectedHandler() {
	  return new HttpStatusRequestRejectedHandler(HttpStatus.NOT_FOUND.value());
	}

}
