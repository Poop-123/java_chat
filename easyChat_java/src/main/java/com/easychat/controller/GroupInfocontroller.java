package com.easychat.controller;

import java.io.IOException;
import java.util.Date;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.dto.TokenUserInfoDto;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.vo.GroupInfoVO;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.mapper.UserContactMapper;
import com.easychat.query.UserContactQuery;
import com.easychat.redis.RedisComponent;
import com.easychat.service.UserContactService;
import com.fasterxml.jackson.annotation.JsonFormat;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import com.easychat.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.easychat.entity.po.GroupInfo;
import com.easychat.query.GroupInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.easychat.service.GroupInfoService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;

import com.easychat.query.SimplePage;
import com.easychat.entity.vo.ResponseVO;
import org.springframework.web.multipart.MultipartFile;

/**
  * @Description: Service
  * @Author:刘耿豪
  * @Date:2025/03/19
  */
@RestController("groupInfoController")
	@RequestMapping("/group")
public class GroupInfocontroller extends ABaseController{
	@Resource
	private GroupInfoService groupInfoService;
    @Autowired
    private RedisComponent redisComponent;
    @Autowired
    private UserContactService userContactService;

	@GlobalInterceptor
	@PostMapping("/saveGroup")
	public ResponseVO saveGroup(HttpServletRequest request,String groupId,
								@NotEmpty String groupName,
								String groupNotice,
								@NotEmpty Integer joinType,
								MultipartFile avatarFile,
								MultipartFile avatarCover) throws BusinessException, IOException {
		TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
        GroupInfo groupInfo=new GroupInfo();
		groupInfo.setGroupId(groupId);
		groupInfo.setGroupOwnerId(tokenUserInfoDto.getUserId());
		groupInfo.setGroupName(groupName);
		groupInfo.setGroupNotice(groupNotice);
		groupInfo.setJoinType(joinType);
		this.groupInfoService.saveGroup(groupInfo,avatarFile,avatarCover);
		return getSuccessResponseVO(null);
	}
	@GlobalInterceptor
	@GetMapping("/loadMyGroup")
	public ResponseVO  loadMyGroup(HttpServletRequest request) throws IOException{
		TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
		GroupInfoQuery groupInfoQuery=new GroupInfoQuery();
		groupInfoQuery.setGroupOwnerId(tokenUserInfoDto.getUserId());
		groupInfoQuery.setOrderBy("create_time desc");
		List<GroupInfo> groupInfoList=this.groupInfoService.findListByParam(groupInfoQuery);
		return getSuccessResponseVO(groupInfoList);

	}
	@GlobalInterceptor
	@PostMapping("/getGroupInfo")
	public ResponseVO getGroupInfo(HttpServletRequest request,@NotEmpty String groupId) throws BusinessException {
		GroupInfo groupInfo = getGroupDetailCommon(request, groupId);
		UserContactQuery userContactQuery=new UserContactQuery();
		userContactQuery.setContactId(groupId);
		Integer memberCount=this.userContactService.findCountByParam(userContactQuery);
        groupInfo.setMemberCount(memberCount);
		return getSuccessResponseVO(groupInfo);
	}
	private GroupInfo getGroupDetailCommon(HttpServletRequest request,String groupId) throws BusinessException {
		TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
		UserContact userContact=this.userContactService.getUserContactByUserIdAndContactId(tokenUserInfoDto.getUserId(),groupId);
		System.out.println(UserContactStatusEnum.FRIEND.getStatus().equals(userContact.getStatus()));
		if(null==userContact|| ! UserContactStatusEnum.FRIEND.getStatus().equals(userContact.getStatus())){
			throw new BusinessException("你不在群聊中或群聊不存在！");
		}
		GroupInfo groupInfo=this.groupInfoService.getGroupInfoByGroupId(groupId);
		if(null==groupInfo|| !GroupStatusEnum.NORMAL.getStatus().equals(groupInfo.getStatus())){
			throw new BusinessException("群聊不存在！");
		}
		return groupInfo;
	}

	@GlobalInterceptor
	@PostMapping("/getGroupInfo4Chat")
	public ResponseVO getGroupInfo4Chat(HttpServletRequest request,@NotEmpty String groupId) throws BusinessException {
		GroupInfo groupInfo = getGroupDetailCommon(request, groupId);
        UserContactQuery userContactQuery=new UserContactQuery();
		userContactQuery.setContactId(groupId);
		userContactQuery.setQueryUserInfo(true);
		userContactQuery.setOrderBy("create_time desc");
		userContactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus());
		List<UserContact> userContacts=this.userContactService.findListByParam(userContactQuery);
		GroupInfoVO groupInfoVO=new GroupInfoVO();
		groupInfoVO.setGroupInfo(groupInfo);
		groupInfoVO.setUserContactList(userContacts);
		return getSuccessResponseVO(groupInfo);
	}

//	@GlobalInterceptor
//	@PostMapping("/leaveGroup")
//	@Transactional
//	public ResponseVO leaveGroup(HttpServletRequest request,@NotEmpty String groupId) throws BusinessException {
//		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
//		UserContact userContact=this.userContactService.getUserContactByUserIdAndContactId(tokenUserInfoDto.getUserId(),groupId);
//		if(null==userContact||!UserContactStatusEnum.FRIEND.equals(userContact.getStatus())){
//			throw new BusinessException("你已不在群聊中！");
//		}
//		GroupInfo groupInfo=this.groupInfoService.getGroupInfoByGroupId(groupId);
//		if(null==groupInfo||!GroupStatusEnum.NORMAL.equals(groupInfo.getStatus())){
//			throw new BusinessException("群聊状态不正常！");
//		}
//		userContact.setStatus(UserContactStatusEnum.DEL.getStatus());
//		this.userContactService.updateUserContactByUserIdAndContactId(userContact,tokenUserInfoDto.getUserId(),groupId);
//		this.groupInfoService.deleteGroupInfoByGroupId(groupId);
//		return getSuccessResponseVO(null);
//	}
//	@PostMapping("/dissolution")
//	@GlobalInterceptor
//	public ResponseVO dissolution(HttpServletRequest request,@NotEmpty String groupId) throws BusinessException {
//		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
//		GroupInfo groupInfo=this.groupInfoService.getGroupInfoByGroupId(groupId);
//		if(null==groupInfo){
//			throw new BusinessException("群聊不存在！");
//		}
//		if(!tokenUserInfoDto.getUserId().equals(groupInfo.getGroupOwnerId())){
//			throw new BusinessException("无权解散！");
//		}
//		this.userContactService.updateUserContactByContactId(groupId);
//		groupInfo.setStatus(GroupStatusEnum.DISSOLUTION.getStatus());
//		this.groupInfoService.updateGroupInfoByGroupId(groupInfo,groupId);
//		return getSuccessResponseVO(null);
//	}


}
