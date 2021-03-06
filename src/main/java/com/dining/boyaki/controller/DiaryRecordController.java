package com.dining.boyaki.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
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
import com.dining.boyaki.model.enums.DiaryRecordCategory;
import com.dining.boyaki.model.form.DiaryRecordForm;
import com.dining.boyaki.model.form.FileUploadForm;
import com.dining.boyaki.model.service.DiaryRecordService;
import com.dining.boyaki.model.service.FileUploadService;

@Controller
public class DiaryRecordController {
	
	private final DiaryRecordService diaryRecordService;
	
	private final FileUploadService fileUploadService;
	
	private final static String s3Path = "boyaki-dining-image/DiaryRecord";
	
	public DiaryRecordController(DiaryRecordService diaryRecordService,
			FileUploadService fileUploadService) {
		this.diaryRecordService = diaryRecordService;
		this.fileUploadService = fileUploadService;
	}
	
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
	
	@GetMapping("/index")
	public String showUserCalendar(Model model) {
		return "UserCalendar/index";
	}
	
	@GetMapping("/index/create")
	@PreAuthorize("principal.username != 'guestuser'")
	public String showCreateContent(@ModelAttribute("diaryRecordForm") DiaryRecordForm form,
			                        @ModelAttribute("fileUploadForm") FileUploadForm file,Model model) {
		model.addAttribute("lists", DiaryRecordCategory.values());
		return "UserCalendar/Create";
	}
	
	@PostMapping("/index/create/insert")
	@PreAuthorize("principal.username != 'guestuser'")
	public String createContent(@AuthenticationPrincipal AccountUserDetails details,
			                    @ModelAttribute("diaryRecordForm") @Validated DiaryRecordForm form,BindingResult resultForm,
			                    @ModelAttribute("fileUploadForm")  @Validated FileUploadForm file,BindingResult resultFile,
			                    Model model) throws Exception{
		if(resultFile.hasErrors() || resultForm.hasErrors()) {
			model.addAttribute("lists", DiaryRecordCategory.values());
			return "UserCalendar/Create";
		}
		
		DiaryRecordForm exist = diaryRecordService.findOneDiaryRecord(details.getUsername(), form.getCategoryId(), form.getDiaryDay());
		if(exist != null) {
			model.addAttribute("lists", DiaryRecordCategory.values());
			model.addAttribute("message","??????????????????????????????????????????????????????????????????");
			return "UserCalendar/Create";
		}
		
		String imageName = null;
		LocalDateTime dateTime = LocalDateTime.now();
		//?????????????????????????????????????????????????????????????????????????????????
		if(!file.getMultipartFile().isEmpty()){
			if(fileUploadService.fileValid(file)) {
				file.setCreateAt(dateTime);
				imageName = fileUploadService.fileUpload(file, s3Path,null);	
			}else {
				model.addAttribute("lists", DiaryRecordCategory.values());
				model.addAttribute("message","?????????????????????????????????");
				return "UserCalendar/Create";
			}
		}
		
		form.setUserName(details.getUsername());
		form.setCreateAt(dateTime);
		form.setImageName(imageName);
		diaryRecordService.insertDiaryRecord(form);
		
		return "redirect:/index";
	}
	
	@GetMapping("/index/record/{diaryDay}/{id}")
	@PreAuthorize("principal.username != 'guestuser'")
	public String showUserEditContent(@AuthenticationPrincipal AccountUserDetails details,
			                          @PathVariable("id")int id,
			                          @PathVariable("diaryDay")String diaryDay,
			                          @ModelAttribute("fileUploadForm") FileUploadForm file,Model model) throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date parsedDate =  format.parse(diaryDay);

		DiaryRecordForm form = diaryRecordService.findOneDiaryRecord(details.getUsername(), id, parsedDate);
		if(form == null) {
			return "error/404";
		}
		
		if(form.getImageName() != null) {
			String src = fileUploadService.fileDownload(s3Path, form.getImageName());
			model.addAttribute("exist", true);
			model.addAttribute("image", "data:image/jpg;base64," + src);
		} else {
			model.addAttribute("exist", false);
		}
		
		model.addAttribute("diaryRecordForm", form);
		model.addAttribute("lists", DiaryRecordCategory.values());
		return "UserCalendar/Edit";
	}
	
	@RequestMapping(value="/index/record/commit",method=RequestMethod.POST,params="update")
	@PreAuthorize("principal.username != 'guestuser'")
	public String updateContent(@AuthenticationPrincipal AccountUserDetails details,
			                    @ModelAttribute("diaryRecordForm") @Validated DiaryRecordForm form,BindingResult resultForm,
			                    @ModelAttribute("fileUploadForm")  @Validated FileUploadForm file,BindingResult resultFile,
			                    Model model) throws Exception{
		if(resultForm.hasErrors() || resultFile.hasErrors()) {
			model.addAttribute("lists", DiaryRecordCategory.values());
			return "UserCalendar/Edit";
		}
		
		DiaryRecordForm exist = diaryRecordService.findOneDiaryRecord(details.getUsername(), form.getCategoryId(), form.getDiaryDay());
		if(exist != null && !exist.getCreateAt().equals(form.getCreateAt()) ) {
			model.addAttribute("lists", DiaryRecordCategory.values());
			model.addAttribute("message","??????????????????????????????????????????????????????????????????");
			return "UserCalendar/Edit";
		}
		
		String imageName = form.getImageName();
		LocalDateTime dateTime = form.getCreateAt();
		//?????????????????????????????????????????????????????????????????????????????????
		if(!file.getMultipartFile().isEmpty()){
			if(fileUploadService.fileValid(file)) {
				file.setCreateAt(dateTime);
				imageName = fileUploadService.fileUpload(file, s3Path, imageName);	
			}else {
				model.addAttribute("lists", DiaryRecordCategory.values());
				model.addAttribute("message","?????????????????????????????????");
				return "UserCalendar/Edit";
			}
		}
		
		form.setUserName(details.getUsername());
		form.setImageName(imageName);
		diaryRecordService.updateDiaryRecord(form);
		return "redirect:/index";
	}
	
	@RequestMapping(value="/index/record/commit",method=RequestMethod.POST,params="delete")
	@PreAuthorize("principal.username != 'guestuser'")
	public String deleteContent(@AuthenticationPrincipal AccountUserDetails details,
			                    @ModelAttribute("diaryRecordForm") DiaryRecordForm form) {
		form.setUserName(details.getUsername());
		diaryRecordService.deleteDiaryRecord(form);
		return "redirect:/index";
	}

}
