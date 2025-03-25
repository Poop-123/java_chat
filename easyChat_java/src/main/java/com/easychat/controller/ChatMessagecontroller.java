package com.easychat.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.easychat.entity.po.ChatMessage;
import com.easychat.query.ChatMessageQuery;
import com.easychat.entity.vo.PaginationResultVO;
import org.springframework.web.bind.annotation.RestController;
import com.easychat.service.ChatMessageService;
import javax.annotation.Resource;
import com.easychat.enums.PageSize;
import com.easychat.query.SimplePage;
import org.springframework.web.bind.annotation.RequestMapping;
import com.easychat.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestBody;

/**
  * @Description:聊天消息表 Service
  * @Author:刘耿豪
  * @Date:2025/03/25
  */
@RestController
	@RequestMapping("/chatMessage")
public class ChatMessagecontroller extends ABaseController{

	@Resource
	private ChatMessageService chatMessageService;
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(ChatMessageQuery query){
		return getSuccessResponseVO(chatMessageService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO add(ChatMessage bean){
		this.chatMessageService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO addBatch(@RequestBody List<ChatMessage> listBean){
		this.chatMessageService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<ChatMessage> listBean){
		this.chatMessageService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据MessageId查询对象
	 */
	@RequestMapping("getChatMessageByMessageId")
	public ResponseVO getChatMessageByMessageId(Long messageId){
		return getSuccessResponseVO(this.chatMessageService.getChatMessageByMessageId(messageId));
	}

	/**
	 * 根据MessageId修改
	 */
	@RequestMapping("updateChatMessageByMessageId")
	public ResponseVO updateChatMessageByMessageId(ChatMessage bean,Long messageId){
		this.chatMessageService.updateChatMessageByMessageId(bean,messageId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据MessageId删除
	 */
	@RequestMapping("deleteChatMessageByMessageId")
	public ResponseVO deleteChatMessageByMessageId(Long messageId){
		this.chatMessageService.deleteChatMessageByMessageId(messageId);
		return getSuccessResponseVO(null);
	}

}
