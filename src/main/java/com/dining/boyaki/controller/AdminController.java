package com.dining.boyaki.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.dining.boyaki.model.entity.Comment;
import com.dining.boyaki.model.entity.Post;
import com.dining.boyaki.model.service.AdminService;

@Controller
public class AdminController {
	
	AdminService adminService;
	
	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@GetMapping("/admin")
	public String showAdminIndex(Model model) {
		return "Admin/AdminIndex";
	}
	
	@PostMapping("/admin/post/confrom")
	public String showConfirmPost(long postId,Model model) {
		Post post = adminService.findPost(postId);
		if(post == null) {
			return "error/404";
		}
		model.addAttribute("post", post);
		model.addAttribute("postId", postId);
		return "Admin/ConfirmPost";
	}
	
	@PostMapping("/admin/post/delete")
	public String deletePost(long postId,Model model) {
		adminService.deletePost(postId);
		return "redirect:/admin";
	}
	
	@PostMapping("/admin/comment/confrom")
	public String showConfirmComment(long commentId,Model model) {
		Comment comment = adminService.findComment(commentId);
		if(comment == null) {
			return "error/404";
		}
		model.addAttribute("comment", comment);
		model.addAttribute("commentId", commentId);
		return "Admin/ConfirmComment";
	}
	
	@PostMapping("/admin/comment/delete")
	public String deleteComment(long commentId,Model model) {
		adminService.deleteComment(commentId);
		return "redirect:/admin";
	}

}
