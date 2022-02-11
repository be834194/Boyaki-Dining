package com.dining.boyaki.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.PasswordHistory;

@Mapper
public interface RegistrationMapper {
	
	public void insertAccount(Account account);
	public void insertPasswordHistory(PasswordHistory history);

}
