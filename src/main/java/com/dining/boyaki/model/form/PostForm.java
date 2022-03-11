package com.dining.boyaki.model.form;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotEmpty;

public class PostForm {
	
	private String userName;
	
	private String nickName;
	
	@Size(max=100,message="100文字以内で入力してください")
	@NotEmpty(message="投稿内容は必須項目です")
	private String content;
	
	private int postCategory;

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
	
}
