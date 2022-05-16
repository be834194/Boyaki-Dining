package com.dining.boyaki.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.dining.boyaki.model.entity.Comment;
import com.dining.boyaki.model.entity.Post;

@Mapper
public interface AdminMapper {
	
	public Post findPost(long postId);
	public void deletePost(long commentId);
	
	public Comment findComment(long commentId);
	public void deleteComment(long commentId);

}
