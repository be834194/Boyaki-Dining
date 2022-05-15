package com.dining.boyaki.model.entity;

import java.time.LocalDateTime;

public class Post {
	private String userName;
	 
	private String nickName;
	 
	private String content;
	 
	private int postCategory;
	 
	private LocalDateTime createAt;
	
	public Post() {
		
	}

	public Post(String userName, String nickName, String content, int postCategory, LocalDateTime createAt) {
		this.userName = userName;
		this.nickName = nickName;
		this.content = content;
		this.postCategory = postCategory;
		this.createAt = createAt;
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

	public int getPostCategory() {
		return postCategory;
	}

	public void setPostCategory(int postCategory) {
		this.postCategory = postCategory;
	}

	public LocalDateTime getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}

}
