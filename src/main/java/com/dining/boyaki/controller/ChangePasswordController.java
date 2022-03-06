package com.dining.boyaki.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dining.boyaki.model.entity.AccountUserDetails;
import com.dining.boyaki.model.form.PasswordChangeForm;
import com.dining.boyaki.model.form.validation.NotReusedPasswordValidator;
import com.dining.boyaki.model.form.validation.OldPasswordValidator;
import com.dining.boyaki.model.service.AccountInfoService;

@Controller
public class ChangePasswordController {
	
	private final AccountInfoService accountInfoService;
	
	private final NotReusedPasswordValidator notReusedPasswordValidator;
	
	private final OldPasswordValidator oldPasswordValidator;
	
	public ChangePasswordController(AccountInfoService accountInfoService,
			                        NotReusedPasswordValidator notReusedPasswordValidator,
			                        OldPasswordValidator oldPasswordValidator) {
		this.accountInfoService = accountInfoService;
		this.notReusedPasswordValidator = notReusedPasswordValidator;
		this.oldPasswordValidator = oldPasswordValidator;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(notReusedPasswordValidator,oldPasswordValidator);
	}
	
	@GetMapping("/index/mypage/changepassword")
	public String showChangePasswordPage(@AuthenticationPrincipal AccountUserDetails details,
			                             Model model) {
		PasswordChangeForm form = new PasswordChangeForm();
		form.setUserName(details.getUsername());
		model.addAttribute("PasswordChangeForm", form);
		return "MyPage/ChangePassword";
	}
	
	@PostMapping("/index/mypage/changepassword/update")
	String changePassword(@ModelAttribute("PasswordChangeForm") @Validated PasswordChangeForm form,
			              BindingResult result, RedirectAttributes model) {
		if(result.hasErrors()) {
			return "MyPage/ChangePassword";
		}
		accountInfoService.updatePassword(form);
		model.addFlashAttribute("message", "更新が完了しました");
		return "redirect:/index/mypage";
	}

}
