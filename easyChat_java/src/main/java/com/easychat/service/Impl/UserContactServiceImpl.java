package com.easychat.service.Impl;

import java.util.Date;

import com.easychat.dto.TokenUserInfoDto;
import com.easychat.dto.UserContactSearchResultDto;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.po.UserContactApply;
import com.easychat.entity.po.UserInfo;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.mapper.GroupInfoMapper;
import com.easychat.mapper.UserContactApplyMapper;
import com.easychat.mapper.UserInfoMapper;
import com.easychat.query.*;
import com.easychat.service.UserContactApplyService;
import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.catalina.User;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForYear;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import com.easychat.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.UserContactService;
import com.easychat.mapper.UserContactMapper;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
  * @Description:联系人 Service
  * @Author:刘耿豪
  * @Date:2025/03/19
  */
@Service
public class UserContactServiceImpl implements UserContactService{

	@Resource
	private UserContactMapper<UserContact,UserContactQuery> userContactMapper;
	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
	@Resource
	private GroupInfoMapper<GroupInfo, GroupInfoQuery> groupInfoMapper;
    @Resource
	private UserContactApplyMapper<UserContactApply, UserContactApplyQuery> userContactApplyMapper;
	@Resource
	private UserContactApplyService userContactApplyService;
	/**
	 * 根据条件查询列表
	 */
	public List<UserContact> findListByParam(UserContactQuery query){
		return this.userContactMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserContactQuery query){
		return this.userContactMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserContact> findListByPage(UserContactQuery query){
	Integer count=this.findCountByParam(query);
	Integer pageSize =query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
	SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
	query.setSimplePage(page);
	List<UserContact> list=this.findListByParam(query);
		return new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),list);
	}

	/**
	 * 新增
	 */
	public Integer add(UserContact bean){
		return this.userContactMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserContact> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.userContactMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	public Integer addOrUpdateBatch(List<UserContact> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.userContactMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据UserIdAndContactId查询对象
	 */
	public UserContact getUserContactByUserIdAndContactId(String userId,String contactId){
		return this.userContactMapper.selectByUserIdAndContactId(userId,contactId);
	}

	/**
	 * 根据UserIdAndContactId修改
	 */
	public Integer updateUserContactByUserIdAndContactId(UserContact bean,String userId,String contactId){
		return this.userContactMapper.updateByUserIdAndContactId(bean,userId,contactId);
	}

	/**
	 * 根据UserIdAndContactId删除
	 */
	public Integer deleteUserContactByUserIdAndContactId(String userId,String contactId){
		return this.userContactMapper.deleteByUserIdAndContactId(userId,contactId);
	}

	@Override
	public UserContactSearchResultDto searchContact(String userId, String contactId) {
		UserContactTypeEnum typeEnum=UserContactTypeEnum.getByPrefix(contactId);
		if(typeEnum==null){
			return null;
		}
		UserContactSearchResultDto resultDto=new UserContactSearchResultDto();
		switch (typeEnum){
			case USER:
				UserInfo userInfo=this.userInfoMapper.selectByUserId(contactId);
				if(userInfo==null){
					return null;
				}
				resultDto=CopyTools.copy(userInfo,UserContactSearchResultDto.class);

				break;
			case GROUP:
				GroupInfo groupInfo=groupInfoMapper.selectByGroupId(contactId);
				if(groupInfo==null){
					return null;
				}
				resultDto.setNickName(groupInfo.getGroupName());
				break;
		}
		resultDto.setContactType(typeEnum.toString());
		resultDto.setContactId(contactId);
		if(userId.equals(contactId)){
			resultDto.setStatus(UserContactStatusEnum.FRIEND.getStatus());
			return resultDto;
		}
		//查询是不是好友
		UserContact userContact=this.userContactMapper.selectByUserIdAndContactId(userId,contactId);
		resultDto.setStatus(userContact==null?null:userContact.getStatus());
		return resultDto;
	}

	@Override
	@Transactional
	public Integer applyAdd(TokenUserInfoDto tokenUserInfoDto, String contactId, String applyInfo) throws BusinessException {
		UserContactTypeEnum typeEnum=UserContactTypeEnum.getByPrefix(contactId);
		if(typeEnum==null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		String applyUserId=tokenUserInfoDto.getUserId();
		applyInfo= StringTools.isEmpty(applyInfo)? String.format(Constants.APPLY_INFO_TEMPLATE,tokenUserInfoDto.getNickName()):applyInfo;
		Long curTime=System.currentTimeMillis();
		Integer joinType=null;
		String receiveUserId=contactId;
		UserContact userContact=userContactMapper.selectByUserIdAndContactId(applyUserId,contactId);
		if(userContact!=null&&
				ArrayUtils.contains(new Integer[]{UserContactStatusEnum.BLACKLIST_BE.getStatus(),UserContactStatusEnum.BLACKLIST_BE_FIRST.getStatus()},userContact.getStatus())
				){
			throw new BusinessException("对方已将你拉黑！");
		}
		if(UserContactTypeEnum.GROUP==typeEnum){
			GroupInfo groupInfo=groupInfoMapper.selectByGroupId(contactId);
			if(groupInfo==null||GroupStatusEnum.DISSOLUTION.getStatus().equals(groupInfo.getStatus())){
				throw new BusinessException("群聊不存在或已解散！");
			}
			receiveUserId=groupInfo.getGroupOwnerId();
			joinType=groupInfo.getJoinType();

		}
		else {
			UserInfo userInfo=userInfoMapper.selectByUserId(contactId);
			if(userInfo==null){
				throw new BusinessException("用户不存在！");
			}
			joinType=userInfo.getJoinType();
		}
		if(JoinTypeEnum.JOIN.getType().equals(joinType)){
             userContactApplyService.addContact(applyUserId,receiveUserId,contactId,typeEnum.getType(),applyInfo);
			return joinType;
		}
        UserContactApply dbApply=this.userContactApplyMapper.selectByApplyUserIdAndReceiveUserIdAndContactId(applyUserId,receiveUserId,contactId);
		if(dbApply==null){
			UserContactApply contactApply=new UserContactApply();
			contactApply.setApplyUserId(applyUserId);
			contactApply.setReceiveUserId(receiveUserId);
			contactApply.setContactId(contactId);
			contactApply.setLastApplyTime(curTime);
			contactApply.setContactType(typeEnum.getType());
			contactApply.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
			contactApply.setApplyInfo(applyInfo);
			this.userContactApplyMapper.insert(contactApply);
		}
		else{
			UserContactApply contactApply=new UserContactApply();
			contactApply.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
			contactApply.setLastApplyTime(curTime);
			contactApply.setApplyInfo(applyInfo);
			contactApply.setApplyId(dbApply.getApplyId());
			System.out.println(111);
			this.userContactApplyMapper.updateByApplyId(contactApply,dbApply.getApplyId());
		}
		if(dbApply==null||!UserContactApplyStatusEnum.INIT.getStatus().equals(dbApply.getStatus())){
			//TODO 发送ws给用户
		}

		return joinType;
	}

	@Override
	public void removeUserContact(String userId, String contactId, UserContactStatusEnum statusEnum) {
        UserContact userContact=new UserContact();
		userContact.setStatus(statusEnum.getStatus());
		userContactMapper.updateByUserIdAndContactId(userContact,userId,contactId);
		UserContact friendContact=new UserContact();
		if(UserContactStatusEnum.DEL==statusEnum){
			friendContact.setStatus(UserContactStatusEnum.DEL_BE.getStatus());
		}
		else if(UserContactStatusEnum.BLACKLIST==statusEnum){
			friendContact.setStatus(UserContactStatusEnum.BLACKLIST_BE.getStatus());
		}
		userContactMapper.updateByUserIdAndContactId(friendContact,contactId,userId);
		//TODO 从我的好友列表缓存中删除好友
		//TODO 从好友列表缓存中删除我
	}


}
