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
import javax.validation.constraints.NotNull;

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

	@GlobalInterceptor
	@PostMapping("/addOrRemoveGroupUser")
	public ResponseVO addOrRemoveGroupUser(HttpServletRequest request, @NotEmpty String groupId, @NotEmpty String selectContacts, @NotNull Integer opType) throws BusinessException {
		TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
		groupInfoService.addOrRemoveGroupUser(tokenUserInfoDto,groupId,selectContacts,opType);
		return getSuccessResponseVO(null);
	}
	@GlobalInterceptor
	@PostMapping("/leaveGroup")
	public ResponseVO leaveGroup(HttpServletRequest request, @NotEmpty String groupId) throws BusinessException {
		TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
		groupInfoService.leaveGroup(tokenUserInfoDto.getUserId(),groupId,MessageTypeEnum.LEAVE_GROUP);
		return getSuccessResponseVO(null);
	}
	@GlobalInterceptor
	@PostMapping("/dissolutionGroup")
	public ResponseVO dissolutionGroup(HttpServletRequest request, @NotEmpty String groupId) throws BusinessException {
		TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
		groupInfoService.dissolutionGroup(tokenUserInfoDto.getUserId(),groupId);
		return getSuccessResponseVO(null);
	}



}
