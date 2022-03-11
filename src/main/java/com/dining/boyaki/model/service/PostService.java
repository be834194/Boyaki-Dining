package com.dining.boyaki.model.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.Post;
import com.dining.boyaki.model.entity.PostRecord;
import com.dining.boyaki.model.form.PostForm;
import com.dining.boyaki.model.mapper.PostMapper;

@Service
public class PostService {
	
	private final PostMapper postMapper;
	
	public PostService(PostMapper postMapper) {
		this.postMapper = postMapper;
	}
	
	@Transactional(readOnly = true)
	public List<PostRecord> searchPostRecord(int[] category,int[] status,
			                                 String text) {
		if(category.length == 0) {
			category = null;
		}
		if(status.length == 0) {
			status = null;
		}
		
		String[] content;
		if(text == null || text.equals("")) {
			content = null;
		}else {
		    content = text.replaceAll("　", " ").replaceAll("	", " ")
		    		      .split(" ");
		    if(content.length == 0) {
		    	content = null;
		    }
		}
		
		List<PostRecord> records = postMapper
				                  .searchPostRecord(category,status,content);
	    return records;
	}
	
	@Transactional(readOnly = true)
	public String findNickName(String userName) {
		return postMapper.findNickName(userName);
	}
	
	@Transactional(readOnly = true)
	public AccountInfo findProfile(String nickName) {
		return postMapper.findProfile(nickName);
	}
	
	@Transactional(readOnly = false)
	public void insertPost(PostForm form) {
		Post post = setToPost(form);
		postMapper.insertPost(post);
	}
	
	public Post setToPost(PostForm form) {
		Post post = new Post();
		post.setUserName(form.getUserName());
		post.setNickName(form.getNickName());
		post.setContent(form.getContent());
		post.setPostCategory(form.getPostCategory());
		post.setCreateAt(LocalDateTime.now());
		return post;
	}

}
