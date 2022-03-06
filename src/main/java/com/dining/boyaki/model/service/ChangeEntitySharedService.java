package com.dining.boyaki.model.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.AccountInfo;
import com.dining.boyaki.model.entity.DiaryRecord;
import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.model.form.AccountInfoForm;
import com.dining.boyaki.model.form.PasswordChangeForm;
import com.dining.boyaki.model.form.DiaryRecordForm;
import com.dining.boyaki.model.form.RegisterForm;

@Service
public class ChangeEntitySharedService {
	
	public Account setToAccount(RegisterForm form) {
		Account account = new Account();
		account.setUserName(form.getUserName());
		account.setPassword(form.getPassword());
		account.setMail(form.getMail());
		account.setRole("ROLE_USER");
		return account;
	}
	
	public Account setToAccount(PasswordChangeForm form) {
		Account account = new Account();
		account.setUserName(form.getUserName());
		account.setPassword(form.getPassword());
		account.setMail(form.getMail());
		return account;
	}
	
	public PasswordHistory setToPasswordHistory(RegisterForm form) {
		PasswordHistory history = new PasswordHistory();
		history.setUserName(form.getUserName());
		history.setPassword(form.getPassword());
		history.setUseDay(LocalDateTime.now());
		return history;
	}
	
	public PasswordHistory setToPasswordHistory(PasswordChangeForm form) {
		PasswordHistory history = new PasswordHistory();
		history.setUserName(form.getUserName());
		history.setPassword(form.getPassword());
		history.setUseDay(LocalDateTime.now());
		return history;
	}
	
	public AccountInfo setToAccountInfo(RegisterForm form) {
		AccountInfo info = new AccountInfo();
		info.setUserName(form.getUserName());
		info.setNickName(form.getUserName());
		info.setProfile(null);
		info.setStatus(0);
		info.setGender(0);
		info.setAge(20);
		return info;
	}
	
	public AccountInfo setToAccountInfo(AccountInfoForm form) {
		AccountInfo info = new AccountInfo();
		info.setUserName(form.getUserName());
		info.setNickName(form.getNickName());
		info.setProfile(form.getProfile());
		info.setStatus(form.getStatus());
		info.setGender(form.getGender());
		info.setAge(form.getAge());
		return info;
	}
	
	public DiaryRecord setToDiaryRecord(DiaryRecordForm form) {
		DiaryRecord record = new DiaryRecord();
		record.setUserName(form.getUserName());
		record.setCategoryId(form.getCategoryId());
		record.setDiaryDay(form.getDiaryDay());
		record.setRecord1(form.getRecord1());
		record.setRecord2(form.getRecord2());
		record.setRecord3(form.getRecord3());
		record.setPrice(form.getPrice());
		record.setMemo(form.getMemo());
		record.setCreateAt(form.getCreateAt());
		return record;
	}
	
	public DiaryRecordForm setToDiaryRecordForm(DiaryRecord record) {
		DiaryRecordForm form = new DiaryRecordForm();
		form.setUserName(record.getUserName());
		form.setCategoryId(record.getCategoryId());
		form.setDiaryDay(record.getDiaryDay());
		form.setRecord1(record.getRecord1());
		form.setRecord2(record.getRecord2());
		form.setRecord3(record.getRecord3());
		form.setPrice(record.getPrice());
		form.setMemo(record.getMemo());
		form.setCreateAt(record.getCreateAt());
		return form;
	}
	
	public AccountInfoForm setToAccountInfoForm(AccountInfo info) {
		AccountInfoForm form = new AccountInfoForm();
		form.setUserName(info.getUserName());
		form.setNickName(info.getNickName());
		form.setProfile(info.getProfile());
		form.setStatus(info.getStatus());
		form.setAge(info.getAge());
		form.setGender(info.getGender());
		return form;
	}

}
