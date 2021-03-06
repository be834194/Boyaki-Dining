package com.dining.boyaki.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.security.access.prepost.PreAuthorize;
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

import com.dining.boyaki.model.service.AccountInfoService;
import com.dining.boyaki.model.entity.AccountUserDetails;
import com.dining.boyaki.model.enums.StatusList;
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
		binder.addValidators(uniqueNickNameValidator);
	}
	
	@GetMapping("/index/mypage")
	public String showIndexMyPage(@AuthenticationPrincipal AccountUserDetails details,
			                      Model model) {
		AccountInfoForm form = accountInfoService.findAccountInfo(details.getUsername());
		model.addAttribute("AccountInfoForm", form);
		model.addAttribute("statusList", StatusList.values());
		BigDecimal bmi = new BigDecimal(form.getWeight() / (form.getHeight() * 0.01) / (form.getHeight() * 0.01));
		bmi = bmi.setScale(2, RoundingMode.HALF_UP);
		model.addAttribute("bmi", bmi);
		return "MyPage/IndexMyPage";
	}
	
	@GetMapping("/index/mypage/edit")
	@PreAuthorize("principal.username != 'guestuser'")
	public String showEditMyPage(@AuthenticationPrincipal AccountUserDetails details,
			                     Model model) {
		AccountInfoForm form = accountInfoService.findAccountInfo(details.getUsername());
		model.addAttribute("AccountInfoForm", form);
		model.addAttribute("statusList", StatusList.values());
		return "MyPage/EditMyPage";
	}
	
	@PostMapping("/index/mypage/edit/update")
	@PreAuthorize("principal.username != 'guestuser'")
	public String updateMyPage(@ModelAttribute("AccountInfoForm") @Validated AccountInfoForm form,
			                   BindingResult result,Model model) {
		if(result.hasErrors()) {
			model.addAttribute("statusList", StatusList.values());
			return "MyPage/EditMyPage";
		}
		accountInfoService.updateAccountInfo(form);
		return "redirect:/index/mypage";
	}
	
	@GetMapping("/index/mypage/confirm")
	@PreAuthorize("principal.username != 'guestuser'")
	public String showConfirmPage(@AuthenticationPrincipal AccountUserDetails details,
			                      Model model) {
		AccountInfoForm form = new AccountInfoForm();
		form.setUserName(details.getUsername());
		model.addAttribute("ConfirmDelete", form);
		return "MyPage/ConfirmDelete";
	}
	
	@PostMapping("/index/mypage/confirm/delete")
	@PreAuthorize("principal.username != 'guestuser'")
	public String deleteAccount(@ModelAttribute("AccountInfoForm") AccountInfoForm form,
			                    RedirectAttributes model){
		accountInfoService.deleteAccount(form.getUserName());
		model.addFlashAttribute("register", "?????????????????????????????????");
		return "redirect:/login";
	}

}
