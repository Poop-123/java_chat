package com.easychat.controller;

import java.util.List;
import com.easychat.entity.po.ChatSession;
import com.easychat.query.ChatSessionQuery;
import com.easychat.entity.vo.PaginationResultVO;
import org.springframework.web.bind.annotation.RestController;
import com.easychat.service.ChatSessionService;
import javax.annotation.Resource;
import com.easychat.enums.PageSize;
import com.easychat.query.SimplePage;
import org.springframework.web.bind.annotation.RequestMapping;
import com.easychat.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestBody;

/**
  * @Description:会话信息 Service
  * @Author:刘耿豪
  * @Date:2025/03/25
  */
@RestController
	@RequestMapping("/chatSession")
public class ChatSessioncontroller extends ABaseController{

	@Resource
	private ChatSessionService chatSessionService;
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(ChatSessionQuery query){
		return getSuccessResponseVO(chatSessionService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO add(ChatSession bean){
		this.chatSessionService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO addBatch(@RequestBody List<ChatSession> listBean){
		this.chatSessionService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<ChatSession> listBean){
		this.chatSessionService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据SessionId查询对象
	 */
	@RequestMapping("getChatSessionBySessionId")
	public ResponseVO getChatSessionBySessionId(String sessionId){
		return getSuccessResponseVO(this.chatSessionService.getChatSessionBySessionId(sessionId));
	}

	/**
	 * 根据SessionId修改
	 */
	@RequestMapping("updateChatSessionBySessionId")
	public ResponseVO updateChatSessionBySessionId(ChatSession bean,String sessionId){
		this.chatSessionService.updateChatSessionBySessionId(bean,sessionId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据SessionId删除
	 */
	@RequestMapping("deleteChatSessionBySessionId")
	public ResponseVO deleteChatSessionBySessionId(String sessionId){
		this.chatSessionService.deleteChatSessionBySessionId(sessionId);
		return getSuccessResponseVO(null);
	}

}
