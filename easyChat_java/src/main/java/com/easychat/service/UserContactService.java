package com.easychat.service;

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

/**
  * @Description:联系人 Service
  * @Author:刘耿豪
  * @Date:2025/03/19
  */
public interface UserContactService{

	/**
	 * 根据条件查询列表
	 */
	List<UserContact> findListByParam(UserContactQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserContactQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserContact> findListByPage(UserContactQuery query);

	/**
	 * 新增
	 */
	Integer add(UserContact bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserContact> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<UserContact> listBean);

	/**
	 * 根据UserIdAndContactId查询对象
	 */
	UserContact getUserContactByUserIdAndContactId(String userId,String contactId);

	/**
	 * 根据UserIdAndContactId修改
	 */
	Integer updateUserContactByUserIdAndContactId(UserContact bean,String userId,String contactId);

	/**
	 * 根据UserIdAndContactId删除
	 */
	Integer deleteUserContactByUserIdAndContactId(String userId,String contactId);

}
