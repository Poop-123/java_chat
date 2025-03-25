package com.easychat.service;

import java.util.List;
import com.easychat.entity.po.ChatSession;
import com.easychat.query.ChatSessionQuery;
import com.easychat.entity.vo.PaginationResultVO;

/**
  * @Description:会话信息 Service
  * @Author:刘耿豪
  * @Date:2025/03/25
  */
public interface ChatSessionService{

	/**
	 * 根据条件查询列表
	 */
	List<ChatSession> findListByParam(ChatSessionQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(ChatSessionQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<ChatSession> findListByPage(ChatSessionQuery query);

	/**
	 * 新增
	 */
	Integer add(ChatSession bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<ChatSession> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<ChatSession> listBean);

	/**
	 * 根据SessionId查询对象
	 */
	ChatSession getChatSessionBySessionId(String sessionId);

	/**
	 * 根据SessionId修改
	 */
	Integer updateChatSessionBySessionId(ChatSession bean,String sessionId);

	/**
	 * 根据SessionId删除
	 */
	Integer deleteChatSessionBySessionId(String sessionId);

}
