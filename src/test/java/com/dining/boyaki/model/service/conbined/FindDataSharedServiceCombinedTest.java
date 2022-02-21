package com.dining.boyaki.model.service.conbined;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Date;
import java.time.LocalDateTime;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.DiaryRecord;
import com.dining.boyaki.model.service.FindDataSharedService;
import com.dining.boyaki.util.CsvDataSetLoader;

@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
	                     TransactionalTestExecutionListener.class,
	                     DbUnitTestExecutionListener.class})
@SpringBootTest
@Transactional
public class FindDataSharedServiceCombinedTest {
	
	@Autowired
	FindDataSharedService findDataSharedService;
	
	@Test
	@DatabaseSetup(value = "/service/FindData/setup/")
	void findUserNameでユーザ名を取得する() throws Exception{
		String userName = findDataSharedService.findUserName("糸井");
		assertEquals("糸井",userName);
	}
	
	@Test
	@DatabaseSetup(value = "/service/FindData/setup/")
	void findUserNameでユーザ名が見つからない場合はNullが返ってくる() throws Exception{
		String userName = findDataSharedService.findUserName("hogehoge");
		assertEquals(null,userName);
	}
	
	@Test
	@DatabaseSetup(value = "/service/FindData/setup/")
	void findUserNameFromMailでユーザ名を取得する() throws Exception{
		String userName = findDataSharedService.findUserNameFromMail("miho@gmail.com");
		assertEquals("miho",userName);
	}
	
	@Test
	void findUserNameFromMailでユーザ名が見つからない場合はNullが返ってくる() throws Exception{
		String userName = findDataSharedService.findUserNameFromMail("hogehoge@gmail.com");
		assertEquals(null,userName);
	}
	
	@Test
	@DatabaseSetup(value = "/service/FindData/setup/")
	void findMailでメールアドレスを取得する() throws Exception{
		String mail = findDataSharedService.findMail("example@ezweb.ne.jp");
		assertEquals("example@ezweb.ne.jp",mail);
	}
	
	@Test
	@DatabaseSetup(value = "/service/FindData/setup/")
	void findMailでメールアドレスが見つからない場合はNullが返ってくる() throws Exception{
		String mail = findDataSharedService.findMail("hogehoge@gmail.com");
		assertEquals(null,mail);
	}
	
	@Test
	@DatabaseSetup(value = "/service/FindData/setup/")
	void findOneDiaryRecordで食事投稿を取得する() throws Exception{
		DiaryRecord result = findDataSharedService.findOneDiaryRecord("miho", 4, Date.valueOf("2022-02-15"));
		assertEquals("miho",result.getUserName());
		assertEquals(4,result.getCategoryId());
		assertEquals(Date.valueOf("2022-02-15"),result.getDiaryDay());
		assertEquals(null,result.getRecord1());
		assertEquals("ポテトチップス",result.getRecord2());
		assertEquals("腕立て伏せ15回×3セット",result.getRecord3());
		assertEquals(0,result.getPrice());
		assertNull(result.getMemo());
		assertEquals(LocalDateTime.parse("2022-02-15T23:30:34"),result.getCreateAt());
		assertEquals(LocalDateTime.parse("2022-02-15T23:30:34"),result.getUpdateAt());
	}
	
	@Test
	@DatabaseSetup(value = "/service/FindData/setup/")
	void findOneDiaryRecordで食事投稿が見つからない場合はNullが返ってくる() throws Exception{
		DiaryRecord result = findDataSharedService.findOneDiaryRecord("糸井", 2, Date.valueOf("2022-02-09"));
		assertNull(result);
	}

}
