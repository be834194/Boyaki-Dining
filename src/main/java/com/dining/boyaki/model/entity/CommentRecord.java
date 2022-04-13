package com.dining.boyaki.model.entity;

public class CommentRecord {
	
	private String nickName;
	
	private String status;
	
	private String content;
	
	private String createAt;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateAt() {
		return createAt;
	}

	public void setCreateat(String createAt) {
		this.createAt = createAt;
	}

}
