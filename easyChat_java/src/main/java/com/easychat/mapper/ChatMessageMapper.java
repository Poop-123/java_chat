package com.easychat.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

/**
  * @Description:聊天消息表
  * @Author:刘耿豪
  * @Date:2025/03/25
  */
public interface ChatMessageMapper<T,P> extends BaseMapper{
	/**
	 * 根据MessageId查询
	 */
	 T selectByMessageId(@Param("messageId") Long messageId);

	/**
	 * 根据MessageId更新
	 */
	 Integer updateByMessageId(@Param("bean") T t, @Param("messageId") Long messageId);

	/**
	 * 根据MessageId删除
	 */
	 Integer deleteByMessageId(@Param("messageId") Long messageId);

    void updateByParam(@Param("bean")T uploadInfo, @Param("query")P messageQuery);
}