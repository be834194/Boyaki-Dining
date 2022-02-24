package com.dining.boyaki.controller;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.InitBinder;

import com.dining.boyaki.model.entity.AccountUserDetails;
import com.dining.boyaki.model.entity.DiaryRecordCategory;
import com.dining.boyaki.model.form.DiaryRecordForm;
import com.dining.boyaki.model.service.DiaryRecordService;

@Controller
public class UserCalendarController {
	
	private final DiaryRecordService diaryRecordService;
	
	public UserCalendarController(DiaryRecordService diaryRecordService) {
		this.diaryRecordService = diaryRecordService;
	}
	
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
	
	@GetMapping("/index")
	@PreAuthorize("hasRole('ROLE_USER')")
	public String showUserCalendar(Model model) {
		return "UserCalendar/index";
	}
	
	@GetMapping("/index/create")
	public String showUserCreateContent(@ModelAttribute DiaryRecordForm form,Model model) {
		model.addAttribute("lists", DiaryRecordCategory.values());
		return "UserCalendar/Create";
	}
	
	@PostMapping("/index/create/insert")
	public String createContent(@AuthenticationPrincipal AccountUserDetails details,
			                    @ModelAttribute("diaryRecordForm") @Validated DiaryRecordForm form,
			                    BindingResult result,Model model) {
		if(result.hasErrors()) {
			model.addAttribute("lists", DiaryRecordCategory.values());
			return "UserCalendar/Create";
		}
		DiaryRecordForm exist = diaryRecordService.findOneDiaryRecord(details.getUsername(), form.getCategoryId(), form.getDiaryDay());
		if(exist != null) {
			model.addAttribute("lists", DiaryRecordCategory.values());
			model.addAttribute("message","既に同じカテゴリ、同じ日付で登録されています");
			return "UserCalendar/Create";
		}
		form.setUserName(details.getUsername());
		diaryRecordService.insertDiaryRecord(form);
		return "redirect:/index";
	}
	
	@GetMapping("/index/record/{diaryDay}/{id}")
	public String showUserEditContent(@AuthenticationPrincipal AccountUserDetails details,
			                          @PathVariable("id")int id,
			                          @PathVariable("diaryDay")String diaryDay,
			                          Model model) throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		DiaryRecordForm form;
		try{
			form = diaryRecordService.findOneDiaryRecord(details.getUsername(), id, format.parse(diaryDay));
		} catch(ParseException error) {
			error.printStackTrace();
			return "Common/404";
		}
		if(form == null) {
			return "Common/404";
		}
		model.addAttribute("diaryRecordForm", form);
		model.addAttribute("lists", DiaryRecordCategory.values());
		return "UserCalendar/Edit";
	}
	
	@RequestMapping(value="/index/record/commit",method=RequestMethod.POST,params="update")
	public String updateContent(@AuthenticationPrincipal AccountUserDetails details,
			                    @ModelAttribute("diaryRecordForm") @Validated DiaryRecordForm form,
			                    BindingResult result,Model model) {
		if(result.hasErrors()) {
			model.addAttribute("lists", DiaryRecordCategory.values());
			return "UserCalendar/Edit";
		}
		DiaryRecordForm exist = diaryRecordService.findOneDiaryRecord(details.getUsername(), form.getCategoryId(), form.getDiaryDay());
		if(exist != null && exist != null && !exist.getCreateAt().equals(form.getCreateAt()) ) {
			model.addAttribute("lists", DiaryRecordCategory.values());
			model.addAttribute("message","既に同じカテゴリ、同じ日付で登録されています");
			return "UserCalendar/Create";
		}
		form.setUserName(details.getUsername());
		diaryRecordService.updateDiaryRecord(form);
		return "redirect:/index";
	}
	
	@RequestMapping(value="/index/record/commit",method=RequestMethod.POST,params="delete")
	public String deleteContent(@AuthenticationPrincipal AccountUserDetails details,
			                    @ModelAttribute("diaryRecordForm") DiaryRecordForm form) {
		form.setUserName(details.getUsername());
		diaryRecordService.deleteDiaryRecord(form);
		return "redirect:/index";
	}

}
