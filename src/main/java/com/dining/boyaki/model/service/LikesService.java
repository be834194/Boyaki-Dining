package com.dining.boyaki.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dining.boyaki.model.mapper.LikesMapper;

@Service
public class LikesService {
	
	private final LikesMapper likesMapper;

	public LikesService(LikesMapper likesMapper) {
		this.likesMapper = likesMapper;
	}
	
	@Transactional(readOnly = true)
	public int sumRate(long postId) {
		return likesMapper.sumRate(postId).orElse(0);
	}
	
	
	@Transactional(readOnly = false)
	public void updateRate(long postId,String userName) {
		//あるユーザの、投稿に対する現在の評価状態を取得
		int currentRate = likesMapper.currentRate(postId, userName).orElse(-1);
		if(currentRate == -1) {
			likesMapper.insertRate(postId, userName,1); //レコードが無いなら1をinsert
		}else if(currentRate == 0){
			likesMapper.updateRate(postId, userName,1); //評価が0なら1にupdate
		}else {
			likesMapper.updateRate(postId, userName,0); //評価が1なら0にupdate
		}
	}

}
