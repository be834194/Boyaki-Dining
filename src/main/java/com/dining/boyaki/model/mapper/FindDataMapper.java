package com.dining.boyaki.model.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dining.boyaki.model.entity.DiaryRecord;

@Mapper
public interface FindDataMapper {
	
	String findUserName(String userName);
	String findUserNameFromMail(String mail);
	String findMail(String mail);
	DiaryRecord findOneDiaryRecord(@Param("userName")String userName,
                                   @Param("categoryId")int categoryId,
                                   @Param("diaryDay")Date diaryDay);
	String findNickName(String nickName);

}
