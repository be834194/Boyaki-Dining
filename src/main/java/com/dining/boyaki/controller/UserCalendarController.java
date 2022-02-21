package com.dining.boyaki.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.dining.boyaki.model.entity.AccountUserDetails;
import com.dining.boyaki.model.form.DiaryRecordForm;
import com.dining.boyaki.model.service.DiaryRecordService;

@Controller
public class UserCalendarController {
	
	private final DiaryRecordService diaryRecordService;
	
	public UserCalendarController(DiaryRecordService diaryRecordService) {
		this.diaryRecordService = diaryRecordService;
	}
	
	@GetMapping("/index")
	@PreAuthorize("hasRole('ROLE_USER')")
	public String showUserCalendar(Model model) {
		return "UserCalendar/index";
	}
	
	@GetMapping("/index/create")
	public String showUserCreateContent(@ModelAttribute DiaryRecordForm form,Model model) {
		return "UserCalendar/Create";
	}
	
	@PostMapping("/index/create/insert")
	public String createContent(@AuthenticationPrincipal AccountUserDetails details,
			                    @ModelAttribute @Validated DiaryRecordForm form,
			                    BindingResult result,Model model) {
		if(result.hasErrors()) {
			return "UserCalendar/Create";
		}
		form.setUserName(details.getUsername());
		diaryRecordService.insertDiaryRecord(form);
		return "redirect:/index";
	}
	
	@GetMapping("/index/edit")
	public String showUserEditContent(@ModelAttribute DiaryRecordForm form,Model model) {
		return "UserCalendar/Edit";
	}
	
	@PostMapping("/index/edit/update")
	public String updateContent(@AuthenticationPrincipal AccountUserDetails details,
			                    @ModelAttribute @Validated DiaryRecordForm form,
			                    BindingResult result,Model model) {
		if(result.hasErrors()) {
			return "UserCalendar/Edit";
		}
		form.setUserName(details.getUsername());
		diaryRecordService.updateDiaryRecord(form);
		return "redirect:/index";
	}
	
	@PostMapping("/index/edit/update")
	public String deleteContent(@AuthenticationPrincipal AccountUserDetails details,
			                    @ModelAttribute DiaryRecordForm form) {
		form.setUserName(details.getUsername());
		diaryRecordService.deleteDiaryRecord(form);
		return "redirect:/index";
	}

}
