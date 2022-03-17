package com.dining.boyaki.model.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.Post;
import com.dining.boyaki.model.entity.PostRecord;

@Mapper
public interface PostMapper {
	
	String findNickName(String userName);
	AccountInfo findProfile(String nickName);
	void insertPost(Post post);
	List<PostRecord> searchPostRecord(@Param("category") int[] category,
			                          @Param("status") int[] status,
			                          @Param("content") String[] content,
			                          @Param("pageable")Pageable pageable
			                          );
	
}
