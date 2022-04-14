package com.dining.boyaki.model.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.data.domain.PageRequest;

import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.Comment;
import com.dining.boyaki.model.entity.CommentRecord;
import com.dining.boyaki.model.entity.Post;
import com.dining.boyaki.model.entity.PostRecord;
import com.dining.boyaki.model.form.CommentForm;
import com.dining.boyaki.model.form.PostForm;
import com.dining.boyaki.model.mapper.PostMapper;

@RunWith(SpringRunner.class)
public class PostServiceTest {
	
	private static LocalDateTime datetime = LocalDateTime.parse("2022-03-03T09:31:12");
	
	private static MockedStatic<LocalDateTime> mock;
	
	@Mock
	PostMapper postMapper;
	
	@InjectMocks
	PostService postService;
	
	@BeforeEach
    void setUp() {
    	MockitoAnnotations.openMocks(this);
    	mock = Mockito.mockStatic(LocalDateTime.class,Mockito.CALLS_REAL_METHODS);
    	mock.when(LocalDateTime::now).thenReturn(datetime);
    }
	
	@AfterEach
	void tearDown() throws Exception{
		mock.close();
	}
	
	@Test
	void findNickNameでニックネームを1件取得する() throws Exception{
		when(postMapper.findNickName("糸井")).thenReturn("sigeno");
		
		String nickName = postService.findNickName("糸井");
		assertEquals("sigeno",nickName);
		verify(postMapper,times(1)).findNickName("糸井");
	}
	
	@Test
	void findNickNameでニックネームを取得できない場合はnullが返ってくる() throws Exception{
		when(postMapper.findNickName("kenken")).thenReturn(null);
		
		String nickName = postService.findNickName("kenken");
		assertEquals(null,nickName);
		verify(postMapper,times(1)).findNickName("kenken");
	}
	
	@Test
	void findProfileでユーザ情報レコードを1件取得する() throws Exception{
		AccountInfo info = new AccountInfo();
		info.setNickName("匿名");
		info.setProfile("5000兆円欲しい！！！");
		info.setStatus(1);
		info.setGender(2);
		info.setAge(2);
		when(postMapper.findProfile("匿名")).thenReturn(info);
		
		AccountInfo result = postService.findProfile("匿名");
		assertEquals("匿名",result.getNickName());
		assertEquals("5000兆円欲しい！！！",result.getProfile());
		assertEquals(1,result.getStatus());
		assertEquals(2,result.getGender());
		assertEquals(2,result.getAge());
		verify(postMapper,times(1)).findProfile("匿名");
	}
	
	@Test
	void findProfileでユーザ情報レコードを取得できない場合はnullが返ってくる() throws Exception{
		when(postMapper.findProfile("miho")).thenReturn(null);
		
		AccountInfo result = postService.findProfile("miho");
		assertEquals(null,result);
		verify(postMapper,times(1)).findProfile("miho");
	}
	
	@Test
	void insertPostで投稿が1件追加される() throws Exception{
		PostForm form = new PostForm();
		form.setUserName("miho");
		form.setNickName("匿名");
		form.setContent("糖質制限ってどこまでやればいいの～？");
		form.setPostCategory(2);
		doNothing().when(postMapper).insertPost(any(Post.class));
		
		postService.insertPost(form);
		verify(postMapper,times(1)).insertPost(any(Post.class));
	}
	
	@Test
	void deletePostで投稿が1件追加される() throws Exception{
		doNothing().when(postMapper).deletePost("糸井", 3);
		
		postService.deletePost("糸井", 3);
		verify(postMapper,times(1)).deletePost("糸井", 3);
	}
	
	@Test
	void findOnePostRecordで投稿を一件取得する() throws Exception{
		PostRecord record = new PostRecord();
		record.setPostId("10");
		record.setUserName("糸井");
		record.setNickName("sigeno");
		record.setContent("ノンアル飽きた！");
		record.setStatus("中性脂肪・コレステロール高め");
		record.setPostCategory("ダイエット");
		record.setCreateAt("2022-03-03 19:32:44");
		when(postMapper.findOnePostRecord(10)).thenReturn(record);
		
		PostRecord result = postService.findOnePostRecord(10);
		assertEquals("10",result.getPostId());
		assertEquals("糸井",result.getUserName());
		assertEquals("sigeno",result.getNickName());
		assertEquals("ノンアル飽きた！",result.getContent());
		assertEquals("中性脂肪・コレステロール高め",result.getStatus());
		assertEquals("ダイエット",result.getPostCategory());
		assertEquals("2022-03-03 19:32:44",result.getCreateAt());
		verify(postMapper,times(1)).findOnePostRecord(10);
	}
	
	@Test
	void findOnePostRecordで投稿を一件取得できない場合はnullが返ってくる() throws Exception{
		when(postMapper.findOnePostRecord(10)).thenReturn(null);
		
		PostRecord result = postService.findOnePostRecord(10);
		assertEquals(null,result);
		verify(postMapper,times(1)).findOnePostRecord(10);
	}
	
	@Test
	void insertCommentで投稿へのコメントが一件追加される() throws Exception{
		CommentForm form = new CommentForm();
		form.setPostId(9);
		form.setUserName("miho");
		form.setNickName("匿名");
		form.setContent("牛乳私も試してみます！");
		doNothing().when(postMapper).insertComment(any(Comment.class));
		
		postService.insertComment(form);
		verify(postMapper,times(1)).insertComment(any(Comment.class));
	}
	
	@Test
	void findCommentRecordで投稿一つに対するコメントを全件取得する() throws Exception{
		CommentRecord record1 = new CommentRecord("sigeno","ダイエット中","応援してます","2022-04-14 20:32:47");
		CommentRecord record2 = new CommentRecord("kenken","尿酸値高め","応援してます","2022-04-13 18:53:12");
		List<CommentRecord> records = new ArrayList<CommentRecord>();
		records.add(record1);
		records.add(record2);
		when(postMapper.findCommentRecord(3, PageRequest.of(0, 5))).thenReturn(records);
		
		List<CommentRecord> result = postService.findCommentList(3, 0);
		assertEquals(2,result.size());
		verify(postMapper,times(1)).findCommentRecord(3, PageRequest.of(0, 5));
	}
	
	@Test
	void sumRateで投稿を件の総いいね数を取得する() throws Exception{
		when(postMapper.sumRate(2)).thenReturn(Optional.of(0));
		when(postMapper.sumRate(3)).thenReturn(Optional.of(5));
		
		int rate = postService.sumRate(2);
		assertEquals(0,rate);
		verify(postMapper,times(1)).sumRate(2);
		
		rate = postService.sumRate(3);
		assertEquals(5,rate);
		verify(postMapper,times(1)).sumRate(3);
	}
	
	@Test
	void updateRateでlikeテーブルにデータが追加もしくは更新される() throws Exception{
		when(postMapper.currentRate(1, "加藤健")).thenReturn(Optional.of(-1));
		when(postMapper.currentRate(2, "加藤健")).thenReturn(Optional.of(0));
		when(postMapper.currentRate(3, "加藤健")).thenReturn(Optional.of(1));
		doNothing().when(postMapper).insertRate(1, "加藤健", 1);
		doNothing().when(postMapper).updateRate(2, "加藤健", 1);
		doNothing().when(postMapper).updateRate(3, "加藤健", 0);
		
		postService.updateRate(1, "加藤健");
		verify(postMapper,times(1)).currentRate(1, "加藤健");
		verify(postMapper,times(1)).insertRate(1, "加藤健", 1);
		
		postService.updateRate(2, "加藤健");
		verify(postMapper,times(1)).currentRate(2, "加藤健");
		verify(postMapper,times(1)).updateRate(2, "加藤健", 1);
		
		postService.updateRate(3, "加藤健");
		verify(postMapper,times(1)).currentRate(3, "加藤健");
		verify(postMapper,times(1)).updateRate(3, "加藤健", 0);
	}
	
	@Test
	void findPostRecordでユーザ一人の投稿情報を全件取得する() throws Exception{
		List<PostRecord> recordList = new ArrayList<PostRecord>();
		PostRecord record = new PostRecord();
		record.setNickName("加藤健");
		record.setContent("牛乳を飲み始めて尿酸値が7.0を切りました！");
		record.setPostCategory("尿酸値");
		record.setStatus("尿酸値高め");
		record.setCreateAt("2022-03-07 12:46:36");
		recordList.add(record);
		
		when(postMapper.findPostRecord("加藤健", PageRequest.of(0, 5))).thenReturn(recordList);
		List<PostRecord> result = postService.findPostRecord("加藤健", 0);
		assertEquals(1,result.size());
		verify(postMapper,times(1)).findPostRecord("加藤健", PageRequest.of(0, 5));
	}
	
	@Test
	void searchPostRecordで全件取得する() throws Exception{
		List<PostRecord> recordList = new ArrayList<PostRecord>();
		PostRecord record = new PostRecord();
		recordList.add(record);
		recordList.add(record);
		
		int[] category = new int[]{};
		int[] status = new int[]{};
		String text = " 		";
		when(postMapper.searchPostRecord(any(),any(),any(),any())).thenReturn(recordList);
		
		List<PostRecord> result = postService.searchPostRecord(category, status, text,0);
		assertEquals(2,result.size());
		verify(postMapper,times(1)).searchPostRecord(any(),any(),any(),any());
		
		text = null;
		result = postService.searchPostRecord(category, status, text,0);
		assertEquals(2,result.size());
		verify(postMapper,times(2)).searchPostRecord(any(),any(),any(),any());
		
		text = "";
		result = postService.searchPostRecord(category, status, text,0);
		assertEquals(2,result.size());
		verify(postMapper,times(3)).searchPostRecord(any(),any(),any(),any());
	}
	
	@Test
	void searchPostRecordで投稿情報を絞り込んで取得する() throws Exception{
		List<PostRecord> recordList = new ArrayList<PostRecord>();
		PostRecord record = new PostRecord();
		record.setNickName("加藤健");
		record.setContent("牛乳を飲み始めて尿酸値が7.0を切りました！");
		record.setPostCategory("尿酸値");
		record.setStatus("尿酸値高め");
		record.setCreateAt("2022-03-07 12:46:36");
		recordList.add(record);
		
		int[] category = new int[]{1,2,3};
		int[] status = new int[]{1,7};
		String text = "牛乳 尿酸";
		when(postMapper.searchPostRecord(
				any(int[].class),any(int[].class),any(String[].class),any(PageRequest.class))).thenReturn(recordList);
		
		List<PostRecord> result = postService.searchPostRecord(category, status, text,0);
		assertEquals(1,result.size());
		verify(postMapper,times(1)).searchPostRecord(any(int[].class),any(int[].class),any(String[].class),any(PageRequest.class));
	}

}
