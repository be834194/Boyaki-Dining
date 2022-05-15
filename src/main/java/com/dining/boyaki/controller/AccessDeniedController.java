package com.dining.boyaki.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController {
	
	@GetMapping("/accessdenied")
	public String showUserCalendar(Model model) {
		return "error/403";
	}

}
