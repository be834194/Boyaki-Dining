package com.dining.boyaki.model.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.Comment;
import com.dining.boyaki.model.entity.CommentRecord;
import com.dining.boyaki.model.entity.Post;
import com.dining.boyaki.model.entity.PostRecord;
import com.dining.boyaki.model.form.CommentForm;
import com.dining.boyaki.model.form.PostForm;
import com.dining.boyaki.model.mapper.PostMapper;

@Service
public class PostService {
	
	private final PostMapper postMapper;
	
	public PostService(PostMapper postMapper) {
		this.postMapper = postMapper;
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
		Post post = new Post(form.getUserName(),form.getNickName(),form.getContent(),
				             form.getPostCategory(),LocalDateTime.now());
		postMapper.insertPost(post);
	}
	
	@Transactional(readOnly = false)
	public void deletePost(String userName,long postId) {
		postMapper.deletePost(userName, postId);
	}
	
	@Transactional(readOnly = true)
	public PostRecord findOnePostRecord(long postId) {
		return postMapper.findOnePostRecord(postId);
	}
	
	@Transactional(readOnly = false)
	public void insertComment(CommentForm form) {
		Comment comment = new Comment(form.getPostId(),form.getUserName(),form.getNickName(),form.getContent(),
				                      LocalDateTime.now());
		postMapper.insertComment(comment);
	}
	
	@Transactional(readOnly = true)
	public List<CommentRecord> findCommentList(long postId, int page){
		return postMapper.findCommentRecord(postId, PageRequest.of(page, 5));
	}
	
	@Transactional(readOnly = true)
	public List<PostRecord> findPostRecord(String nickName,int page) {
		List<PostRecord> records = postMapper.findPostRecord(nickName, PageRequest.of(page, 5));
		return records;
	}
	
	@Transactional(readOnly = true)
	public List<PostRecord> searchPostRecord(int[] category,int[] status,
			                                 String text,int page) {
		if(category.length == 0) {
			category = null;
		}
		if(status.length == 0) {
			status = null;
		}
		
		String[] content;
		if(text == null || text.equals("")) {
			content = null;
		}else { //??????,???????????????
		    content = text.replaceAll("???", " ").replaceAll("	", " ")
		    		      .split(" ");
		    if(content.length == 0) {
		    	content = null;
		    }
		}
		
		List<PostRecord> records = postMapper
				                  .searchPostRecord(category,status,content,
				                		            PageRequest.of(page, 5));
		return records;
	}

}
