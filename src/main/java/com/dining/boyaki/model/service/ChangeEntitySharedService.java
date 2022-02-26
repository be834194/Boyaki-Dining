package com.dining.boyaki.model.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.dining.boyaki.model.entity.Account;
import com.dining.boyaki.model.entity.DiaryRecord;
import com.dining.boyaki.model.entity.PasswordHistory;
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
	
	public PasswordHistory setToPasswordHistory(RegisterForm form) {
		PasswordHistory history = new PasswordHistory();
		history.setUserName(form.getUserName());
		history.setPassword(form.getPassword());
		history.setUseDay(LocalDateTime.now());
		return history;
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

}
