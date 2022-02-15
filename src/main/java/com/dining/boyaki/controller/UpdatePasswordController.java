package com.dining.boyaki.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.form.validation.ExistMailValidator;
import com.dining.boyaki.model.service.UpdatePasswordService;

@Controller
public class UpdatePasswordController {
	
	private final UpdatePasswordService updatePasswordService;
	
	private final ExistMailValidator existMailValidator;
	
	public UpdatePasswordController(UpdatePasswordService updatePasswordService,
			                           ExistMailValidator existMailValidator) {
		this.updatePasswordService = updatePasswordService;
		this.existMailValidator = existMailValidator;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(existMailValidator);
	}
	
	@GetMapping("/resetpassword")
	String showResettingPassword(@ModelAttribute("registerForm")RegisterForm form,
			                Model model) {
		return "Login/ResettingPassword";
	}
	
	@PostMapping("/updatePassword")
	String updatePassword(@ModelAttribute("registerForm") @Validated RegisterForm form,
			              BindingResult result,  RedirectAttributes model) {
		if(result.hasErrors()) {
			return "Login/ResettingPassword";
		}
		updatePasswordService.updatePassword(form);
		model.addFlashAttribute("register", "パスワードを再設定いたしました");
		return "redirect:/login";
	}

}
