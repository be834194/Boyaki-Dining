package com.dining.boyaki.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController {
	
	@RequestMapping("/admin")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String showAdminIndex(Model model) {
		return "Admin/AdminIndex";
	}

}
