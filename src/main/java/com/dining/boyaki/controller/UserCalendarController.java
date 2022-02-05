package com.dining.boyaki.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserCalendarController {
	
	@RequestMapping("/index")
	@PreAuthorize("hasRole('ROLE_USER')")
	public String showUserCalendar(Model model) {
		return "UserCalendar/UserCalendar";
	}

}
