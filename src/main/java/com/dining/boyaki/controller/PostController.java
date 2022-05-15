package com.dining.boyaki.controller;

import java.net.URI;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.UriComponentsBuilder;

import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.AccountUserDetails;
import com.dining.boyaki.model.entity.PostRecord;
import com.dining.boyaki.model.enums.PostCategory;
import com.dining.boyaki.model.enums.StatusList;
import com.dining.boyaki.model.form.CommentForm;
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
	
	@GetMapping("/index/boyaki/{postId}")
	@PreAuthorize("principal.username != 'guestuser'")
	String showPostDetail(@AuthenticationPrincipal AccountUserDetails details,
			              @PathVariable("postId")long postId,Model model) {
		PostRecord record = postService.findOnePostRecord(postId);
		if(record == null) {
			return "error/404";
		}
		model.addAttribute("postRecord",record);
		
		CommentForm comment = new CommentForm();
		String nickName = postService.findNickName(details.getUsername());
		comment.setUserName(details.getUsername());
		comment.setNickName(nickName);
		comment.setPostId(postId);
		model.addAttribute("commentForm",comment);
		
		if(record.getUserName().equals(details.getUsername())) {
			model.addAttribute("ableDeleted","true");
		}else {
			model.addAttribute("ableDeleted","false");
		}
		
		int sumRate = postService.sumRate(postId);
		model.addAttribute("sumRate",sumRate);
		
		return "Post/PostDetail";
	}
	
	@PostMapping("/index/boyaki/comment")
	@PreAuthorize("principal.username != 'guestuser'")
	String insertComment(@ModelAttribute("commentForm")@Validated CommentForm form,
	                     BindingResult result,UriComponentsBuilder builder,
	                     RedirectAttributes model) {
		URI location = builder.path("/index/boyaki/" + form.getPostId()).build().toUri();
		if(result.hasErrors()) {
			model.addFlashAttribute("validMessage", "1～100文字以内でコメントを入力してください");
			return "redirect:" + location.toString();
		}
		postService.insertComment(form);
		
		return "redirect:" + location.toString();
	}
	
	@PostMapping("/index/boyaki/rate")
	@PreAuthorize("principal.username != 'guestuser'")
	String updateRate(@AuthenticationPrincipal AccountUserDetails details,
			          @RequestParam(value="postId")long postId,Model model) {
		postService.updateRate(postId, details.getUsername());
		
		int sumRate = postService.sumRate(postId);
		model.addAttribute("sumRate",sumRate);
		return "Post/PostDetail :: rateFragment";
	}
	
	@GetMapping("/index/boyaki/profile/{nickName}")
	@PreAuthorize("principal.username != 'guestuser'")
	String showUserProfile(@AuthenticationPrincipal AccountUserDetails details,
			               @PathVariable("nickName")String nickName,Model model) {
		AccountInfo info = postService.findProfile(nickName);
		if(info == null) {
			return "error/404";
		}
		model.addAttribute("accountInfo", info);
		model.addAttribute("statusList", StatusList.values());
		return "Post/Profile";
	}
	
	@GetMapping("/index/boyaki/post")
	@PreAuthorize("principal.username != 'guestuser'")
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
	
	@PostMapping("/index/boyaki/post/delete")
	String deletePost(@AuthenticationPrincipal AccountUserDetails details,
			          @RequestParam(value="postId")long postId) {
		postService.deletePost(details.getUsername(),postId);
		return "redirect:/index/boyaki";
	}
	
	@PostMapping("/index/boyaki/post/insert")
	@PreAuthorize("principal.username != 'guestuser'")
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
