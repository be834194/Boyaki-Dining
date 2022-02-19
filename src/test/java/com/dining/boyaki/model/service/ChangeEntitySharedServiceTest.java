package com.dining.boyaki.model.service;

import static org.junit.Assert.assertEquals;

import java.sql.Date;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.DiaryRecord;
import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.model.form.DiaryRecordForm;
import com.dining.boyaki.model.form.RegisterForm;

@RunWith(SpringRunner.class)
public class ChangeEntitySharedServiceTest {
	
	private static final LocalDateTime datetime = LocalDateTime.parse("2022-02-08T11:00:52");
	
	private static MockedStatic<LocalDateTime> mock;
	
	ChangeEntitySharedService changeEntitySharedService = new ChangeEntitySharedService();
	
	@BeforeEach
	void setup() {
		mock = Mockito.mockStatic(LocalDateTime.class,Mockito.CALLS_REAL_METHODS);
		mock.when(LocalDateTime::now).thenReturn(datetime);
	}
	
	@AfterEach
	void tearDown() throws Exception{
		mock.close();
	}
	
	@Test
    void setToAccountでRegisterFormをAccountに詰め替える() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setUserName("マクベイ");
		form.setPassword("encodedPassword");
		form.setMail("north-east@gmail.com");
		form.setConfirmPassword("sun-flan-sis");
		Account account = changeEntitySharedService.setToAccount(form);
		assertEquals(account.getUserName(),"マクベイ");
		assertEquals(account.getPassword(),"encodedPassword");
		assertEquals(account.getMail(),"north-east@gmail.com");
		assertEquals(account.getRole(),"ROLE_USER");
	}
	
	@Test
    void setToPasswordHistoryでRegisterFormをPasswordHistoryに詰め替える() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setUserName("マクベイ");
		form.setPassword("encodedPassword");
		form.setMail("north-east@gmail.com");
		form.setConfirmPassword("sun-flan-sis");
		PasswordHistory history = changeEntitySharedService.setToPasswordHistory(form);
		assertEquals(history.getUserName(),"マクベイ");
		assertEquals(history.getPassword(),"encodedPassword");
		assertEquals(history.getUseDay(),datetime);
	}
	
	@Test
    void setToDiaryRecordでDiaryRecordFormをDiaryRecordに詰め替える() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		form.setCategoryId(3);
		form.setDiaryDay(Date.valueOf("2022-02-04"));
		form.setRecord1("冷麺");
		form.setRecord2("焼肉");
		form.setRecord3("もやしナムル");
		form.setPrice(2980);
		form.setMemo("焼肉屋で外食");
		DiaryRecord record = changeEntitySharedService.setToDiaryRecord(form);
		assertEquals(null,record.getUserName());
		assertEquals(3,record.getCategoryId());
		assertEquals(Date.valueOf("2022-02-04"),record.getDiaryday());
		assertEquals("冷麺",record.getRecord1());
		assertEquals("焼肉",record.getRecord2());
		assertEquals("もやしナムル",record.getRecord3());
		assertEquals(2980,record.getPrice());
		assertEquals("焼肉屋で外食",record.getMemo());
	}

}
