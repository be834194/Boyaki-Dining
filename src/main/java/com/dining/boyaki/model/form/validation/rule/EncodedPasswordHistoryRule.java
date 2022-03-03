package com.dining.boyaki.model.form.validation.rule;

import org.passay.HistoryRule;
import org.passay.PasswordData;
import org.springframework.security.crypto.password.PasswordEncoder;

//PasswordValidator(BeanConfigを参照)に渡すRULEクラス
//HistoryRule：パスワードが、ユーザーが選択した以前のパスワードの1つと一致するかどうかを判断するためのルール
public class EncodedPasswordHistoryRule extends HistoryRule{
	
	private final PasswordEncoder passwordEncoder;

    public EncodedPasswordHistoryRule(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
	
    @Override
    protected boolean matches(String rawPassword,
    		PasswordData.Reference reference) {
        return passwordEncoder.matches(rawPassword, reference.getPassword());
    }

}
