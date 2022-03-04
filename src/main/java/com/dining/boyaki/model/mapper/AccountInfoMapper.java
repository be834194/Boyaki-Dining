package com.dining.boyaki.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.PasswordHistory;

@Mapper
public interface AccountInfoMapper {
	
	AccountInfo findAccountInfo(String userName);
	void updateAccountInfo(AccountInfo info);
	void updatePassword(Account account);
	void insertPasswordHistory(PasswordHistory history);
	void deleteAccount(String userName);
	
}
