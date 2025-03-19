package com.easychat.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.easychat.entity.po.UserContactApply;
import com.easychat.query.UserContactApplyQuery;
import com.easychat.entity.vo.PaginationResultVO;
import org.springframework.web.bind.annotation.RestController;
import com.easychat.service.UserContactApplyService;
import javax.annotation.Resource;
import com.easychat.enums.PageSize;
import com.easychat.query.SimplePage;
import org.springframework.web.bind.annotation.RequestMapping;
import com.easychat.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestBody;

/**
  * @Description:用户联系人表 Service
  * @Author:刘耿豪
  * @Date:2025/03/19
  */
@RestController
	@RequestMapping("/userContactApply")
public class UserContactApplycontroller extends ABaseController{

	@Resource
	private UserContactApplyService userContactApplyService;
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(UserContactApplyQuery query){
		return getSuccessResponseVO(userContactApplyService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO add(UserContactApply bean){
		this.userContactApplyService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO addBatch(@RequestBody List<UserContactApply> listBean){
		this.userContactApplyService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<UserContactApply> listBean){
		this.userContactApplyService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据ApplyId查询对象
	 */
	@RequestMapping("getUserContactApplyByApplyId")
	public ResponseVO getUserContactApplyByApplyId(Integer applyId){
		return getSuccessResponseVO(this.userContactApplyService.getUserContactApplyByApplyId(applyId));
	}

	/**
	 * 根据ApplyId修改
	 */
	@RequestMapping("updateUserContactApplyByApplyId")
	public ResponseVO updateUserContactApplyByApplyId(UserContactApply bean,Integer applyId){
		this.userContactApplyService.updateUserContactApplyByApplyId(bean,applyId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据ApplyId删除
	 */
	@RequestMapping("deleteUserContactApplyByApplyId")
	public ResponseVO deleteUserContactApplyByApplyId(Integer applyId){
		this.userContactApplyService.deleteUserContactApplyByApplyId(applyId);
		return getSuccessResponseVO(null);
	}

}
