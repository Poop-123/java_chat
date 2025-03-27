package com.easychat.service;

import java.util.List;
import com.easychat.entity.po.ChatSessionUser;
import com.easychat.query.ChatSessionUserQuery;
import com.easychat.entity.vo.PaginationResultVO;

/**
  * @Description:会话用户表 Service
  * @Author:刘耿豪
  * @Date:2025/03/25
  */
public interface ChatSessionUserService{

	/**
	 * 根据条件查询列表
	 */
	List<ChatSessionUser> findListByParam(ChatSessionUserQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(ChatSessionUserQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<ChatSessionUser> findListByPage(ChatSessionUserQuery query);

	/**
	 * 新增
	 */
	Integer add(ChatSessionUser bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<ChatSessionUser> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<ChatSessionUser> listBean);

	/**
	 * 根据UserIdAndContactId查询对象
	 */
	ChatSessionUser getChatSessionUserByUserIdAndContactId(String userId,String contactId);

	/**
	 * 根据UserIdAndContactId修改
	 */
	Integer updateChatSessionUserByUserIdAndContactId(ChatSessionUser bean,String userId,String contactId);

	/**
	 * 根据UserIdAndContactId删除
	 */
	Integer deleteChatSessionUserByUserIdAndContactId(String userId,String contactId);
	/**
	 * 修改昵称
	 */
	public void updateRedundancyInfo(String contactName,String contactId);
}
