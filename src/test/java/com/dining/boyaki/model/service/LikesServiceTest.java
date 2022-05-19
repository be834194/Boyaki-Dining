package com.dining.boyaki.model.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import com.dining.boyaki.model.mapper.LikesMapper;

@RunWith(SpringRunner.class)
public class LikesServiceTest {
	
	@Mock
	LikesMapper likesMapper;
	
	@InjectMocks
	LikesService likesService;
	
	@BeforeEach
    void setUp() {
    	MockitoAnnotations.openMocks(this);
    }
	
	@Test
	void sumRateで投稿を件の総いいね数を取得する() throws Exception{
		when(likesMapper.sumRate(2)).thenReturn(Optional.of(0));
		when(likesMapper.sumRate(3)).thenReturn(Optional.of(5));
		
		int rate = likesService.sumRate(2);
		assertEquals(0,rate);
		verify(likesMapper,times(1)).sumRate(2);
		
		rate = likesService.sumRate(3);
		assertEquals(5,rate);
		verify(likesMapper,times(1)).sumRate(3);
	}
	
	@Test
	void updateRateでlikeテーブルにデータが追加もしくは更新される() throws Exception{
		when(likesMapper.currentRate(1, "加藤健")).thenReturn(Optional.of(-1));
		when(likesMapper.currentRate(2, "加藤健")).thenReturn(Optional.of(0));
		when(likesMapper.currentRate(3, "加藤健")).thenReturn(Optional.of(1));
		doNothing().when(likesMapper).insertRate(1, "加藤健", 1);
		doNothing().when(likesMapper).updateRate(2, "加藤健", 1);
		doNothing().when(likesMapper).updateRate(3, "加藤健", 0);
		
		likesService.updateRate(1, "加藤健");
		verify(likesMapper,times(1)).currentRate(1, "加藤健");
		verify(likesMapper,times(1)).insertRate(1, "加藤健", 1);
		
		likesService.updateRate(2, "加藤健");
		verify(likesMapper,times(1)).currentRate(2, "加藤健");
		verify(likesMapper,times(1)).updateRate(2, "加藤健", 1);
		
		likesService.updateRate(3, "加藤健");
		verify(likesMapper,times(1)).currentRate(3, "加藤健");
		verify(likesMapper,times(1)).updateRate(3, "加藤健", 0);
	}

}
