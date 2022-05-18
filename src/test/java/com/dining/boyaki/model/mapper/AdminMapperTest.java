package com.dining.boyaki.model.mapper;

import static org.junit.Assert.assertEquals;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.Comment;
import com.dining.boyaki.model.entity.Post;
import com.dining.boyaki.util.CsvDataSetLoader;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
                         TransactionalTestExecutionListener.class,
                         DbUnitTestExecutionListener.class})
@MybatisTest
@Transactional
public class AdminMapperTest {
	
	@Autowired
	AdminMapper adminMapper;
	
	@Autowired
	SqlSession session;
	
	@AfterEach
	void tearDown() {
		session.flushStatements();
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Admin/setup/")
	void findPostで投稿を1件取得する() throws Exception{
		Post result = adminMapper.findPost(3);
		assertEquals("糸井",result.getUserName());
		assertEquals("sigeno",result.getNickName());
		assertEquals("ドーナツは穴が開いてるからゼロカロリーって本当？",result.getContent());
		
		result = adminMapper.findPost(30);
		assertEquals(null,result);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Admin/setup/")
	@ExpectedDatabase(value = "/mapper/Admin/delete/post/",table="post")
	void deletePostで投稿が1件削除される() throws Exception{
		adminMapper.deletePost(3);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Admin/setup/")
	void findCommentでコメントを1件取得する() throws Exception{
		Comment result = adminMapper.findComment(4);
		assertEquals("加藤健",result.getUserName());
		assertEquals("加藤健",result.getNickName());
		assertEquals("応援してます",result.getContent());
		
		result = adminMapper.findComment(40);
		assertEquals(null,result);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Admin/setup/")
	@ExpectedDatabase(value = "/mapper/Admin/delete/comment/",table="comment")
	void deleteCommentでコメントが1件削除される() throws Exception{
		adminMapper.deleteComment(4);
	}

}
