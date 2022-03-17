package com.dining.boyaki.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dining.boyaki.model.entity.AccountUserDetails;
import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.PostCategory;
import com.dining.boyaki.model.entity.StatusList;
import com.dining.boyaki.model.form.PostForm;
import com.dining.boyaki.model.service.PostService;

@Controller
public class PostController {
	
	private final PostService postService;
	
	public PostController(PostService postService) {
		this.postService = postService;
	}
	
	@GetMapping("/index/boyaki")
	String showPostIndex(Model model) {
		model.addAttribute("postCategory", PostCategory.values());
		model.addAttribute("statusList", StatusList.values());
		return "Post/PostIndex";
	}
	
	@GetMapping("/index/boyaki/{nickName}")
	String showProfile(@PathVariable("nickName")String nickName,Model model) {
		AccountInfo info = postService.findProfile(nickName);
		if(info == null) {
			return "Common/404";
		}
		model.addAttribute("accountInfo",info);
		model.addAttribute("statusList", StatusList.values());
		return "Post/Profile";
	}
	
	@GetMapping("/index/boyaki/post")
	String showPostCreate(@AuthenticationPrincipal AccountUserDetails details,
			              Model model) {
		PostForm form = new PostForm();
		String nickName = postService.findNickName(details.getUsername());
		form.setUserName(details.getUsername());
		form.setNickName(nickName);
		
		model.addAttribute("postForm",form);
		model.addAttribute("postCategory", PostCategory.values());
		return "Post/PostCreate";
	}
	
	@PostMapping("/index/boyaki/post/insert")
	String insertPost(@ModelAttribute("postForm") @Validated PostForm form,
			          BindingResult result,Model model) {
		if(result.hasErrors()) {
			model.addAttribute("postCategory", PostCategory.values());
			return "Post/PostCreate";
		}
		postService.insertPost(form);
		return "redirect:/index/boyaki";
	}
}
