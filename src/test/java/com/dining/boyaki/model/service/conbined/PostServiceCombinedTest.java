package com.dining.boyaki.model.service.conbined;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

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
import com.dining.boyaki.model.entity.CommentRecord;
import com.dining.boyaki.model.entity.PostRecord;
import com.dining.boyaki.model.form.CommentForm;
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
	
	private static MockedStatic<LocalDateTime> mock;
	
	@Autowired
	PostService postService;
	
	int[] category;
	int[] status;
	String text;
	
	@BeforeEach
    void setUp() {
    	mock = Mockito.mockStatic(LocalDateTime.class,Mockito.CALLS_REAL_METHODS);
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
	void findNickNameでニックネームを1件取得する() throws Exception{
		String nickName = postService.findNickName("糸井");
		assertEquals("sigeno",nickName);
		
		nickName = postService.findNickName("kenken");
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
		assertEquals(2,info.getAge());
		
		info = postService.findProfile("miho");
		assertEquals(null,info);
	}
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	@ExpectedDatabase(value = "/service/Post/insert/post/",table="post"
			         ,assertionMode=DatabaseAssertionMode.NON_STRICT)
	void insertPostで投稿が1件追加される() throws Exception{
		PostForm form = new PostForm("miho","匿名","糖質制限ってどこまでやればいいの～？",2);
		
		LocalDateTime datetime = LocalDateTime.parse("2022-03-08T09:31:12");
    	mock.when(LocalDateTime::now).thenReturn(datetime);
		postService.insertPost(form);
	}
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	@ExpectedDatabase(value = "/service/Post/delete/")
	void deletePostで投稿が1件削除される() throws Exception{
		postService.deletePost("糸井", 3);
	}
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	@ExpectedDatabase(value = "/service/Post/insert/comment/",table="comment"
	         ,assertionMode=DatabaseAssertionMode.NON_STRICT)
	void insertCommentで投稿へのコメントが一件追加される() throws Exception{
		CommentForm form = new CommentForm(9,"miho","匿名","牛乳私も試してみます！");
		LocalDateTime datetime = LocalDateTime.parse("2022-03-10T16:27:38");
    	mock.when(LocalDateTime::now).thenReturn(datetime);
		postService.insertComment(form);
	}
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	void findCommentRecordで投稿一つに対するコメントを全件取得する() throws Exception{
		//0ページ目
		List<CommentRecord> record = postService.findCommentList(7, 0);
		assertEquals(5,record.size());
		assertEquals("sigeno",record.get(0).getNickName());
		assertEquals("中性脂肪・コレステロール高め",record.get(0).getStatus());
		assertEquals("test",record.get(0).getContent());;
		assertEquals("2022-03-10 19:44:28",record.get(0).getCreateAt());
		assertEquals("2022-03-07 09:52:28",record.get(4).getCreateAt());
		
		//1ページ目
		record = postService.findCommentList(7, 1);
		assertEquals(1,record.size());
		assertEquals("2022-03-07 09:44:28",record.get(0).getCreateAt());
		
		//2ページ目
		record = postService.findCommentList(7, 2);
		assertEquals(0,record.size());
	}
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	void findOnePostRecordで投稿を一件取得する() {
		PostRecord result = postService.findOnePostRecord(10);
		assertEquals("10",result.getPostId());
		assertEquals("糸井",result.getUserName());
		assertEquals("sigeno",result.getNickName());
		assertEquals("ノンアル飽きた！",result.getContent());
		assertEquals("中性脂肪・コレステロール高め",result.getStatus());
		assertEquals("ダイエット",result.getPostCategory());
		assertEquals("2022-03-03 19:32:44",result.getCreateAt());
		
		result = postService.findOnePostRecord(333);
		assertEquals(null,result);
	}
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	void findPostRecordでユーザ一人の投稿情報を全件取得する() throws Exception{
		//0ページ目
		List<PostRecord> record = postService.findPostRecord("sigeno", 0);
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
		
		//1ページ目
		record = postService.findPostRecord("sigeno", 1);
		assertEquals(1,record.size());
		assertEquals("2022-03-01 12:06:21",record.get(0).getCreateAt());
		
		//2ページ目
		record = postService.findPostRecord("sigeno", 2);
		assertTrue(record.isEmpty());
	}
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	void searchPostRecordで全件取得する() throws Exception{
		//0ページ目
        List<PostRecord> record = postService.searchPostRecord(category,status,text,0);
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
		
		//2ページ目
		record = postService.searchPostRecord(category,status,text,2);
		assertEquals(1,record.size());
		assertEquals("2022-02-28 23:30:34",record.get(0).getCreateAt());
		
		//3ページ目
		record = postService.searchPostRecord(category,status,text,3);
		assertTrue(record.isEmpty());
	}
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	void searchPostRecordで一つの条件で絞り込んで取得する() throws Exception{
		//0ページ目、カテゴリ(運動・筋トレ,塩分)
		category = new int[]{5,3};
		List<PostRecord> record = postService.searchPostRecord(category,status,text,0);
		assertEquals(2,record.size());
		assertEquals("運動・筋トレ",record.get(0).getPostCategory());
		assertEquals("2022-03-02 00:02:49",record.get(0).getCreateAt());
		assertEquals("塩分",record.get(1).getPostCategory());
		assertEquals("2022-03-01 18:29:51",record.get(1).getCreateAt());
		
		//0ページ目、ステータス(ダイエット中,尿酸値高め)
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
		
		//0ページ目、投稿
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
		//0ページ目、カテゴリ(塩分,尿酸値)+ステータス(ダイエット中,コレステロール高め)
		List<PostRecord> record = postService.searchPostRecord(category,status,text,0);
		assertEquals(1,record.size());
		assertEquals("ダイエット中",record.get(0).getStatus());
		assertEquals("塩分",record.get(0).getPostCategory());
		assertEquals("2022-03-01 18:29:51",record.get(0).getCreateAt());
		
		category = new int[]{};
		text = "検査";
		//0ページ目、ステータス(ダイエット中,コレステロール高め)+投稿
		record = postService.searchPostRecord(category,status,text,0);
		assertEquals(1,record.size());
		assertEquals("ダイエット中",record.get(0).getStatus());
		assertTrue(record.get(0).getContent().contains("検査"));
		assertEquals("2022-03-01 18:29:51",record.get(0).getCreateAt());
		
		category = new int[]{3,7};
		status = new int[]{};
		//0ページ目、カテゴリ(塩分,尿酸値)+投稿
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
		//0ページ目、カテゴリ(ダイエット,糖質,塩分)+ステータス(ダイエット中,尿酸値高め)+投稿内容
		List<PostRecord> record = postService.searchPostRecord(category, status, text,0);
		assertEquals(1,record.size());
		assertEquals("ダイエット",record.get(0).getPostCategory());
		assertEquals("尿酸値高め",record.get(0).getStatus());
		assertEquals("加藤健",record.get(0).getNickName());
		assertEquals("サイゼリヤのサラダがダイエットに効果あるらしい",record.get(0).getContent());
		assertEquals("2022-02-28 23:30:34",record.get(0).getCreateAt());
	}

}
