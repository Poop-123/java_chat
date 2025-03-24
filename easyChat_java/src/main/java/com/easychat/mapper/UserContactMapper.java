package com.easychat.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

/**
  * @Description:联系人
  * @Author:刘耿豪
  * @Date:2025/03/19
  */
public interface UserContactMapper<T,P> extends BaseMapper{
	/**
	 * 根据UserIdAndContactId查询
	 */
	 T selectByUserIdAndContactId(@Param("userId") String userId, @Param("contactId") String contactId);

	/**
	 * 根据UserIdAndContactId更新
	 */
	 Integer updateByUserIdAndContactId(@Param("bean") T t, @Param("userId") String userId, @Param("contactId") String contactId);

	/**
	 * 根据UserIdAndContactId删除
	 */
	 Integer deleteByUserIdAndContactId(@Param("userId") String userId, @Param("contactId") String contactId);

    void updateByParam(T updateUserContact, P userContactQuery);
}