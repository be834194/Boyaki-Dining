package com.dining.boyaki.model.mapper;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.time.LocalDateTime;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.Post;
import com.dining.boyaki.model.entity.PostRecord;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
                         TransactionalTestExecutionListener.class,
                         DbUnitTestExecutionListener.class})
@MybatisTest
@Transactional
public class PostMapperTest {
	
	@Autowired
	PostMapper postMapper;
	
	@Autowired
	SqlSession session;
	
	@AfterEach
	void tearDown() {
		session.flushStatements();
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Post/setup/")
	void findAccountInfoでユーザ情報レコードを1件取得する() throws Exception{
		String userName = postMapper.findNickName("糸井");
		assertEquals("sigeno",userName);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Post/setup/")
	void searchPostRecordで投稿情報を絞り込んで取得する() throws Exception{
		int[] category = new int[]{1,2};
		int[] status = new int[]{1,3,5};
		String[] content = new String[]{"サイゼ","ダイエット"};
		List<PostRecord> record = postMapper.searchPostRecord(category,status,content);
		assertEquals(1,record.size());
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Post/setup/")
	@ExpectedDatabase(value = "/mapper/Post/insert/",table="post")
	void insertPostで投稿が1件追加される() throws Exception{
		Post post = new Post();
		post.setUserName("miho");
		post.setNickName("匿名");
		post.setContent("糖質制限ってどこまでやればいいの～？");
		post.setPostCategory(2);
		post.setCreateAt(LocalDateTime.parse("2022-03-03T09:31:12"));
		postMapper.insertPost(post);
	}

}
