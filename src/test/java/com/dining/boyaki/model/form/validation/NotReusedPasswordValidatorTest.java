package com.dining.boyaki.model.form.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import com.dining.boyaki.model.form.PasswordChangeForm;
import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.model.service.PasswordHistoryService;

@RunWith(SpringRunner.class)
public class NotReusedPasswordValidatorTest {
	
	private static final LocalDateTime datetime = LocalDateTime.of(2022, 01, 15, 0, 0, 0);
	
	private static MockedStatic<LocalDateTime> mock;
	
	PasswordChangeForm form = new PasswordChangeForm();
	BindingResult bindingResult = new BindException(form, "PasswordChangeForm");
	
	@Mock
	PasswordEncoder passwordEncoder;
	
	@Mock
	PasswordHistoryService passwordHistoryService;
	
	@Mock
	PasswordValidator encodedPasswordHistoryValidator;
	
	@InjectMocks
	NotReusedPasswordValidator notReusedPasswordValidator;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
		mock.when(LocalDateTime::now).thenReturn(datetime);
	}
	
	@AfterEach //mockStatic????????????????????????
    void tearDown() throws Exception {
        mock.close();
    }
	
	@Test
	void validate???????????????????????????????????????????????????????????????() throws Exception{
		RuleResult result = new RuleResult();
		result.setValid(true);
		form = new PasswordChangeForm("?????????","example@ezweb.ne.jp","pinballs",
				                      "wonderSong","wonderSong");
		List<PasswordHistory> histories = new ArrayList<PasswordHistory>();
		PasswordHistory history1 = new PasswordHistory("?????????","ten_bear",LocalDateTime.of(2021, 12, 15, 01, 22,39));
		histories.add(history1);
		PasswordHistory history2 = new PasswordHistory("?????????","pinballs",LocalDateTime.of(2022, 01, 13, 01, 22,39));
		histories.add(history2);
		
		when(passwordEncoder.matches("wonderSong", "pinballs")).thenReturn(false);
		when(passwordHistoryService.findPassword("?????????", "example@ezweb.ne.jp")).thenReturn("pinballs");
		when(passwordHistoryService.findUseFrom("?????????", datetime.minusDays(30)
                .withHour(0).withMinute(0).withSecond(0).withNano(0))).thenReturn(histories);
		when(encodedPasswordHistoryValidator.validate(any())).thenReturn(result);
		
		notReusedPasswordValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		verify(passwordEncoder,times(1)).matches("wonderSong", "pinballs");
		verify(passwordHistoryService,times(1)).findPassword("?????????", "example@ezweb.ne.jp");
		verify(passwordHistoryService,times(1)).findUseFrom("?????????", datetime.minusDays(30)
                .withHour(0).withMinute(0).withSecond(0).withNano(0));
		verify(encodedPasswordHistoryValidator,times(1)).validate(any());
	}
	
	@Test
	void validate???30????????????????????????????????????????????????????????????????????????????????????() throws Exception{
		form = new PasswordChangeForm("?????????","example@ezweb.ne.jp","pinballs",
                                      "wonderSong","wonderSong");
		List<PasswordHistory> histories = new ArrayList<PasswordHistory>();
		
		when(passwordEncoder.matches("wonderSong", "pinballs")).thenReturn(false);
		when(passwordHistoryService.findPassword("?????????", "example@ezweb.ne.jp")).thenReturn("pinballs");
		when(passwordHistoryService.findUseFrom("?????????", datetime.minusDays(30)
                .withHour(0).withMinute(0).withSecond(0).withNano(0))).thenReturn(histories);
		when(encodedPasswordHistoryValidator.validate(any())).thenReturn(null);
		
		notReusedPasswordValidator.validate(form, bindingResult);
		assertEquals(0,bindingResult.getFieldErrorCount());
		verify(passwordEncoder,times(1)).matches("wonderSong", "pinballs");
		verify(passwordHistoryService,times(1)).findPassword("?????????", "example@ezweb.ne.jp");
		verify(passwordHistoryService,times(1)).findUseFrom("?????????", datetime.minusDays(30)
                .withHour(0).withMinute(0).withSecond(0).withNano(0));
		verify(encodedPasswordHistoryValidator,times(0)).validate(any());
	}
	
	@Test
	void validate????????????????????????????????????????????????????????????() throws Exception{
		form = new PasswordChangeForm("?????????","hogehoge@ezweb.ne.jp","pinballs",
                                      "wonderSong","wonderSong");
		
		when(passwordEncoder.matches("wonderSong", "pinballs")).thenReturn(false);
		when(passwordHistoryService.findPassword("?????????", "hogehoge@ezweb.ne.jp")).thenReturn(null);
		when(passwordHistoryService.findUseFrom("?????????", datetime.minusDays(30)
                .withHour(0).withMinute(0).withSecond(0).withNano(0))).thenReturn(null);
		when(encodedPasswordHistoryValidator.validate(any())).thenReturn(null);
		
		notReusedPasswordValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("mail")
				                .toString().contains("?????????????????????????????????????????????"));
		verify(passwordEncoder,times(0)).matches("wonderSong", "pinballs");
		verify(passwordHistoryService,times(1)).findPassword("?????????", "hogehoge@ezweb.ne.jp");
		verify(passwordHistoryService,times(0)).findUseFrom("?????????", datetime.minusDays(30)
                .withHour(0).withMinute(0).withSecond(0).withNano(0));
		verify(encodedPasswordHistoryValidator,times(0)).validate(any());
		
	}
	
	@Test
	void validate????????????????????????????????????????????????????????????() throws Exception{
		form = new PasswordChangeForm("?????????","example@ezweb.ne.jp","pinballs",
                                      "pinballs","pinballs");
		
		when(passwordEncoder.matches("pinballs", "pinballs")).thenReturn(true);
		when(passwordHistoryService.findPassword("?????????", "example@ezweb.ne.jp")).thenReturn("pinballs");
		when(passwordHistoryService.findUseFrom("?????????", datetime.minusDays(30)
                .withHour(0).withMinute(0).withSecond(0).withNano(0))).thenReturn(null);
		when(encodedPasswordHistoryValidator.validate(any())).thenReturn(null);
		
		notReusedPasswordValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("password")
				                .toString().contains("????????????????????????????????????????????????"));
		verify(passwordEncoder,times(1)).matches("pinballs", "pinballs");
		verify(passwordHistoryService,times(1)).findPassword("?????????", "example@ezweb.ne.jp");
		verify(passwordHistoryService,times(0)).findUseFrom("?????????", datetime.minusDays(30)
                .withHour(0).withMinute(0).withSecond(0).withNano(0));
		verify(encodedPasswordHistoryValidator,times(0)).validate(any());
	}
	
	@Test
	void validate??????????????????????????????30??????????????????????????????????????????????????????????????????() throws Exception{
		RuleResult result = new RuleResult();
		result.setValid(false);
		form = new PasswordChangeForm("?????????","example@ezweb.ne.jp","pinballs",
				                      "ten_bear","ten_bear");
		List<PasswordHistory> histories = new ArrayList<PasswordHistory>();
		PasswordHistory history1 = new PasswordHistory("?????????","ten_bear",LocalDateTime.of(2021, 12, 15, 01, 22,39));
		histories.add(history1);
		PasswordHistory history2 = new PasswordHistory("?????????","pinballs",LocalDateTime.of(2022, 01, 13, 01, 22,39));
		histories.add(history2);
		
		when(passwordEncoder.matches("ten_bear", "pinballs")).thenReturn(false);
		when(passwordHistoryService.findPassword("?????????", "example@ezweb.ne.jp")).thenReturn("pinballs");
		when(passwordHistoryService.findUseFrom("?????????", datetime.minusDays(30)
                .withHour(0).withMinute(0).withSecond(0).withNano(0))).thenReturn(histories);
		when(encodedPasswordHistoryValidator.validate(any())).thenReturn(result);
		
		notReusedPasswordValidator.validate(form, bindingResult);
		assertEquals(1,bindingResult.getFieldErrorCount());
		assertTrue(bindingResult.getFieldError("password")
				                .toString().contains("30??????????????????????????????????????????????????????????????????"));
		verify(passwordEncoder,times(1)).matches("ten_bear", "pinballs");
		verify(passwordHistoryService,times(1)).findPassword("?????????", "example@ezweb.ne.jp");
		verify(passwordHistoryService,times(1)).findUseFrom("?????????", datetime.minusDays(30)
                .withHour(0).withMinute(0).withSecond(0).withNano(0));
		verify(encodedPasswordHistoryValidator,times(1)).validate(any());
	}

}