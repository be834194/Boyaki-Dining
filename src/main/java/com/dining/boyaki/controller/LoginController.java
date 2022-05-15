package com.dining.boyaki.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
	
	@GetMapping("/")
	public String showTopPage(Model model) {
		return "Login/Index";
	}
	
	@GetMapping("/login")
	public String showLoginPage(Model model) {
		return "Login/Login";
	}

}
