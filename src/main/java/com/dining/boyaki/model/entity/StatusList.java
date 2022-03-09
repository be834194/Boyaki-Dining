package com.dining.boyaki.model.entity;

public enum StatusList {
	
	NO(0,"健康状態問題ナシ"),
	DIET(1,"ダイエット中"),
	LOCABO(2,"糖質制限中"),
	SALTY(3,"塩分制限中"),
	TEMPERANCE(4,"禁酒・禁煙中"),
	MUSCLE(5,"筋トレ中"),
	LIPIDS(6,"中性脂肪・コレステロール高め"),
	URICACID(7,"尿酸値高め"),
	BLOOD(8,"血圧高め");
	
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
