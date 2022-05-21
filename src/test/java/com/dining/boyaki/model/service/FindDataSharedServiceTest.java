package com.dining.boyaki.model.service;

import java.sql.Date;
import java.time.LocalDateTime;

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

import com.dining.boyaki.model.entity.AccountInfo;
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
		
		when(findDataMapper.findUserName("hogehoge")).thenReturn(null);
		userName = findDataSharedService.findUserName("hogehoge");
		assertEquals(null,userName);
		verify(findDataMapper,times(1)).findUserName("hogehoge");
	}
	
	@Test
	void findUserNameFromMailでユーザ名を取得する() throws Exception{
		when(findDataMapper.findUserNameFromMail("miho@gmail.com")).thenReturn("miho");
		String userName = findDataSharedService.findUserNameFromMail("miho@gmail.com");
		assertEquals("miho",userName);
		verify(findDataMapper,times(1)).findUserNameFromMail("miho@gmail.com");
		
		when(findDataMapper.findUserNameFromMail("hogehoge@gmail.com")).thenReturn(null);
		userName = findDataSharedService.findUserNameFromMail("hogehoge@gmail.com");
		assertEquals(null,userName);
		verify(findDataMapper,times(1)).findUserNameFromMail("hogehoge@gmail.com");
	}
	
	@Test
	void findMailでメールアドレスを取得する() throws Exception{
		when(findDataMapper.findMail("example@ezweb.ne.jp")).thenReturn("example@ezweb.ne.jp");
		String mail = findDataSharedService.findMail("example@ezweb.ne.jp");
		assertEquals("example@ezweb.ne.jp",mail);
		verify(findDataMapper,times(1)).findMail("example@ezweb.ne.jp");
		
		when(findDataMapper.findMail("hogehoge@gmail.com")).thenReturn(null);
		mail = findDataSharedService.findMail("hogehoge@gmail.com");
		assertEquals(null,mail);
		verify(findDataMapper,times(1)).findMail("hogehoge@gmail.com");
	}
	
	@Test
	void findOneDiaryRecordで食事投稿を取得する() throws Exception{
		DiaryRecord diary = new DiaryRecord("miho",4,Date.valueOf("2022-02-15"),
				                            null,"ポテトチップス","腕立て伏せ15回×3セット",null,null,
				                            LocalDateTime.parse("2022-02-15T23:30:34"),
				                            LocalDateTime.parse("2022-02-15T23:30:34"));
		when(findDataMapper.findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-15"))).thenReturn(diary);
		
		DiaryRecord result = findDataSharedService.findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-15"));
		assertEquals("miho",result.getUserName());
		assertEquals(4,result.getCategoryId());
		assertEquals(Date.valueOf("2022-02-15"),result.getDiaryDay());
		assertNull(result.getRecord1());
		assertEquals("ポテトチップス",result.getRecord2());
		assertEquals("腕立て伏せ15回×3セット",result.getRecord3());
		assertEquals(null,result.getImageName());
		assertNull(result.getMemo());
		assertEquals(LocalDateTime.parse("2022-02-15T23:30:34"),result.getCreateAt());
		verify(findDataMapper,times(1)).findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-15"));
		
		when(findDataMapper.findOneDiaryRecord("糸井", 2, Date.valueOf("2022-02-09"))).thenReturn(null);
		result = findDataSharedService.findOneDiaryRecord("糸井", 2, Date.valueOf("2022-02-09"));
		assertNull(result);
		verify(findDataMapper,times(1)).findOneDiaryRecord("糸井", 2, Date.valueOf("2022-02-09"));
	}
	
	@Test
	void findNickNameでニックネームを取得する() throws Exception{
		AccountInfo info = new AccountInfo();
		info.setUserName("糸井");
		info.setNickName("sigeno");
		when(findDataMapper.findNickName("sigeno")).thenReturn(info);
		AccountInfo nickName = findDataSharedService.findNickName("sigeno");
		assertEquals("糸井",nickName.getUserName());
		assertEquals("sigeno",nickName.getNickName());
		verify(findDataMapper,times(1)).findNickName("sigeno");
		
		when(findDataMapper.findNickName("hogei")).thenReturn(null);
		nickName = findDataSharedService.findNickName("hogei");
		assertNull(nickName);
		verify(findDataMapper,times(1)).findNickName("hogei");
	}

}
