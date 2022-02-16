package com.dining.boyaki.model.mapper;

import java.util.Date;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dining.boyaki.model.entity.DiaryRecord;

@Mapper
public interface DiaryRecordMapper {
	
	DiaryRecord findOneDiaryRecord(@Param("userName")String userName,
			                        @Param("categoryId")int categoryId,
			                        @Param("diaryDay")Date diaryDay);
	void insertDiaryRecord(DiaryRecord record);
	void updateDiaryRecord(DiaryRecord record);
	void deleteDiaryRecord(DiaryRecord record);

}
