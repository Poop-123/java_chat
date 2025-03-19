package com.easychat.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

/**
  * @Description:用户联系人表
  * @Author:刘耿豪
  * @Date:2025/03/19
  */
public interface UserContactApplyMapper<T,P> extends BaseMapper{
	/**
	 * 根据ApplyId查询
	 */
	 T selectByApplyId(@Param("applyId") Integer applyId);

	/**
	 * 根据ApplyId更新
	 */
	 Integer updateByApplyId(@Param("bean") T t, @Param("applyId") Integer applyId);

	/**
	 * 根据ApplyId删除
	 */
	 Integer deleteByApplyId(@Param("applyId") Integer applyId);

}