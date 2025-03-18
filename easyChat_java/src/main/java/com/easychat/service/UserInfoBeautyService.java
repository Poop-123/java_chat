package com.easychat.service;

import java.util.List;
import com.easychat.entity.po.UserInfoBeauty;
import com.easychat.query.UserInfoBeautyQuery;
import com.easychat.entity.vo.PaginationResultVO;

/**
  * @Description: Service
  * @Author:刘耿豪
  * @Date:2025/03/18
  */
public interface UserInfoBeautyService{

	/**
	 * 根据条件查询列表
	 */
	List<UserInfoBeauty> findListByParam(UserInfoBeautyQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserInfoBeautyQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserInfoBeauty> findListByPage(UserInfoBeautyQuery query);

	/**
	 * 新增
	 */
	Integer add(UserInfoBeauty bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserInfoBeauty> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<UserInfoBeauty> listBean);

	/**
	 * 根据UserId查询对象
	 */
	UserInfoBeauty getUserInfoBeautyByUserId(Integer userId);

	/**
	 * 根据UserId修改
	 */
	Integer updateUserInfoBeautyByUserId(UserInfoBeauty bean,Integer userId);

	/**
	 * 根据UserId删除
	 */
	Integer deleteUserInfoBeautyByUserId(Integer userId);

	/**
	 * 根据Email查询对象
	 */
	UserInfoBeauty getUserInfoBeautyByEmail(String email);

	/**
	 * 根据Email修改
	 */
	Integer updateUserInfoBeautyByEmail(UserInfoBeauty bean,String email);

	/**
	 * 根据Email删除
	 */
	Integer deleteUserInfoBeautyByEmail(String email);

}
