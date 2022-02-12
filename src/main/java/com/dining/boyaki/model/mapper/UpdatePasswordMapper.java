package com.dining.boyaki.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.PasswordHistory;

@Mapper
public interface UpdatePasswordMapper {
	
	public void updatePassword(Account account);
	public void insertPasswordHistory(PasswordHistory history);

}
