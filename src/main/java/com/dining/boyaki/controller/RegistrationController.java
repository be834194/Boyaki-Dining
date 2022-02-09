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

import com.dining.boyaki.model.form.RegisterForm;
import com.dining.boyaki.model.form.validation.UniqueUsernameValidator;
import com.dining.boyaki.model.service.RegistrationService;

@Controller
public class RegistrationController {
	
	private final RegistrationService registrationService;
	
	private final UniqueUsernameValidator uniqueUsernameValidator;
	
	public RegistrationController(RegistrationService registrationService,
			                      UniqueUsernameValidator uniqueUsernameValidator) {
		this.registrationService = registrationService;
		this.uniqueUsernameValidator = uniqueUsernameValidator;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(this.uniqueUsernameValidator);
	}
	
	@GetMapping("/registration")
	String showRegistration(@ModelAttribute("registerForm")RegisterForm form,
			                Model model) {
		return "Login/Registration";
	}
	
	@PostMapping("/regist")
	String regist(@ModelAttribute("registerForm") @Validated RegisterForm form,
                  BindingResult result, Model model) {
		if(result.hasErrors()) {
			return "Login/Registration";
		}
		registrationService.insertAccount(form);
		return "redirect:/login";
	}

}
