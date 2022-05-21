package com.dining.boyaki.model.service.conbined;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.Comment;
import com.dining.boyaki.model.entity.Post;
import com.dining.boyaki.model.service.AdminService;
import com.dining.boyaki.util.CsvDataSetLoader;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class AdminServiceCombinedTest {
	
	@Autowired
	AdminService adminService;
	
	@Test
	@DatabaseSetup(value = "/service/Admin/setup/")
	void findPostで投稿を1件取得する() throws Exception{
		Post result = adminService.findPost(3);
		assertEquals("糸井",result.getUserName());
		assertEquals("sigeno",result.getNickName());
		assertEquals("ドーナツは穴が開いてるからゼロカロリーって本当？",result.getContent());
		
		result = adminService.findPost(30);
		assertEquals(null,result);
	}
	
	@Test
	@DatabaseSetup(value = "/service/Admin/setup/")
	@ExpectedDatabase(value = "/service/Admin/delete/post/",table="post")
	void deletePostで投稿が1件削除される() throws Exception{
		adminService.deletePost(3);
	}
	
	@Test
	@DatabaseSetup(value = "/service/Admin/setup/")
	void findCommentでコメントを1件取得する() throws Exception{
		Comment result = adminService.findComment(4);
		assertEquals("加藤健",result.getUserName());
		assertEquals("加藤健",result.getNickName());
		assertEquals("応援してます",result.getContent());
		
		result = adminService.findComment(40);
		assertEquals(null,result);
	}
	
	@Test
	@DatabaseSetup(value = "/service/Admin/setup/")
	@ExpectedDatabase(value = "/service/Admin/delete/comment/",table="comment")
	void deleteCommentでコメントが1件削除される() throws Exception{
		adminService.deleteComment(4);
	}

}
