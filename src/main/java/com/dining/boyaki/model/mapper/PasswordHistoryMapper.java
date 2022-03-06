package com.dining.boyaki.model.mapper;

import java.util.List;

import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.dining.boyaki.model.entity.PasswordHistory;

@Mapper
public interface PasswordHistoryMapper {
	
	String findPassword(@Param("userName") String username,
			           @Param("mail") String mail);
	List<PasswordHistory> findUseFrom(@Param("userName") String username,
                                      @Param("useDay") LocalDateTime useDay);
}
