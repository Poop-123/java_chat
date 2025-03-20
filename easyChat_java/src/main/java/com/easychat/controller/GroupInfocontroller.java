package com.easychat.controller;

import java.util.Date;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.dto.TokenUserInfoDto;
import com.easychat.redis.RedisComponent;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import com.easychat.enums.DateTimePatternEnum;
import com.easychat.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.easychat.entity.po.GroupInfo;
import com.easychat.query.GroupInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.easychat.service.GroupInfoService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;

import com.easychat.enums.PageSize;
import com.easychat.query.SimplePage;
import org.springframework.web.bind.annotation.RequestMapping;
import com.easychat.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestBody;
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

	@GlobalInterceptor
	@PostMapping("/saveGroup")
	public ResponseVO saveGroup(HttpServletRequest request,String groupId,
								@NotEmpty String groupName,
								String groupNotice,
								@NotEmpty Integer joinType,
								MultipartFile avatarFile,
								MultipartFile avatarCover){
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

}
