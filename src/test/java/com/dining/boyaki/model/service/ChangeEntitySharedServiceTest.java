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
import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.DiaryRecord;
import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.model.form.AccountInfoForm;
import com.dining.boyaki.model.form.DiaryRecordForm;
import com.dining.boyaki.model.form.PasswordChangeForm;
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
		assertEquals("マクベイ",account.getUserName());
		assertEquals("encodedPassword",account.getPassword());
		assertEquals("north-east@gmail.com",account.getMail());
		assertEquals("ROLE_USER",account.getRole());
	}
	
	@Test
    void setToAccountでPasswordChangeFormをAccountに詰め替える() throws Exception{
		PasswordChangeForm form = new PasswordChangeForm();
		form.setUserName("加藤健");
		form.setMail("example@ezweb.ne.jp");
		form.setOldPassword("pinballs");
		form.setPassword("wonderSong");
		form.setConfirmPassword("WONDERSONG");
		
		Account account = changeEntitySharedService.setToAccount(form);
		assertEquals("加藤健",account.getUserName());
		assertEquals("wonderSong",account.getPassword());
		assertEquals("example@ezweb.ne.jp",account.getMail());
		assertEquals(null,account.getRole());
	}
	
	@Test
    void setToPasswordHistoryでRegisterFormをPasswordHistoryに詰め替える() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setUserName("マクベイ");
		form.setPassword("encodedPassword");
		form.setMail("north-east@gmail.com");
		form.setConfirmPassword("sun-flan-sis");
		
		PasswordHistory history = changeEntitySharedService.setToPasswordHistory(form);
		assertEquals("マクベイ",history.getUserName());
		assertEquals("encodedPassword",history.getPassword());
		assertEquals(datetime,history.getUseDay());
	}
	
	@Test
    void setToPasswordHistoryでPasswordChangeFormをPasswordHistoryに詰め替える() throws Exception{
		PasswordChangeForm form = new PasswordChangeForm();
		form.setUserName("加藤健");
		form.setMail("example@ezweb.ne.jp");
		form.setOldPassword("pinballs");
		form.setPassword("wonderSong");
		form.setConfirmPassword("WONDERSONG");
		
		PasswordHistory history = changeEntitySharedService.setToPasswordHistory(form);
		assertEquals("加藤健",history.getUserName());
		assertEquals("wonderSong",history.getPassword());
		assertEquals(datetime,history.getUseDay());
	}
	
	@Test
    void setToAccountInfoでRegisterFormをAccountInfoに詰め替える() throws Exception{
		RegisterForm form = new RegisterForm();
		form.setUserName("マクベイ");
		form.setPassword("encodedPassword");
		form.setMail("north-east@gmail.com");
		form.setConfirmPassword("sun-flan-sis");
		
		AccountInfo info = changeEntitySharedService.setToAccountInfo(form);
		assertEquals("マクベイ",info.getUserName());
		assertEquals("マクベイ",info.getNickName());
		assertEquals(null,info.getProfile());
		assertEquals(0,info.getStatus());
		assertEquals(0,info.getGender());
		assertEquals(0,info.getAge());
	}
	
	@Test
    void setToAccountInfoでAccountInfoFormをAccountInfoに詰め替える() throws Exception{
		AccountInfoForm form = new AccountInfoForm();
		form.setUserName("miho");
		form.setNickName("匿名ちゃん");
		form.setProfile("毎日定時で帰りたい");
		form.setStatus(0);
		form.setGender(2);
		form.setAge(21);
		
		AccountInfo info = changeEntitySharedService.setToAccountInfo(form);
		assertEquals("miho",info.getUserName());
		assertEquals("匿名ちゃん",info.getNickName());
		assertEquals("毎日定時で帰りたい",info.getProfile());
		assertEquals(0,info.getStatus());
		assertEquals(2,info.getGender());
		assertEquals(21,info.getAge());
	}
	
	@Test
    void setToAccountInfoFormでAccountInfoをAccountInfoFormに詰め替える() throws Exception{
		AccountInfo info = new AccountInfo();
		info.setUserName("miho");
		info.setNickName("匿名ちゃん");
		info.setProfile("毎日定時で帰りたい");
		info.setStatus(0);
		info.setGender(2);
		info.setAge(21);
		
		AccountInfoForm form = changeEntitySharedService.setToAccountInfoForm(info);
		assertEquals("miho",form.getUserName());
		assertEquals("匿名ちゃん",form.getNickName());
		assertEquals("毎日定時で帰りたい",form.getProfile());
		assertEquals(0,form.getStatus());
		assertEquals(2,form.getGender());
		assertEquals(21,form.getAge());
	}
	
	@Test
    void setToDiaryRecordでDiaryRecordFormをDiaryRecordに詰め替える() throws Exception{
		DiaryRecordForm form = new DiaryRecordForm();
		form.setUserName("糸井");
		form.setCategoryId(3);
		form.setDiaryDay(Date.valueOf("2022-02-04"));
		form.setRecord1("冷麺");
		form.setRecord2("焼肉");
		form.setRecord3("もやしナムル");
		form.setPrice(2980);
		form.setMemo("焼肉屋で外食");
		form.setCreateAt(datetime);
		
		DiaryRecord record = changeEntitySharedService.setToDiaryRecord(form);
		assertEquals("糸井",record.getUserName());
		assertEquals(3,record.getCategoryId());
		assertEquals(Date.valueOf("2022-02-04"),record.getDiaryDay());
		assertEquals("冷麺",record.getRecord1());
		assertEquals("焼肉",record.getRecord2());
		assertEquals("もやしナムル",record.getRecord3());
		assertEquals(2980,record.getPrice());
		assertEquals("焼肉屋で外食",record.getMemo());
		assertEquals(datetime,record.getCreateAt());
	}
	
	@Test
    void setToDiaryRecordFormでDiaryRecordをDiaryRecordFormに詰め替える() throws Exception{
		DiaryRecord record = new DiaryRecord();
		record.setUserName("糸井");
		record.setCategoryId(3);
		record.setDiaryDay(Date.valueOf("2022-02-04"));
		record.setRecord1("冷麺");
		record.setRecord2("焼肉");
		record.setRecord3("もやしナムル");
		record.setPrice(2980);
		record.setMemo("焼肉屋で外食");
		record.setCreateAt(datetime);
		
		DiaryRecordForm form = changeEntitySharedService.setToDiaryRecordForm(record);
		assertEquals("糸井",form.getUserName());
		assertEquals(3,form.getCategoryId());
		assertEquals(Date.valueOf("2022-02-04"),form.getDiaryDay());
		assertEquals("冷麺",form.getRecord1());
		assertEquals("焼肉",form.getRecord2());
		assertEquals("もやしナムル",form.getRecord3());
		assertEquals(2980,form.getPrice());
		assertEquals("焼肉屋で外食",form.getMemo());
		assertEquals(datetime,form.getCreateAt());
	}
	
}
