package com.easychat.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

/**
  * @Description:会话信息
  * @Author:刘耿豪
  * @Date:2025/03/25
  */
public interface ChatSessionMapper<T,P> extends BaseMapper{
	/**
	 * 根据SessionId查询
	 */
	 T selectBySessionId(@Param("sessionId") String sessionId);

	/**
	 * 根据SessionId更新
	 */
	 Integer updateBySessionId(@Param("bean") T t, @Param("sessionId") String sessionId);

	/**
	 * 根据SessionId删除
	 */
	 Integer deleteBySessionId(@Param("sessionId") String sessionId);

}