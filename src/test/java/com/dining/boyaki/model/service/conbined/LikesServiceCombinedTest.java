package com.dining.boyaki.model.service.conbined;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.service.LikesService;
import com.dining.boyaki.util.CsvDataSetLoader;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class LikesServiceCombinedTest {
	
	@Autowired
	LikesService likesService;
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	void sumRateで投稿を件の総いいね数を取得する() throws Exception{
		int rate = likesService.sumRate(2);
		assertEquals(2,rate);
		rate = likesService.sumRate(7);
		assertEquals(0,rate);
	}
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	void currentRateで投稿に対する現在の評価状態を取得() throws Exception{
		int rate = likesService.currentRate(1, "加藤健");
		assertEquals(-1,rate);
		
		rate = likesService.currentRate(2, "加藤健");
		assertEquals(1,rate);
		
		rate = likesService.currentRate(3, "加藤健");
		assertEquals(0,rate);
	}
	
	@Test
	@DatabaseSetup(value = "/service/Post/setup/")
	@ExpectedDatabase(value = "/service/Post/likes/",table="likes")
	void updateRateでlikeテーブルにデータが追加もしくは更新される() throws Exception{
		likesService.updateRate(1, "加藤健");
		likesService.updateRate(2, "加藤健");
		likesService.updateRate(3, "加藤健");
	}

}
