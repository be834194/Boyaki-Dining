package com.dining.boyaki.model.service.conbined;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.time.LocalDateTime;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.model.service.PasswordHistoryService;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class PasswordHistoryServiceCombinedTest {
	
	@Autowired
	PasswordHistoryService passwordHistoryService;
	
	@Test
	@DatabaseSetup(value = "/service/PasswordHistory/setup/")
	void findPasswordでパスワードを取得する()  throws Exception{
		String password = passwordHistoryService.findPassword("miho", "miho@gmail.com");
		assertEquals("ocean-Nu",password);
		
		password = passwordHistoryService.findPassword("糸井", "miho@gmail.com");
		assertEquals(null,password);
	}
	
	@Test
	@DatabaseSetup(value = "/service/PasswordHistory/setup/")
	void findUseFromでユーザ一人のPW更新履歴を降順で取得する() throws Exception{
		List<PasswordHistory> result = passwordHistoryService.findUseFrom("加藤健", LocalDateTime.of(2021, 12, 13, 01, 22,39));
		assertEquals(2,result.size());
		assertEquals("加藤健",result.get(0).getUserName());
		assertEquals("pinballs",result.get(0).getPassword());
		assertEquals(LocalDateTime.of(2022, 01, 13, 9, 8,56),result.get(0).getUseDay());
		assertEquals("ten_bear",result.get(1).getPassword());
		assertEquals(LocalDateTime.of(2021, 12, 14, 12, 8, 28),result.get(1).getUseDay());
		
		result = passwordHistoryService.findUseFrom("加藤健", LocalDateTime.of(2022, 01, 13, 10, 22, 39));
		assertTrue(result.isEmpty());
	}

}
