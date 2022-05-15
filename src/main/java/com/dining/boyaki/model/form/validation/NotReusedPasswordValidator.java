package com.dining.boyaki.model.form.validation;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.dining.boyaki.model.form.PasswordChangeForm;
import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.model.service.PasswordHistoryService;

@Component
public class NotReusedPasswordValidator implements Validator {
	
	private final PasswordEncoder passwordEncoder;
	
	private final PasswordHistoryService passwordHistoryService;
	
	private final PasswordValidator encodedPasswordHistoryValidator; //BeanConfigで設定したBean

	public NotReusedPasswordValidator(PasswordEncoder passwordEncoder,
			                          PasswordHistoryService passwordHistoryService,
			                          PasswordValidator encodedPasswordHistoryValidator) {
		this.passwordEncoder = passwordEncoder;
		this.passwordHistoryService = passwordHistoryService;
		this.encodedPasswordHistoryValidator = encodedPasswordHistoryValidator;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return PasswordChangeForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		PasswordChangeForm form = (PasswordChangeForm)target;
		String userName = form.getUserName();
		String mail = form.getMail();
		String newPassword = form.getPassword();
		String currentPassword = passwordHistoryService.findPassword(userName, mail);
		
		if(currentPassword != null) {
			if(isCurrentPasswordCheck(newPassword,currentPassword)) {
				if(isHistoricalPasswordCheck(userName,newPassword)) {
					return;
				} else {
					errors.rejectValue("password", 
     			           "PasswordChangeForm.password",
                            "30日以内に使用したパスワードはご利用できません");
				}
			} else {
				errors.rejectValue("password", 
	        			           "PasswordChangeForm.password",
	                               "ログイン中のパスワードと一緒です");
			}
		} else {
			errors.rejectValue("mail", 
					           "PasswordChangeForm.mail",
                               "メールアドレスに誤りがあります");
		}
	}
	
	//入力された新パスワードが、現在使われているパスワードと一致した場合はfalseを返す
	public boolean isCurrentPasswordCheck(String newPassword,String currentPassword) {
		if(!passwordEncoder.matches(newPassword, currentPassword)) {
			return true;
		}
		return false;
	}
	
	//入力された新パスワードが、過去30日以内に使われたパスワードと一致しないか確認する
	private boolean isHistoricalPasswordCheck(String userName,String newPassword) {
		LocalDateTime useFrom = LocalDateTime.now().minusDays(30)
					                         .withHour(0).withMinute(0).withSecond(0).withNano(0);
		List<PasswordHistory> histories = passwordHistoryService.findUseFrom(userName, useFrom);
		//30日以内にパスワードを変えていないのなら、バリデーションは実行せずにtrueを返す
		if(histories.isEmpty()) {
			return true;
		}
		
		List<PasswordData.Reference> historyData = new ArrayList<>();
		for (PasswordHistory h : histories) {
			//HistoricalReference：履歴パスワードへの参照
			//コンストラクタ(String パスワード文字列)
			historyData.add(new PasswordData.HistoricalReference(h.getPassword()));
			}
		
		//PasswordData：設定したルールでパスワード検証を実行するために使用するパスワード関連情報を扱うクラス
		//コンストラクタ(String ユーザ名,String パスワード、List<PasswordData.Reference> 参照)
		PasswordData passwordData = new PasswordData(userName,newPassword,historyData);
		RuleResult result = encodedPasswordHistoryValidator.validate(passwordData);
		
		if(result.isValid()) {
			return true;
		}
		return false;
	}

}
