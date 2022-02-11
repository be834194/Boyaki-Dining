package com.dining.boyaki.model.service.conbined;

import static org.junit.Assert.assertEquals;
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
	

}
