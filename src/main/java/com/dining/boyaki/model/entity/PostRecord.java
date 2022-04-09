package com.dining.boyaki.model.entity;

public class PostRecord {
	
	private String postId; //postから
	
	private String userName; //postから
	
	private String nickName; //postから
	
	private String status;   //AccountInfo+StatusListから
	
	private String postCategory; //PostCategoryから
	
	private String content; //Postから
	
	private String createAt;  //Postから
	
	public PostRecord() {
		
	}

	public PostRecord(String postId, String userName, String nickName, String status, String postCategory,
			String content, String createAt) {
		this.postId = postId;
		this.userName = userName;
		this.nickName = nickName;
		this.status = status;
		this.postCategory = postCategory;
		this.content = content;
		this.createAt = createAt;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPostCategory() {
		return postCategory;
	}

	public void setPostCategory(String postCategory) {
		this.postCategory = postCategory;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateAt() {
		return createAt;
	}

	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}
	
}
