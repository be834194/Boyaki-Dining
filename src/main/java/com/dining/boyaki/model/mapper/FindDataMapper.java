package com.dining.boyaki.model.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FindDataMapper {
	
	public String findUserName(String userName);
	public String findUserNameFromMail(String mail);
	public String findMail(String mail);

}
