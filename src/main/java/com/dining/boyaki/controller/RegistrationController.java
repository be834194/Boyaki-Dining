package com.dining.boyaki.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.form.validation.UniqueMailValidator;
import com.dining.boyaki.model.form.validation.UniqueUsernameValidator;
import com.dining.boyaki.model.service.RegistrationService;

@Controller
public class RegistrationController {
	
	private final RegistrationService registrationService;
	
	private final UniqueMailValidator uniqueMailValidator;
	
	private final UniqueUsernameValidator uniqueUsernameValidator;
	
	public RegistrationController(RegistrationService registrationService,
			                      UniqueMailValidator uniqueMailValidator,
			                      UniqueUsernameValidator uniqueUsernameValidator) {
		this.registrationService = registrationService;
		this.uniqueMailValidator = uniqueMailValidator;
		this.uniqueUsernameValidator = uniqueUsernameValidator;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(this.uniqueUsernameValidator,
				             this.uniqueMailValidator);
	}
	
	@GetMapping("/registration")
	String showRegistration(@ModelAttribute("registerForm")RegisterForm form,
			                Model model) {
		return "Login/Registration";
	}
	
	@PostMapping("/regist")
	String regist(@ModelAttribute("registerForm") @Validated RegisterForm form,
                  BindingResult result, RedirectAttributes model) {
		if(result.hasErrors()) {
			return "Login/Registration";
		}
		registrationService.insertAccount(form);
		model.addFlashAttribute("register", "ユーザ登録が完了しました");
		return "redirect:/login";
	}

}
