package com.dining.boyaki.model.service;

import org.springframework.stereotype.Service;

import com.dining.boyaki.model.entity.Comment;
import com.dining.boyaki.model.entity.Post;
import com.dining.boyaki.model.mapper.AdminMapper;

@Service
public class AdminService {
	
	private final AdminMapper adminMapper;

	public AdminService(AdminMapper adminMapper) {
		super();
		this.adminMapper = adminMapper;
	}
	
	public Post findPost(long postId) {
		return adminMapper.findPost(postId);
	}
	
	public void deletePost(long postId) {
		adminMapper.deletePost(postId);
	}
	
	public Comment findComment(long commentId) {
		return adminMapper.findComment(commentId);
	}
	
	public void deleteComment(long postId) {
		adminMapper.deleteComment(postId);
	}

}
