package com.easychat.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

/**
  * @Description:
  * @Author:刘耿豪
  * @Date:2025/03/19
  */
public interface GroupInfoMapper<T,P> extends BaseMapper{
	/**
	 * 根据GroupId查询
	 */
	 T selectByGroupId(@Param("groupId") String groupId);

	/**
	 * 根据GroupId更新
	 */
	 Integer updateByGroupId(@Param("bean") T t, @Param("groupId") String groupId);

	/**
	 * 根据GroupId删除
	 */
	 Integer deleteByGroupId(@Param("groupId") String groupId);

}