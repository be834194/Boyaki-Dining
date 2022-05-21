package com.dining.boyaki.model.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.model.mapper.PasswordHistoryMapper;

@RunWith(SpringRunner.class)
public class PasswordHistoryServiceTest {
	
	@Mock
	PasswordHistoryMapper passwordHistoryMapper;
	
	@InjectMocks
	PasswordHistoryService passwordHistoryService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void findPasswordでパスワードを取得する()  throws Exception{
		when(passwordHistoryMapper.findPassword("miho", "miho@gmail.com")).thenReturn("ocean_Nu");
		String password = passwordHistoryService.findPassword("miho", "miho@gmail.com");
		assertEquals("ocean_Nu",password);
		verify(passwordHistoryMapper,times(1)).findPassword("miho", "miho@gmail.com");
		
		when(passwordHistoryMapper.findPassword("miho", "ocean@gmail.com")).thenReturn(null);
		password = passwordHistoryService.findPassword("miho", "ocean@gmail.com");
		assertEquals(null,password);
		verify(passwordHistoryMapper,times(1)).findPassword("miho", "ocean@gmail.com");
	}
	
	@Test
	void findUseFromでユーザ一人のPW更新履歴を降順で取得する() throws Exception{
		List<PasswordHistory> histories = new ArrayList<PasswordHistory>();
		PasswordHistory history = new PasswordHistory("加藤健","ten_bear",LocalDateTime.of(2021, 12, 15, 01, 22,39));
		histories.add(history);
		history = new PasswordHistory("加藤健","pinballs",LocalDateTime.of(2022, 01, 13, 01, 22,39));
		histories.add(history);
		when(passwordHistoryMapper.findUseFrom("加藤健", LocalDateTime.of(2021, 12, 13, 01, 22,39))).thenReturn(histories);
		
		List<PasswordHistory> result = passwordHistoryService.findUseFrom("加藤健", LocalDateTime.of(2021, 12, 13, 01, 22,39));
		assertEquals(2,result.size());
		assertEquals("加藤健",result.get(0).getUserName());
		assertEquals("ten_bear",result.get(0).getPassword());
		assertEquals(LocalDateTime.of(2021, 12, 15, 01, 22,39),result.get(0).getUseDay());
		assertEquals("pinballs",result.get(1).getPassword());
		assertEquals(LocalDateTime.of(2022, 01, 13, 01, 22,39),result.get(1).getUseDay());
		verify(passwordHistoryMapper,times(1)).findUseFrom("加藤健", LocalDateTime.of(2021, 12, 13, 01, 22,39));
		
		histories = new ArrayList<PasswordHistory>();
        when(passwordHistoryMapper.findUseFrom("加藤健", LocalDateTime.of(2021, 11, 30, 01, 22,39))).thenReturn(histories);
		result = passwordHistoryService.findUseFrom("加藤健", LocalDateTime.of(2021, 11, 30, 01, 22,39));
		assertTrue(result.isEmpty());
		verify(passwordHistoryMapper,times(1)).findUseFrom("加藤健", LocalDateTime.of(2021, 11, 30, 01, 22,39));
	}
	
}
