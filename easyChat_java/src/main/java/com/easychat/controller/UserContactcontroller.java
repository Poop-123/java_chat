package com.easychat.controller;

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
import jodd.util.ArraysUtil;
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
import org.springframework.web.bind.annotation.RequestMapping;
import com.easychat.entity.vo.ResponseVO;
/**
  * @Description:联系人 Service
  * @Author:刘耿豪
  * @Date:2025/03/19
  */
@RestController
	@RequestMapping("/contact")
public class UserContactcontroller extends ABaseController{
	@Resource
	private UserContactService userContactService;
	@Resource
	private UserInfoService userInfoService;
	@Resource
	private UserContactApplyService userContactApplyService;
    //搜索
	@GlobalInterceptor
	@PostMapping("/search")
	@Validated
	public ResponseVO search(HttpServletRequest request, @NotEmpty String contactId){
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
		UserContactSearchResultDto userContactSearchResultDto = this.userContactService.searchContact(tokenUserInfoDto.getUserId(), contactId);
		return getSuccessResponseVO(userContactSearchResultDto);
	}
	//添加申请
	@GlobalInterceptor
	@PostMapping("/applyAdd")
	@Validated
	public ResponseVO applyAdd(HttpServletRequest request, @NotEmpty String contactId,String applyInfo) throws BusinessException {
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
		Integer joinType= userContactService.applyAdd(tokenUserInfoDto,contactId,applyInfo);
		return getSuccessResponseVO(joinType);
	}
	//加载申请
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
	//处理申请
	@GlobalInterceptor
	@RequestMapping("/dealWithApply")
	public ResponseVO dealWithApply(HttpServletRequest request,
									@NotEmpty Integer applyId,
									@NotEmpty Integer status ) throws BusinessException {
		TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
		this.userContactApplyService.dealWithApply(tokenUserInfoDto.getUserId(),applyId,status);
		return getSuccessResponseVO(null);
	}
    //加载好友
	@RequestMapping("/loadContact")
	@GlobalInterceptor
	public ResponseVO loadContact(HttpServletRequest request,
								  @NotEmpty String contactType) throws BusinessException {
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
	//显示联系人详情
	@RequestMapping("/getContactInfo")
	@GlobalInterceptor
	public ResponseVO getContactInfo(HttpServletRequest request,@NotEmpty String contactId){
		TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
		UserInfo userInfo=userInfoService.getUserInfoByUserId(contactId);
		UserInfoVO userInfoVO=CopyTools.copy(userInfo,UserInfoVO.class);
		userInfoVO.setContactStatus(UserContactStatusEnum.NOT_FRIEND.getStatus());
		UserContact userContact=userContactService.getUserContactByUserIdAndContactId(tokenUserInfoDto.getUserId(),contactId);
		if(userContact!=null){
			userInfoVO.setContactStatus(UserContactStatusEnum.FRIEND.getStatus());
		}
		return getSuccessResponseVO(userInfoVO);

	}
    //删除好友
	@RequestMapping("/delContact")
	@GlobalInterceptor
	public ResponseVO delContact(HttpServletRequest request,@NotEmpty String contactId) throws BusinessException {
		System.out.println("删除好友");
		TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
		userContactService.removeUserContact(tokenUserInfoDto.getUserId(),contactId,UserContactStatusEnum.DEL);
		return getSuccessResponseVO(null);
	}
	//添加黑名单
	@RequestMapping("/addContact2BlackList")
	@GlobalInterceptor
	public ResponseVO addContact2BlackList(HttpServletRequest request,@NotEmpty String contactId) throws BusinessException {
		TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
		userContactService.removeUserContact(tokenUserInfoDto.getUserId(),contactId,UserContactStatusEnum.BLACKLIST);
		return getSuccessResponseVO(null);
	}
	//得到好友信息
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
