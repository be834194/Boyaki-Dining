package com.dining.boyaki.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.PasswordHistory;

@Mapper
public interface RegistrationMapper {
	
	void insertAccount(Account account);
	void insertPasswordHistory(PasswordHistory history);
	void insertAccountInfo(AccountInfo info);

}
