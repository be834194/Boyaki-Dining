package com.dining.boyaki.model.service;

import java.sql.Date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.dining.boyaki.model.entity.DiaryRecord;
import com.dining.boyaki.model.mapper.FindDataMapper;

@RunWith(SpringRunner.class)
public class FindDataSharedServiceTest {
	
	@Mock
	FindDataMapper findDataMapper;
	
	@InjectMocks
	FindDataSharedService findDataSharedService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void findUserNameでユーザ名を取得する() throws Exception{
		when(findDataMapper.findUserName("糸井")).thenReturn("糸井");
		String userName = findDataSharedService.findUserName("糸井");
		assertEquals("糸井",userName);
		verify(findDataMapper,times(1)).findUserName("糸井");
	}
	
	@Test
	void findUserNameでユーザ名が見つからない場合はNullが返ってくる() throws Exception{
		when(findDataMapper.findUserName("hogehoge")).thenReturn(null);
		String userName = findDataSharedService.findUserName("hogehoge");
		assertEquals(null,userName);
		verify(findDataMapper,times(1)).findUserName("hogehoge");
	}
	
	@Test
	void findUserNameFromMailでユーザ名を取得する() throws Exception{
		when(findDataMapper.findUserNameFromMail("miho@gmail.com")).thenReturn("miho");
		String userName = findDataSharedService.findUserNameFromMail("miho@gmail.com");
		assertEquals("miho",userName);
		verify(findDataMapper,times(1)).findUserNameFromMail("miho@gmail.com");
	}
	
	@Test
	void findUserNameFromMailでユーザ名が見つからない場合はNullが返ってくる() throws Exception{
		when(findDataMapper.findUserNameFromMail("hogehoge@gmail.com")).thenReturn(null);
		String userName = findDataSharedService.findUserNameFromMail("hogehoge@gmail.com");
		assertEquals(null,userName);
		verify(findDataMapper,times(1)).findUserNameFromMail("hogehoge@gmail.com");
	}
	
	@Test
	void findMailでメールアドレスを取得する() throws Exception{
		when(findDataMapper.findMail("example@ezweb.ne.jp")).thenReturn("example@ezweb.ne.jp");
		String mail = findDataSharedService.findMail("example@ezweb.ne.jp");
		assertEquals("example@ezweb.ne.jp",mail);
		verify(findDataMapper,times(1)).findMail("example@ezweb.ne.jp");
	}
	
	@Test
	void findMailでメールアドレスが見つからない場合はNullが返ってくる() throws Exception{
		when(findDataMapper.findMail("hogehoge@gmail.com")).thenReturn(null);
		String mail = findDataSharedService.findMail("hogehoge@gmail.com");
		assertEquals(null,mail);
		verify(findDataMapper,times(1)).findMail("hogehoge@gmail.com");
	}
	
	@Test
	void findOneDiaryRecordで食事投稿を取得する() throws Exception{
		DiaryRecord diary = new DiaryRecord();
		diary.setUserName("miho");
		diary.setCategoryId(4);
		diary.setDiaryDay(Date.valueOf("2022-02-15"));
		diary.setRecord1("");
		diary.setRecord2("ポテトチップス");
		diary.setRecord3("腕立て伏せ15回×3セット");
		diary.setPrice(0);
		diary.setMemo(null);
		when(findDataMapper.findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-15"))).thenReturn(diary);
		
		DiaryRecord result = findDataSharedService.findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-15"));
		assertEquals("miho",result.getUserName());
		assertEquals(4,result.getCategoryId());
		assertEquals(Date.valueOf("2022-02-15"),result.getDiaryDay());
		assertEquals("",result.getRecord1());
		assertEquals("ポテトチップス",result.getRecord2());
		assertEquals("腕立て伏せ15回×3セット",result.getRecord3());
		assertEquals(0,result.getPrice());
		assertNull(result.getMemo());
		verify(findDataMapper,times(1)).findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-15"));
	}
	
	@Test
	void findOneDiaryRecordで食事投稿が見つからない場合はNullが返ってくる() throws Exception{
		when(findDataMapper.findOneDiaryRecord("糸井", 2, Date.valueOf("2022-02-09"))).thenReturn(null);
		DiaryRecord result = findDataSharedService.findOneDiaryRecord("糸井", 2, Date.valueOf("2022-02-09"));
		assertNull(result);
		verify(findDataMapper,times(1)).findOneDiaryRecord("糸井", 2, Date.valueOf("2022-02-09"));
	}

}
