package com.dining.boyaki.model.service.conbined;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.PostRecord;
import com.dining.boyaki.model.form.PostForm;
import com.dining.boyaki.model.service.PostService;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class PostServiceCombinedTest {
	
	private static LocalDateTime datetime = LocalDateTime.parse("2022-03-03T09:31:12");
	
	private static MockedStatic<LocalDateTime> mock;
	
	@Autowired
	PostService postService;
	
	int[] category;
	int[] status;
	String text;
	
	@BeforeEach
    void setUp() {
    	mock = Mockito.mockStatic(LocalDateTime.class,Mockito.CALLS_REAL_METHODS);
    	mock.when(LocalDateTime::now).thenReturn(datetime);
    	category = new int[] {};
		status = new int[] {};
		text = "　 		";
    }
	
	@AfterEach
	void tearDown() throws Exception{
		mock.close();
	}
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	void searchPostRecordで全件取得する() throws Exception{
        List<PostRecord> record = postService.searchPostRecord(category,status,text,0);
        assertEquals(5,record.size());
        assertEquals("2022-03-03 18:41:36",record.get(0).getCreateAt());
		assertEquals("2022-03-02 12:55:08",record.get(1).getCreateAt());
		assertEquals("2022-03-02 00:02:49",record.get(3).getCreateAt());
		assertEquals("2022-03-01 18:29:51",record.get(4).getCreateAt());
		
		record = postService.searchPostRecord(category,status,text,1);
		assertEquals(2,record.size());
		assertEquals("2022-03-01 12:07:27",record.get(0).getCreateAt());
		assertEquals("2022-02-28 23:30:34",record.get(1).getCreateAt());
	}
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	void searchPostRecordで一つの条件で絞り込んで取得する() throws Exception{
		category = new int[]{5,3};
		List<PostRecord> record = postService.searchPostRecord(category,status,text,0);
		assertEquals(2,record.size());
		assertEquals("運動・筋トレ",record.get(0).getPostCategory());
		assertEquals("2022-03-02 00:02:49",record.get(0).getCreateAt());
		assertEquals("塩分",record.get(1).getPostCategory());
		assertEquals("2022-03-01 18:29:51",record.get(1).getCreateAt());
		
		category = new int[] {};
		status = new int[]{1,7};
		record = postService.searchPostRecord(category,status,text,0);
		assertEquals(5,record.size());
		assertEquals("尿酸値高め",record.get(0).getStatus());
		assertEquals("2022-03-03 18:41:36",record.get(0).getCreateAt());
		assertEquals("ダイエット中",record.get(1).getStatus());
		assertEquals("2022-03-02 11:12:50",record.get(1).getCreateAt());
		assertEquals("2022-03-01 18:29:51",record.get(3).getCreateAt());
		assertEquals("2022-02-28 23:30:34",record.get(4).getCreateAt());
		
		status = new int[] {};
		text = "改善";
		record = postService.searchPostRecord(category,status,text,0);
		assertEquals(1,record.size());
		assertTrue(record.get(0).getContent().contains("改善"));
		assertEquals("2022-03-02 12:55:08",record.get(0).getCreateAt());
	}
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	void searchPostRecordで二つの条件で絞り込んで取得する() throws Exception{
		category = new int[]{3,7};
		status = new int[]{1,6};
		
		List<PostRecord> record = postService.searchPostRecord(category,status,text,0);
		assertEquals(1,record.size());
		assertEquals("ダイエット中",record.get(0).getStatus());
		assertEquals("塩分",record.get(0).getPostCategory());
		assertEquals("2022-03-01 18:29:51",record.get(0).getCreateAt());
		
		category = new int[]{};
		text = "検査";
		record = postService.searchPostRecord(category,status,text,0);
		assertEquals(1,record.size());
		assertEquals("ダイエット中",record.get(0).getStatus());
		assertTrue(record.get(0).getContent().contains("検査"));
		assertEquals("2022-03-01 18:29:51",record.get(0).getCreateAt());
		
		category = new int[]{3,7};
		status = new int[]{};
		record = postService.searchPostRecord(category,status,text,0);
		assertEquals(2,record.size());
		assertEquals("尿酸値",record.get(0).getPostCategory());
		assertTrue(record.get(0).getContent().contains("検査"));
		assertEquals("2022-03-03 18:41:36",record.get(0).getCreateAt());
		assertEquals("塩分",record.get(1).getPostCategory());
		assertTrue(record.get(1).getContent().contains("検査"));
		assertEquals("2022-03-01 18:29:51",record.get(1).getCreateAt());
	}
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	void searchPostRecordで投稿情報を絞り込んで取得する() throws Exception{
		category = new int[]{1,2,3};
		status = new int[]{1,7};
		text = "ダイエット　効果";
		List<PostRecord> record = postService.searchPostRecord(category, status, text,0);
		assertEquals(1,record.size());
		assertEquals("ダイエット",record.get(0).getPostCategory());
		assertEquals("尿酸値高め",record.get(0).getStatus());
		assertEquals("加藤健",record.get(0).getNickName());
		assertEquals("サイゼリヤのサラダがダイエットに効果あるらしい",record.get(0).getContent());
		assertEquals("2022-02-28 23:30:34",record.get(0).getCreateAt());
	}
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	void findNickNameでニックネームを1件取得する() throws Exception{
		String nickName = postService.findNickName("糸井");
		assertEquals("sigeno",nickName);
	}
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	void findNickNameでニックネームを取得できない場合はnullが返ってくる() throws Exception{
		String nickName = postService.findNickName("kenken");
		assertEquals(null,nickName);
	}
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	void findProfileでユーザ情報レコードを1件取得する() throws Exception{
		AccountInfo info = postService.findProfile("匿名");
		assertEquals("匿名",info.getNickName());
		assertEquals("5000兆円欲しい！！！",info.getProfile());
		assertEquals(1,info.getStatus());
		assertEquals(2,info.getGender());
		assertEquals(27,info.getAge());
	}
	
	@Test
	void findProfileでユーザ情報レコードを取得できない場合はnullが返ってくる() throws Exception{
		AccountInfo result = postService.findProfile("miho");
		assertEquals(null,result);
	}
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	@ExpectedDatabase(value = "/service/Post/insert/",table="post")
	void insertPostで投稿が1件追加される() throws Exception{
		PostForm form = new PostForm();
		form.setUserName("miho");
		form.setNickName("匿名");
		form.setContent("糖質制限ってどこまでやればいいの～？");
		form.setPostCategory(2);
		postService.insertPost(form);
	}

}
