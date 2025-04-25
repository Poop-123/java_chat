package com.easychat.service.Impl;

import java.util.Date;

import com.easychat.dto.MessageSendDto;
import com.easychat.dto.SysSettingDto;
import com.easychat.dto.TokenUserInfoDto;
import com.easychat.dto.UserContactSearchResultDto;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.po.*;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.mapper.*;
import com.easychat.query.*;
import com.easychat.redis.RedisComponent;
import com.easychat.service.ChatSessionUserService;
import com.easychat.service.UserContactApplyService;
import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import com.easychat.websocket.MessageHandler;
import com.fasterxml.jackson.annotation.JsonFormat;

import org.apache.catalina.User;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForYear;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import com.easychat.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.UserContactService;

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
    @Autowired
    private RedisComponent redisComponent;
	@Resource
	private ChatSessionMapper<ChatSession,ChatSessionQuery> chatSessionMapper;
	@Resource
	private ChatSessionUserMapper<ChatSessionUser,ChatSessionUserQuery> chatSessionUserMapper;
	@Resource
	private ChatMessageMapper<ChatMessage,ChatMessageQuery> chatMessageMapper;
    @Resource
	private MessageHandler messageHandler;
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
		//参数错误
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
			this.userContactApplyMapper.updateByApplyId(contactApply,dbApply.getApplyId());
		}
		if(dbApply==null||!UserContactApplyStatusEnum.INIT.getStatus().equals(dbApply.getStatus())){
			MessageSendDto messageSendDto=new MessageSendDto();
			messageSendDto.setMessageType((MessageTypeEnum.CONTACT_APPLY.getType()));
			messageSendDto.setMessageContent(applyInfo);
			messageSendDto.setContactId(receiveUserId);
			messageHandler.sendMessage(messageSendDto);
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

		redisComponent.removeUserContact(contactId,userId);
		redisComponent.removeUserContact(userId,contactId);

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addContact4Robot(String userId) {
		Date curDate=new Date();
		SysSettingDto sysSettingDto=redisComponent.getSysSetting();
		String contactId= sysSettingDto.getRobotUid();
		String contactName=sysSettingDto.getRobotNickName();
		String sendMessage=sysSettingDto.getRobotWelcome();
		sendMessage=StringTools.cleanHtmlTag(sendMessage);
		//增加机器人好友
		UserContact userContact=new UserContact();
		userContact.setUserId(userId);
		userContact.setContactId(contactId);
		userContact.setContactType(UserContactTypeEnum.USER.getType());
		userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
		userContact.setLastUpdateTime(curDate);
		userContact.setCreateTime(curDate);
		userContactMapper.insert(userContact);
		//增加会话信息
		String sessionId=StringTools.getChatSessionId4User(new String[]{contactId,userId});
		ChatSession chatSession=new ChatSession();
		chatSession.setLastMessage(sendMessage);
		chatSession.setSessionId(sessionId);
		chatSession.setLastReceiveTime(curDate.getTime());
		chatSessionMapper.insert(chatSession);
		//增加会话人信息
		ChatSessionUser chatSessionUser=new ChatSessionUser();
		chatSessionUser.setUserId(userId);
		chatSessionUser.setContactName(contactName);
		chatSessionUser.setContactId(contactId);
		chatSessionUser.setSessionId(sessionId);
		chatSessionUserMapper.insert(chatSessionUser);
		//增加聊天消息
		ChatMessage chatMessage=new ChatMessage();
		chatMessage.setSessionId(sessionId);
		chatMessage.setMessageType(MessageTypeEnum.CHAT.getType());
		chatMessage.setMessageContent(sendMessage);
		chatMessage.setSendUserId(contactId);
		chatMessage.setSendUserNickName(contactName);
		chatMessage.setSendTime(curDate.getTime());
		chatMessage.setContactId(userId);
		chatMessage.setContactType(UserContactTypeEnum.USER.getType());
		chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus());
		chatMessageMapper.insert(chatMessage);
	}
}
