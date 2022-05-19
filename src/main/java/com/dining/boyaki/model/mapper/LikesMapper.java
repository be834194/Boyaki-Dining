package com.dining.boyaki.model.mapper;

import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikesMapper {
	
	Optional<Integer> currentRate(@Param("postId")long postId,
                                  @Param("userName")String userName);
	
	Optional<Integer> sumRate(@Param("postId")long postId);
	
	void insertRate(@Param("postId")long postId,
	                @Param("userName")String userName,
	                @Param("rate")int rate);
	
	void updateRate(@Param("postId")long postId,
	                @Param("userName")String userName,
	               @Param("rate")int rate);

}
