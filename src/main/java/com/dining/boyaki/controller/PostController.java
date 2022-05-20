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
import com.dining.boyaki.model.service.LikesService;
import com.dining.boyaki.model.service.PostService;

@Controller
public class PostController {
	
	private final LikesService likesService;
	
	private final PostService postService;
	
	public PostController(LikesService likesService,PostService postService) {
		this.likesService = likesService;
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
		
		String nickName = postService.findNickName(details.getUsername());
		CommentForm comment = new CommentForm(postId,details.getUsername(),nickName,null);
		model.addAttribute("commentForm",comment);
		
		//削除ボタンの有無
		if(record.getUserName().equals(details.getUsername())) {
			model.addAttribute("ableDeleted","true");
		}else {
			model.addAttribute("ableDeleted","false");
		}
		
		//総いいね数の取得
		int sumRate = likesService.sumRate(postId);
		model.addAttribute("sumRate",sumRate);
		//投稿に対する現在の評価状態を取得
		int currentRate = likesService.currentRate(postId,details.getUsername());
		model.addAttribute("currentRate",currentRate);
		
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
		likesService.updateRate(postId, details.getUsername());
		
		//総いいね数の取得
		int sumRate = likesService.sumRate(postId);
		model.addAttribute("sumRate",sumRate);
		//投稿に対する現在の評価状態を取得
		int currentRate = likesService.currentRate(postId,details.getUsername());
		model.addAttribute("currentRate",currentRate);
		
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
		String nickName = postService.findNickName(details.getUsername());
		PostForm form = new PostForm(details.getUsername(),nickName,null,1);
		
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
