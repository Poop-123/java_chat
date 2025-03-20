package com.easychat.service.Impl;

import java.util.Date;

import com.easychat.config.AppConfig;
import com.easychat.dto.SysSettingDto;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.po.UserContact;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.mapper.UserContactMapper;
import com.easychat.query.UserContactQuery;
import com.easychat.redis.RedisComponent;
import com.easychat.utils.StringTools;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import com.easychat.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.easychat.entity.po.GroupInfo;
import com.easychat.query.GroupInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.GroupInfoService;
import com.easychat.mapper.GroupInfoMapper;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.easychat.query.SimplePage;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
  * @Description: Service
  * @Author:刘耿豪
  * @Date:2025/03/19
  */
@Service
public class GroupInfoServiceImpl implements GroupInfoService{

	@Resource
	private GroupInfoMapper<GroupInfo,GroupInfoQuery> groupInfoMapper;
	@Resource
	private RedisComponent redisComponent;
	@Resource
	private UserContactMapper<UserContact, UserContactQuery> userContactMapper;
    @Autowired
    private AppConfig appConfig;


	/**
	 * 根据条件查询列表
	 */
	public List<GroupInfo> findListByParam(GroupInfoQuery query){
		return this.groupInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(GroupInfoQuery query){
		return this.groupInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<GroupInfo> findListByPage(GroupInfoQuery query){
	Integer count=this.findCountByParam(query);
	Integer pageSize =query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
	SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
	query.setSimplePage(page);
	List<GroupInfo> list=this.findListByParam(query);
		return new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),list);
	}

	/**
	 * 新增
	 */
	public Integer add(GroupInfo bean){
		return this.groupInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<GroupInfo> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.groupInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	public Integer addOrUpdateBatch(List<GroupInfo> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.groupInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据GroupId查询对象
	 */
	public GroupInfo getGroupInfoByGroupId(String groupId){
		return this.groupInfoMapper.selectByGroupId(groupId);
	}

	/**
	 * 根据GroupId修改
	 */
	public Integer updateGroupInfoByGroupId(GroupInfo bean,String groupId){
		return this.groupInfoMapper.updateByGroupId(bean,groupId);
	}

	/**
	 * 根据GroupId删除
	 */
	public Integer deleteGroupInfoByGroupId(String groupId){
		return this.groupInfoMapper.deleteByGroupId(groupId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveGroup(GroupInfo groupInfo, MultipartFile avatarFile, MultipartFile avatarCover) throws BusinessException {
        Date curDate=new Date();
		//新增
		if(StringTools.isEmpty(groupInfo.getGroupId())){
			 GroupInfoQuery groupInfoQuery=new GroupInfoQuery();
			 groupInfoQuery.setGroupOwnerId(groupInfo.getGroupOwnerId());
			 Integer count=this.groupInfoMapper.selectCount(groupInfoQuery);
			 SysSettingDto sysSettingDto= redisComponent.getSysSetting();
			 if(count>=sysSettingDto.getMaxGroupCount()){
				 throw new BusinessException("最多只能创建"+sysSettingDto.getMaxGroupCount()+"个群聊");
			 }
			 if(null==avatarFile){
				 throw new BusinessException(ResponseCodeEnum.CODE_600);
			 }
			 groupInfo.setCreateTime(curDate);
			 groupInfo.setGroupId(StringTools.getGroupId());
			 this.groupInfoMapper.insert(groupInfo);
			 //设置群组为联系人
			UserContact userContact=new UserContact();
			userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
			userContact.setContactType(UserContactTypeEnum.GROUP.getType());
			userContact.setContactId(groupInfo.getGroupId());
			userContact.setUserId(groupInfo.getGroupOwnerId());
			userContact.setCreateTime(curDate);
			//TODO 创建会话
			//TODO 发送消息(欢迎)
			this.userContactMapper.insert(userContact);
		 }
		//修改
		else {
			GroupInfo dbInfo=this.groupInfoMapper.selectByGroupId(groupInfo.getGroupId());
			//不是群主
			if(!dbInfo.getGroupOwnerId().equals(groupInfo.getGroupOwnerId())){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			this.groupInfoMapper.updateByGroupId(groupInfo,groupInfo.getGroupId());
			//TODO 更新相关表冗余信息
			//TODO 修改群昵称发送ws消息
		}
		if(null==avatarFile){
			return;
		}
		String baseFolder=appConfig.getProjectFolder()+ Constants.FILE_FOLDER_FILE;
	}

}
