package com.easychat.controller;

import java.util.Date;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.dto.TokenUserInfoDto;
import com.easychat.dto.UserContactSearchResultDto;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.vo.UserInfoVO;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.query.UserContactApplyQuery;
import com.easychat.service.UserContactApplyService;
import com.easychat.service.UserInfoService;
import com.easychat.utils.CopyTools;
import com.fasterxml.jackson.annotation.JsonFormat;
import jodd.util.ArraysUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.format.annotation.DateTimeFormat;
import com.easychat.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.easychat.entity.po.UserContact;
import com.easychat.query.UserContactQuery;
import com.easychat.entity.vo.PaginationResultVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.easychat.service.UserContactService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;

import com.easychat.query.SimplePage;
import org.springframework.web.bind.annotation.RequestMapping;
import com.easychat.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestBody;

/**
  * @Description:联系人 Service
  * @Author:刘耿豪
  * @Date:2025/03/19
  */
@RestController
	@RequestMapping("/userContact")
public class UserContactcontroller extends ABaseController{

	@Resource
	private UserContactService userContactService;
	@Resource
	private UserInfoService userInfoService;
	@Resource
	private UserContactApplyService userContactApplyService;

	@GlobalInterceptor
	@PostMapping("/search")
	@Validated
	public ResponseVO search(HttpServletRequest request, @NotEmpty String contactId){
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
		UserContactSearchResultDto userContactSearchResultDto = this.userContactService.searchContact(tokenUserInfoDto.getUserId(), contactId);
		return getSuccessResponseVO(userContactSearchResultDto);
	}
	@GlobalInterceptor
	@PostMapping("/applyAdd")
	@Validated
	public ResponseVO applyAdd(HttpServletRequest request, @NotEmpty String contactId,String applyInfo) throws BusinessException {
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
		Integer joinType= userContactService.applyAdd(tokenUserInfoDto,contactId,applyInfo);
		return getSuccessResponseVO(joinType);
	}
	@GlobalInterceptor
	@PostMapping("/loadApply")
	public ResponseVO loadApply(HttpServletRequest request,Integer pageNo){
		TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
		UserContactApplyQuery userContactApplyQuery=new UserContactApplyQuery();
		userContactApplyQuery.setOrderBy("last_apply_time desc" );
		userContactApplyQuery.setReceiveUserId(tokenUserInfoDto.getUserId());
		userContactApplyQuery.setPageNo(pageNo);
		userContactApplyQuery.setPageSize(PageSize.SIZE15.getSize());
		userContactApplyQuery.setQueryContactInfo(true);
		PaginationResultVO resultVO=userContactApplyService.findListByPage(userContactApplyQuery);
		return getSuccessResponseVO(resultVO);
	}
	@GlobalInterceptor
	@PostMapping("/dealWithApply")
	public ResponseVO dealWithApply(HttpServletRequest request,@NotEmpty Integer applyId ,@NotEmpty Integer status ) throws BusinessException {
		TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
		this.userContactApplyService.dealWithApply(tokenUserInfoDto.getUserId(),applyId,status);
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/loadContact")
	@GlobalInterceptor
	public ResponseVO loadContact(HttpServletRequest request,@NotEmpty String contactType) throws BusinessException {
		TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
		UserContactTypeEnum contactTypeEnum=UserContactTypeEnum.getByName(contactType);
		if(null==contactTypeEnum){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		UserContactQuery contactQuery=new UserContactQuery();
		contactQuery.setUserId(tokenUserInfoDto.getUserId());
		contactQuery.setContactType(contactTypeEnum.getType());
		if(UserContactTypeEnum.USER==contactTypeEnum){
			contactQuery.setQueryContactUserInfo(true);

		}
		else if(UserContactTypeEnum.GROUP==contactTypeEnum){

			contactQuery.setQueryGroupInfo(true);
			contactQuery.setExcludeMyOwnGroup(true);
		}
        contactQuery.setOrderBy("last_update_time desc");
		contactQuery.setStatusArray(new Integer[]{
				UserContactStatusEnum.FRIEND.getStatus(),
				UserContactStatusEnum.DEL_BE.getStatus(),
				UserContactStatusEnum.BLACKLIST_BE.getStatus(),


		});
		List<UserContact> contactList=userContactService.findListByParam(contactQuery);
		return getSuccessResponseVO(contactList);
	}

	@RequestMapping("/delContact")
	@GlobalInterceptor
	public ResponseVO delContact(HttpServletRequest request,@NotEmpty String contactId) throws BusinessException {
		TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
		userContactService.removeUserContact(tokenUserInfoDto.getUserId(),contactId,UserContactStatusEnum.DEL);
		return getSuccessResponseVO(null);
	}
	@RequestMapping("/addContact2BlackList")
	@GlobalInterceptor
	public ResponseVO addContact2BlackList(HttpServletRequest request,@NotEmpty String contactId) throws BusinessException {
		TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
		userContactService.removeUserContact(tokenUserInfoDto.getUserId(),contactId,UserContactStatusEnum.BLACKLIST);
		return getSuccessResponseVO(null);
	}


	@RequestMapping("/getContactUserInfo")
	@GlobalInterceptor
	public ResponseVO getContactUserInfo(HttpServletRequest request,@NotEmpty String contactId) throws BusinessException {
		TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);

		UserContact userContact= userContactService.getUserContactByUserIdAndContactId(tokenUserInfoDto.getUserId(),contactId);
		if(userContact==null|| !ArraysUtil.contains(new Integer[]{
				UserContactStatusEnum.FRIEND.getStatus(),
				UserContactStatusEnum.DEL_BE.getStatus(),
				UserContactStatusEnum.BLACKLIST_BE.getStatus()

		},userContact.getStatus())){
			System.out.println(userContact==null);

              throw new BusinessException(ResponseCodeEnum.CODE_600);
		}

		UserInfo userInfo=userInfoService.getUserInfoByUserId(contactId);
		UserInfoVO userInfoVO= CopyTools.copy(userInfo, UserInfoVO.class);
		return getSuccessResponseVO(userInfoVO);
	}




}
