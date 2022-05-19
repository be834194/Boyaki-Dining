package com.dining.boyaki.model.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.Comment;
import com.dining.boyaki.model.entity.Post;
import com.dining.boyaki.model.mapper.AdminMapper;

@RunWith(SpringRunner.class)
@Transactional
public class AdminServiceTest {
	
	@Mock
	AdminMapper adminMapper;
	
	@InjectMocks
	AdminService adminService;
	
	Comment comment;
	Post post;
	
	@BeforeEach
    void setUp() {
    	MockitoAnnotations.openMocks(this);
    	comment = new Comment();
    	comment.setUserName("加藤健");
    	comment.setNickName("加藤健");
    	comment.setContent("応援しています");
    	post = new Post();
    	post.setUserName("糸井");
    	post.setNickName("sigeno");
    	post.setContent("sample");
    	when(adminMapper.findPost(3)).thenReturn(post);
    	when(adminMapper.findPost(30)).thenReturn(null);
    	when(adminMapper.findComment(4)).thenReturn(comment);
    	when(adminMapper.findComment(40)).thenReturn(null);
    	doNothing().when(adminMapper).deletePost(3);
    	doNothing().when(adminMapper).deleteComment(4);
    }
	
	@Test
	void findPostで投稿を1件取得する() throws Exception{
		Post result = adminService.findPost(3);
		assertEquals("糸井",result.getUserName());
		assertEquals("sigeno",result.getNickName());
		assertEquals("sample",result.getContent());
		verify(adminMapper,times(1)).findPost(3);
		
		result = adminService.findPost(30);
		assertEquals(null,result);
		verify(adminMapper,times(1)).findPost(30);
	}
	
	@Test
	void deletePostで投稿が1件削除される() throws Exception{
		adminService.deletePost(3);
		verify(adminMapper,times(1)).deletePost(3);
	}
	
	@Test
	void findCommentでコメントを1件取得する() throws Exception{
		Comment result = adminService.findComment(4);
		assertEquals("加藤健",result.getUserName());
		assertEquals("加藤健",result.getNickName());
		assertEquals("応援しています",result.getContent());
		verify(adminMapper,times(1)).findComment(4);
		
		result = adminService.findComment(40);
		assertEquals(null,result);
		verify(adminMapper,times(1)).findComment(40);
	}
	
	@Test
	void deleteCommentでコメントが1件削除される() throws Exception{
		adminService.deleteComment(4);
		verify(adminMapper,times(1)).deleteComment(4);
	}
}
