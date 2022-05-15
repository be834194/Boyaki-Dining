package com.dining.boyaki.model.form;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class CommentForm implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private long postId;
	
	private String userName;
	
	private String nickName;
	
	@Size(max=100,message="100文字以内で入力してください")
	@NotEmpty(message="投稿内容は必須項目です")
	private String content;
	
	public CommentForm() {
		
	}

	public CommentForm(long postId, String userName, String nickName,
			@Size(max = 100, message = "100文字以内で入力してください") @NotEmpty(message = "投稿内容は必須項目です") String content) {
		super();
		this.postId = postId;
		this.userName = userName;
		this.nickName = nickName;
		this.content = content;
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
	
}
