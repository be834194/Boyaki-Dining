package com.dining.boyaki.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
	public String findMail(String mail) {
		return findDataMapper.findMail(mail);
	}
}
