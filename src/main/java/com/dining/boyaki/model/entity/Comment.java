package com.dining.boyaki.model.entity;

import java.time.LocalDateTime;

public class Comment {
	
	private long postId;
	
	private String userName;
	
	private String nickName;
	
	private String content;
	
	private LocalDateTime createAt;
	
	public Comment() {
		
	}
	
	public Comment(long postId, String userName, String nickName, String content,
			LocalDateTime createAt) {
		this.postId = postId;
		this.userName = userName;
		this.nickName = nickName;
		this.content = content;
		this.createAt = createAt;
	}
	
	public long getPostId() {
		return postId;
	}
	
	public void setPostId(long postId) {
		this.postId = postId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getNickName() {
		return nickName;
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public LocalDateTime getCreateAt() {
		return createAt;
	}
	
	public void setCreateat(LocalDateTime createAt) {
		this.createAt = createAt;
	}
	
}
