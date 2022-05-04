package com.dining.boyaki.model.entity;

public enum PostCategory {
	
	BOYAKI(0,"グチ・ぼやき"),
	DIET(1,"ダイエット"),
	LOCABO(2,"糖質"),
	SALTY(3,"塩分"),
	TEMPERANCE(4,"禁酒・禁煙"),
	MUSCLE(5,"運動・筋トレ"),
	LIPIDS(6,"中性脂肪・コレステロール"),
	URICACID(7,"尿酸値"),
	BLOOD(8,"血圧"),
	OTHER(9,"その他");
	
	private int postId;
	private String postName;
	
	private PostCategory(int postId,String postName) {
		this.postId = postId;
		this.postName = postName;
	}
	
	public int getPostId() {
		return postId;
	}
	
	public String getPostName() {
		return postName;
	}

}
