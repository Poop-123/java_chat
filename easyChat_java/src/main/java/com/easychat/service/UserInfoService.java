package com.easychat.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.easychat.dto.TokenUserInfoDto;
import com.easychat.entity.po.UserInfo;
import com.easychat.exception.BusinessException;
import com.easychat.query.UserInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import org.springframework.web.multipart.MultipartFile;

/**
  * @Description: Service
  * @Author:刘耿豪
  * @Date:2025/03/18
  */
public interface UserInfoService{

	/**
	 * 根据条件查询列表
	 */
	List<UserInfo> findListByParam(UserInfoQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserInfo> findListByPage(UserInfoQuery query);

	/**
	 * 新增
	 */
	Integer add(UserInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserInfo> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<UserInfo> listBean);

	/**
	 * 根据UserId查询对象
	 */
	UserInfo getUserInfoByUserId(String userId);

	/**
	 * 根据UserId修改
	 */
	Integer updateUserInfoByUserId(UserInfo bean,String userId);

	/**
	 * 根据UserId删除
	 */
	Integer deleteUserInfoByUserId(String userId);

	/**
	 * 根据Email查询对象
	 */
	UserInfo getUserInfoByEmail(String email);

	/**
	 * 根据Email修改
	 */
	Integer updateUserInfoByEmail(UserInfo bean,String email);

	/**
	 * 根据Email删除
	 */
	Integer deleteUserInfoByEmail(String email);
	/**
	 * 注册
	 */
	void register(String email, String nickName, String password) throws BusinessException;
	/**
	 * 登录
	 */
	TokenUserInfoDto login(String email, String password) throws BusinessException;
	/**
	 * 更新个人账户
	 */
	void updateUserInfo(UserInfo userInfo, MultipartFile avtarFile,MultipartFile avtarCover) throws IOException;

	/**
	 * 启用禁用账户
	 */
	void updateUserStatus(Integer status,String userId) throws BusinessException;
	/**
	 * 强制下线
	 */
	void forceOffLine(String userId);


}
