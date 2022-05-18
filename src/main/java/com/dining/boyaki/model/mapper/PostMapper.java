package com.dining.boyaki.model.mapper;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.Comment;
import com.dining.boyaki.model.entity.CommentRecord;
import com.dining.boyaki.model.entity.Post;
import com.dining.boyaki.model.entity.PostRecord;

@Mapper
public interface PostMapper {
	
	String findNickName(String userName);
	AccountInfo findProfile(String nickName);
	
	void insertPost(Post post);
	void deletePost(@Param("userName")String userName,
			        @Param("postId")long postId);
	PostRecord findOnePostRecord(long postId);
	List<PostRecord> findPostRecord(@Param("nickName")String nickName,
                                    @Param("pageable")Pageable pageable);
	List<PostRecord> searchPostRecord(@Param("category") int[] category,
                                      @Param("status") int[] status,
                                      @Param("content") String[] content,
                                      @Param("pageable")Pageable pageable);
	
	void insertComment(Comment comment);
	List<CommentRecord> findCommentRecord(@Param("postId")long postId,
			                              @Param("pageable")Pageable pageable);
	
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
