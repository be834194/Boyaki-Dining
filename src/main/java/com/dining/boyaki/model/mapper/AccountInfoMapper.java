package com.dining.boyaki.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.dining.boyaki.model.entity.AccountInfo;

@Mapper
public interface AccountInfoMapper {
	
	void updateAccountInfo(AccountInfo info);

}
