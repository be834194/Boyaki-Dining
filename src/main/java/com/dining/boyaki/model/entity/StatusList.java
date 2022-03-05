package com.dining.boyaki.model.entity;

public enum StatusList {
	
	NO(0,"健康状態問題ナシ"),
	DIEET(1,"ダイエット中"),
	LOCABO(2,"糖質制限中"),
	OTHERS(3,"禁酒・禁煙中");
	
	private int statusId;
	private String statusName;
	
	private StatusList(int statusId,String statusName) {
		this.statusId = statusId;
		this.statusName = statusName;
	}
	
	public int getStatusId() {
		return statusId;
	}
	
	public String getStatusName() {
		return statusName;
	}

}
