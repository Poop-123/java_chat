package com.easychat.service;

import java.util.Date;

import com.easychat.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.easychat.enums.DateTimePatternEnum;
import com.easychat.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.easychat.entity.po.GroupInfo;
import com.easychat.query.GroupInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import org.springframework.web.multipart.MultipartFile;

/**
  * @Description: Service
  * @Author:刘耿豪
  * @Date:2025/03/19
  */
public interface GroupInfoService{

	/**
	 * 根据条件查询列表
	 */
	List<GroupInfo> findListByParam(GroupInfoQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(GroupInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<GroupInfo> findListByPage(GroupInfoQuery query);

	/**
	 * 新增
	 */
	Integer add(GroupInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<GroupInfo> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<GroupInfo> listBean);

	/**
	 * 根据GroupId查询对象
	 */
	GroupInfo getGroupInfoByGroupId(String groupId);

	/**
	 * 根据GroupId修改
	 */
	Integer updateGroupInfoByGroupId(GroupInfo bean,String groupId);

	/**
	 * 根据GroupId删除
	 */
	Integer deleteGroupInfoByGroupId(String groupId);
	/**
	 * 保存群组
	 */
	void saveGroup(GroupInfo groupInfo, MultipartFile avatarFile,MultipartFile avatarCover) throws BusinessException;

}
