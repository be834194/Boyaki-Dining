package com.dining.boyaki.model.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.dining.boyaki.model.entity.Post;
import com.dining.boyaki.model.entity.PostRecord;

@Mapper
public interface PostMapper {
	
	String findNickName(String userName);
	void insertPost(Post post);
	List<PostRecord> searchPostRecord(@Param("category") int[] category,
			                          @Param("status") int[] status,
			                          @Param("content") String[] content
			);
	
}
