package com.dining.boyaki.model.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.time.LocalDateTime;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import com.dining.boyaki.model.entity.AccountInfo;
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
	
	int[] category;
	int[] status;
	String[] content;
	
	@BeforeEach
	void setUp() {
		category = null;
		status = null;
		content = null;
	}
	
	@AfterEach
	void tearDown() {
		session.flushStatements();
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Post/setup/")
	void findNickNameでニックネームを1件取得する() throws Exception{
		String nickName = postMapper.findNickName("糸井");
		assertEquals("sigeno",nickName);
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Post/setup/")
	void findProfileでユーザ情報レコードを1件取得する() throws Exception{
		AccountInfo info = postMapper.findProfile("匿名");
		assertEquals("匿名",info.getNickName());
		assertEquals("5000兆円欲しい！！！",info.getProfile());
		assertEquals(1,info.getStatus());
		assertEquals(2,info.getGender());
		assertEquals(2,info.getAge());
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Post/setup/")
	void findPostRecordでユーザ一人の投稿を全件取得する() throws Exception{
		List<PostRecord> record = postMapper.findPostRecord("sigeno", PageRequest.of(0, 5));
		assertEquals(5,record.size());
		assertEquals("11",record.get(0).getPostId());
		assertEquals("sigeno",record.get(0).getNickName());
		assertEquals("ラジオで聞いた話ですが、睡眠時間が8→6時間に減ると毛穴が二倍に広がるそうです",record.get(0).getContent());
		assertEquals("グチ・ぼやき",record.get(0).getPostCategory());
		assertEquals("中性脂肪・コレステロール高め",record.get(0).getStatus());
		assertEquals("2022-03-07 22:17:49",record.get(0).getCreateAt());
		assertEquals("2022-03-03 19:32:44",record.get(1).getCreateAt());
		assertEquals("2022-03-01 18:07:15",record.get(3).getCreateAt());
		assertEquals("2022-03-01 12:07:27",record.get(4).getCreateAt());
		
		record = postMapper.findPostRecord("sigeno", PageRequest.of(1, 5));
		assertEquals(1,record.size());
		assertEquals("2022-03-01 12:06:21",record.get(0).getCreateAt());
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Post/setup/")
	void searchPostRecordで全件取得する() throws Exception{
		List<PostRecord> record = postMapper.searchPostRecord(category,status,content,
				                                              PageRequest.of(0, 5));
		assertEquals(5,record.size());
		assertEquals("11",record.get(0).getPostId());
		assertEquals("sigeno",record.get(0).getNickName());
		assertEquals("ラジオで聞いた話ですが、睡眠時間が8→6時間に減ると毛穴が二倍に広がるそうです",record.get(0).getContent());
		assertEquals("グチ・ぼやき",record.get(0).getPostCategory());
		assertEquals("中性脂肪・コレステロール高め",record.get(0).getStatus());
		assertEquals("2022-03-07 22:17:49",record.get(0).getCreateAt());
		assertEquals("2022-03-03 19:32:44",record.get(1).getCreateAt());
		assertEquals("2022-03-02 12:55:08",record.get(3).getCreateAt());
		assertEquals("2022-03-02 11:12:50",record.get(4).getCreateAt());
		
		record = postMapper.searchPostRecord(category,status,content,
                                             PageRequest.of(2, 5));
		assertEquals(1,record.size());
		assertEquals("2022-02-28 23:30:34",record.get(0).getCreateAt());
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Post/setup/")
	void searchPostRecordで一つの条件で絞り込んで取得する() throws Exception{
		category = new int[]{5,3};
		List<PostRecord> record = postMapper.searchPostRecord(category,status,null,
				                                              PageRequest.of(0, 5));
		assertEquals(2,record.size());
		assertEquals("運動・筋トレ",record.get(0).getPostCategory());
		assertEquals("2022-03-02 00:02:49",record.get(0).getCreateAt());
		assertEquals("塩分",record.get(1).getPostCategory());
		assertEquals("2022-03-01 18:29:51",record.get(1).getCreateAt());
		
		status = new int[]{1,7};
		record = postMapper.searchPostRecord(null,status,null,
				                             PageRequest.of(0, 5));
		assertEquals(5,record.size());
		assertEquals("尿酸値高め",record.get(0).getStatus());
		assertEquals("2022-03-03 18:41:36",record.get(0).getCreateAt());
		assertEquals("ダイエット中",record.get(1).getStatus());
		assertEquals("2022-03-02 11:12:50",record.get(1).getCreateAt());
		assertEquals("2022-03-01 18:29:51",record.get(3).getCreateAt());
		assertEquals("2022-02-28 23:30:34",record.get(4).getCreateAt());
		
		content = new String[]{"改善"};
		record = postMapper.searchPostRecord(null,null,content,
				                             PageRequest.of(0, 5));
		assertEquals(1,record.size());
		assertTrue(record.get(0).getContent().contains("改善"));
		assertEquals("2022-03-02 12:55:08",record.get(0).getCreateAt());
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Post/setup/")
	void searchPostRecordで二つの条件で絞り込んで取得する() throws Exception{
		category = new int[]{3,7};
		status = new int[]{1,6};
		content = new String[]{"検査"};
		
		List<PostRecord> record = postMapper.searchPostRecord(category,status,null,
				                                              PageRequest.of(0, 5));
		assertEquals(1,record.size());
		assertEquals("ダイエット中",record.get(0).getStatus());
		assertEquals("塩分",record.get(0).getPostCategory());
		assertEquals("2022-03-01 18:29:51",record.get(0).getCreateAt());
		
		record = postMapper.searchPostRecord(null,status,content,
				                             PageRequest.of(0, 5));
		assertEquals(1,record.size());
		assertEquals("ダイエット中",record.get(0).getStatus());
		assertTrue(record.get(0).getContent().contains("検査"));
		assertEquals("2022-03-01 18:29:51",record.get(0).getCreateAt());
		
		record = postMapper.searchPostRecord(category,null,content,
				                             PageRequest.of(0, 5));
		assertEquals(2,record.size());
		assertEquals("尿酸値",record.get(0).getPostCategory());
		assertTrue(record.get(0).getContent().contains("検査"));
		assertEquals("2022-03-03 18:41:36",record.get(0).getCreateAt());
		assertEquals("塩分",record.get(1).getPostCategory());
		assertTrue(record.get(1).getContent().contains("検査"));
		assertEquals("2022-03-01 18:29:51",record.get(1).getCreateAt());
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Post/setup/")
	void searchPostRecordで投稿情報を絞り込んで取得する() throws Exception{
		category = new int[]{1,2,3};
		status = new int[]{1,7};
		content = new String[]{"ダイエット","効果"};
		List<PostRecord> record = postMapper.searchPostRecord(category,status,content,
				                                              PageRequest.of(0, 5));
		assertEquals(1,record.size());
		assertEquals("ダイエット",record.get(0).getPostCategory());
		assertEquals("尿酸値高め",record.get(0).getStatus());
		assertEquals("加藤健",record.get(0).getNickName());
		assertEquals("サイゼリヤのサラダがダイエットに効果あるらしい",record.get(0).getContent());
		assertEquals("2022-02-28 23:30:34",record.get(0).getCreateAt());
	}
	
	@Test
	@DatabaseSetup(value = "/mapper/Post/setup/")
	@ExpectedDatabase(value = "/mapper/Post/insert/",table="post"
	                 ,assertionMode=DatabaseAssertionMode.NON_STRICT)
	void insertPostで投稿が1件追加される() throws Exception{
		Post post = new Post();
		post.setUserName("miho");
		post.setNickName("匿名");
		post.setContent("糖質制限ってどこまでやればいいの～？");
		post.setPostCategory(2);
		post.setCreateAt(LocalDateTime.parse("2022-03-08T09:31:12"));
		postMapper.insertPost(post);
	}

}
