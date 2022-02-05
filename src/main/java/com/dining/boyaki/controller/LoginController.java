package com.dining.boyaki.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {
	
	@RequestMapping("/login")
	public String showloginPage(Model model) {
		return "Login/Login";
	}

}
