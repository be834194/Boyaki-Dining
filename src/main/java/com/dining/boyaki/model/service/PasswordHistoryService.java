package com.dining.boyaki.model.service;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.PasswordHistory;
import com.dining.boyaki.model.mapper.PasswordHistoryMapper;

@Service
public class PasswordHistoryService {
	
	private final PasswordHistoryMapper passwordHistoryMapper;
	
	public PasswordHistoryService(PasswordHistoryMapper passwordHistoryMapper) {
		this.passwordHistoryMapper = passwordHistoryMapper;
	}
	
	@Transactional(readOnly = true)
	public String findPassword(String userName,String mail) {
		return passwordHistoryMapper.findPassword(userName, mail);
	}
	
	@Transactional(readOnly = true)
	public List<PasswordHistory> findUseFrom(String userName,LocalDateTime time){
		return passwordHistoryMapper.findUseFrom(userName, time);
	}

}
