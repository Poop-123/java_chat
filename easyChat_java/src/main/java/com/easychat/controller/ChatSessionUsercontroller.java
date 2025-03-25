package com.easychat.controller;

import java.util.List;
import com.easychat.entity.po.ChatSessionUser;
import com.easychat.query.ChatSessionUserQuery;
import com.easychat.entity.vo.PaginationResultVO;
import org.springframework.web.bind.annotation.RestController;
import com.easychat.service.ChatSessionUserService;
import javax.annotation.Resource;
import com.easychat.enums.PageSize;
import com.easychat.query.SimplePage;
import org.springframework.web.bind.annotation.RequestMapping;
import com.easychat.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestBody;

/**
  * @Description:会话用户表 Service
  * @Author:刘耿豪
  * @Date:2025/03/25
  */
@RestController
	@RequestMapping("/chatSessionUser")
public class ChatSessionUsercontroller extends ABaseController{

	@Resource
	private ChatSessionUserService chatSessionUserService;
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(ChatSessionUserQuery query){
		return getSuccessResponseVO(chatSessionUserService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO add(ChatSessionUser bean){
		this.chatSessionUserService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO addBatch(@RequestBody List<ChatSessionUser> listBean){
		this.chatSessionUserService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<ChatSessionUser> listBean){
		this.chatSessionUserService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UserIdAndContactId查询对象
	 */
	@RequestMapping("getChatSessionUserByUserIdAndContactId")
	public ResponseVO getChatSessionUserByUserIdAndContactId(String userId,String contactId){
		return getSuccessResponseVO(this.chatSessionUserService.getChatSessionUserByUserIdAndContactId(userId,contactId));
	}

	/**
	 * 根据UserIdAndContactId修改
	 */
	@RequestMapping("updateChatSessionUserByUserIdAndContactId")
	public ResponseVO updateChatSessionUserByUserIdAndContactId(ChatSessionUser bean,String userId,String contactId){
		this.chatSessionUserService.updateChatSessionUserByUserIdAndContactId(bean,userId,contactId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UserIdAndContactId删除
	 */
	@RequestMapping("deleteChatSessionUserByUserIdAndContactId")
	public ResponseVO deleteChatSessionUserByUserIdAndContactId(String userId,String contactId){
		this.chatSessionUserService.deleteChatSessionUserByUserIdAndContactId(userId,contactId);
		return getSuccessResponseVO(null);
	}

}
