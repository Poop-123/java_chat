package com.easychat.controller;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.easychat.enums.DateTimePatternEnum;
import com.easychat.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.easychat.entity.po.UserContact;
import com.easychat.query.UserContactQuery;
import com.easychat.entity.vo.PaginationResultVO;
import org.springframework.web.bind.annotation.RestController;
import com.easychat.service.UserContactService;
import javax.annotation.Resource;
import com.easychat.enums.PageSize;
import com.easychat.query.SimplePage;
import org.springframework.web.bind.annotation.RequestMapping;
import com.easychat.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestBody;

/**
  * @Description:联系人 Service
  * @Author:刘耿豪
  * @Date:2025/03/19
  */
@RestController
	@RequestMapping("/userContact")
public class UserContactcontroller extends ABaseController{

	@Resource
	private UserContactService userContactService;
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(UserContactQuery query){
		return getSuccessResponseVO(userContactService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO add(UserContact bean){
		this.userContactService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO addBatch(@RequestBody List<UserContact> listBean){
		this.userContactService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<UserContact> listBean){
		this.userContactService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UserIdAndContactId查询对象
	 */
	@RequestMapping("getUserContactByUserIdAndContactId")
	public ResponseVO getUserContactByUserIdAndContactId(String userId,String contactId){
		return getSuccessResponseVO(this.userContactService.getUserContactByUserIdAndContactId(userId,contactId));
	}

	/**
	 * 根据UserIdAndContactId修改
	 */
	@RequestMapping("updateUserContactByUserIdAndContactId")
	public ResponseVO updateUserContactByUserIdAndContactId(UserContact bean,String userId,String contactId){
		this.userContactService.updateUserContactByUserIdAndContactId(bean,userId,contactId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UserIdAndContactId删除
	 */
	@RequestMapping("deleteUserContactByUserIdAndContactId")
	public ResponseVO deleteUserContactByUserIdAndContactId(String userId,String contactId){
		this.userContactService.deleteUserContactByUserIdAndContactId(userId,contactId);
		return getSuccessResponseVO(null);
	}

}
