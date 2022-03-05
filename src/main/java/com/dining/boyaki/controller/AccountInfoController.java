package com.dining.boyaki.controller;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
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

import com.dining.boyaki.model.service.AccountInfoService;
import com.dining.boyaki.model.entity.AccountUserDetails;
import com.dining.boyaki.model.entity.StatusList;
import com.dining.boyaki.model.form.AccountInfoForm;
import com.dining.boyaki.model.form.validation.UniqueNickNameValidator;

@Controller
public class AccountInfoController {
	
	private final AccountInfoService accountInfoService;
	
	private final UniqueNickNameValidator uniqueNickNameValidator;
	
	public AccountInfoController(AccountInfoService accountInfoService,
			                     UniqueNickNameValidator uniqueNickNameValidator) {
		this.accountInfoService = accountInfoService;
		this.uniqueNickNameValidator = uniqueNickNameValidator;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.addValidators(uniqueNickNameValidator);
	}
	
	@GetMapping("/index/mypage")
	public String showMyPage(@AuthenticationPrincipal AccountUserDetails details,
			                 Model model) {
		AccountInfoForm form = accountInfoService.findAccountInfo(details.getUsername());
		model.addAttribute("AccountInfoForm", form);
		model.addAttribute("statusList", StatusList.values());
		return "MyPage/MyPage";
	}
	
	@PostMapping("/index/mypage/update")
	public String updateMyPage(@AuthenticationPrincipal AccountUserDetails details,
			                   @ModelAttribute("AccountInfoForm") @Validated AccountInfoForm form,
			                   BindingResult result,Model model) {
		if(result.hasErrors()) {
			model.addAttribute("statusList", StatusList.values());
			return "MyPage/MyPage";
		}
		form.setUserName(details.getUsername());
		accountInfoService.updateAccountInfo(form);
		return "redirect:/index/mypage";
	}
	
	@PostMapping("/index/mypage/delete")
	public String deleteAccount(@AuthenticationPrincipal AccountUserDetails details){
		accountInfoService.deleteAccount(details.getUsername());
		return "redirect:/login";
	}

}
