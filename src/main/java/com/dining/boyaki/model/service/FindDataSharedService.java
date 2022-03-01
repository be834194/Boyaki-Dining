package com.dining.boyaki.model.service;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.DiaryRecord;
import com.dining.boyaki.model.mapper.FindDataMapper;

@Service
public class FindDataSharedService {
	
	private final FindDataMapper findDataMapper;
	
	public FindDataSharedService(FindDataMapper findDataMapper) {
		this.findDataMapper = findDataMapper;
	}
	
	@Transactional(readOnly = true)
	public String findUserName(String userName) {
		return findDataMapper.findUserName(userName);
	}
	
	@Transactional(readOnly = true)
	public String findUserNameFromMail(String mail) {
		return findDataMapper.findUserNameFromMail(mail);
	}
	
	@Transactional(readOnly = true)
	public String findMail(String mail) {
		return findDataMapper.findMail(mail);
	}
	
	@Transactional(readOnly = true)
    public DiaryRecord findOneDiaryRecord(String userName,int categoryId,Date diaryDay) {
		return findDataMapper.findOneDiaryRecord(userName, categoryId, diaryDay);
	}
	
	@Transactional(readOnly = true)
	public String findNickName(String userName) {
		return findDataMapper.findNickName(userName);
	}
}
