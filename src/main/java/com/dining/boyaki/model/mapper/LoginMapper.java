package com.dining.boyaki.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.dining.boyaki.model.entity.Account;

@Mapper
public interface LoginMapper {
	
	Account findAccount(String mail);

}
