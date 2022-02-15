package com.dining.boyaki.model.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

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
	}
	
	@Test
	void findUserNameでユーザ名が見つからない場合はNullが返ってくる() throws Exception{
		when(findDataMapper.findUserName("hogehoge")).thenReturn(null);
		String userName = findDataSharedService.findUserName("hogehoge");
		assertEquals(null,userName);
	}
	
	@Test
	void findUserNameFromMailでユーザ名を取得する() throws Exception{
		when(findDataMapper.findUserNameFromMail("miho@gmail.com")).thenReturn("miho");
		String userName = findDataSharedService.findUserNameFromMail("miho@gmail.com");
		assertEquals("miho",userName);
	}
	
	@Test
	void findUserNameFromMailでユーザ名が見つからない場合はNullが返ってくる() throws Exception{
		when(findDataMapper.findUserNameFromMail("hogehoge@gmail.com")).thenReturn(null);
		String userName = findDataSharedService.findUserNameFromMail("hogehoge@gmail.com");
		assertEquals(null,userName);
	}
	
	@Test
	void findMailでメールアドレスを取得する() throws Exception{
		when(findDataMapper.findMail("example@ezweb.ne.jp")).thenReturn("example@ezweb.ne.jp");
		String mail = findDataSharedService.findMail("example@ezweb.ne.jp");
		assertEquals("example@ezweb.ne.jp",mail);
	}
	
	@Test
	void findMailでメールアドレスが見つからない場合はNullが返ってくる() throws Exception{
		when(findDataMapper.findMail("hogehoge@gmail.com")).thenReturn(null);
		String mail = findDataSharedService.findMail("hogehoge@gmail.com");
		assertEquals(null,mail);
	}

}
