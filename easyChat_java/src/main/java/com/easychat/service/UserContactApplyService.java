package com.easychat.service;

import com.easychat.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.easychat.entity.po.UserContactApply;
import com.easychat.query.UserContactApplyQuery;
import com.easychat.entity.vo.PaginationResultVO;

/**
  * @Description:用户联系人表 Service
  * @Author:刘耿豪
  * @Date:2025/03/22
  */
public interface UserContactApplyService{

	/**
	 * 根据条件查询列表
	 */
	List<UserContactApply> findListByParam(UserContactApplyQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserContactApplyQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserContactApply> findListByPage(UserContactApplyQuery query);

	/**
	 * 新增
	 */
	Integer add(UserContactApply bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserContactApply> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<UserContactApply> listBean);

	/**
	 * 根据ApplyId查询对象
	 */
	UserContactApply getUserContactApplyByApplyId(Integer applyId);

	/**
	 * 根据ApplyId修改
	 */
	Integer updateUserContactApplyByApplyId(UserContactApply bean,Integer applyId);

	/**
	 * 根据ApplyId删除
	 */
	Integer deleteUserContactApplyByApplyId(Integer applyId);

	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId查询对象
	 */
	UserContactApply getUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId,String receiveUserId,String contactId);

	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId修改
	 */
	Integer updateUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(UserContactApply bean,String applyUserId,String receiveUserId,String contactId);

	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId删除
	 */
	Integer deleteUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId,String receiveUserId,String contactId);
	/**
	 * 处理申请
	 */
	void dealWithApply(String userId,Integer applyId,Integer status) throws BusinessException;
	/**
	 * 添加联系人
	 */
	void addContact(String applyUserId,String receiveUserId,String contactId,Integer contactType,String applyInfo) throws BusinessException;
}
