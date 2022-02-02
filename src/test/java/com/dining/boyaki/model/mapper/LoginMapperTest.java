package com.dining.boyaki.model.mapper;

import static org.junit.Assert.assertEquals;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.entity.Account;

@MybatisTest
@Transactional
public class LoginMapperTest {
	
	@Autowired
	LoginMapper loginMapper;
	
	@Autowired
	SqlSession session;
	
	@AfterEach
	void tearDown() {
		session.flushStatements(); 
	}
	
	@Test
	void findAccountでユーザを一人見つける() throws Exception{
		Account existAccount = loginMapper.findAccount("加藤健");
		assertEquals(existAccount.getUserName(), "加藤健");
		assertEquals(existAccount.getPassword(), "pinballs");
		assertEquals(existAccount.getRole(), "ROLE_USER");
	}

}
