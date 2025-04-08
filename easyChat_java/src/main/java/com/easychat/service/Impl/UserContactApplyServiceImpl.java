package com.easychat.service.Impl;

import com.easychat.dto.MessageSendDto;
import com.easychat.dto.SysSettingDto;
import com.easychat.entity.po.*;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.mapper.*;
import com.easychat.query.*;
import com.easychat.redis.RedisComponent;
import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import com.easychat.websocket.ChannelContextUitls;
import com.easychat.websocket.MessageHandler;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.UserContactApplyService;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
  * @Description:用户联系人表 Service
  * @Author:刘耿豪
  * @Date:2025/03/22
  */
@Service
public class UserContactApplyServiceImpl implements UserContactApplyService{

	@Resource
	private UserContactApplyMapper<UserContactApply,UserContactApplyQuery> userContactApplyMapper;
    @Autowired
    private UserContactMapper<UserContact, UserContactQuery> userContactMapper;
    @Autowired
    private RedisComponent redisComponent;
	@Resource
	private ChatSessionMapper<ChatSession, ChatSessionQuery> chatSessionMapper;
    @Autowired
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Resource
	private ChatSessionUserMapper chatSessionUserMapper;
	@Resource
	private  ChatMessageMapper chatMessageMapper;
    @Autowired
    private MessageHandler messageHandler;
    @Resource
	private GroupInfoMapper<GroupInfo,GroupInfoQuery> groupInfoMapper;
    @Autowired
    private ChannelContextUitls channelContextUitls;

	/**
	 * 根据条件查询列表
	 */
	public List<UserContactApply> findListByParam(UserContactApplyQuery query){
		return this.userContactApplyMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserContactApplyQuery query){
		return this.userContactApplyMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserContactApply> findListByPage(UserContactApplyQuery query){
	Integer count=this.findCountByParam(query);
	Integer pageSize =query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
	SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
	query.setSimplePage(page);
	List<UserContactApply> list=this.findListByParam(query);
		return new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),list);
	}

	/**
	 * 新增
	 */
	public Integer add(UserContactApply bean){
		return this.userContactApplyMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserContactApply> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.userContactApplyMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	public Integer addOrUpdateBatch(List<UserContactApply> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.userContactApplyMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据ApplyId查询对象
	 */
	public UserContactApply getUserContactApplyByApplyId(Integer applyId){
		return this.userContactApplyMapper.selectByApplyId(applyId);
	}

	/**
	 * 根据ApplyId修改
	 */
	public Integer updateUserContactApplyByApplyId(UserContactApply bean,Integer applyId){
		return this.userContactApplyMapper.updateByApplyId(bean,applyId);
	}

	/**
	 * 根据ApplyId删除
	 */
	public Integer deleteUserContactApplyByApplyId(Integer applyId){
		return this.userContactApplyMapper.deleteByApplyId(applyId);
	}

	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId查询对象
	 */
	public UserContactApply getUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId,String receiveUserId,String contactId){
		return this.userContactApplyMapper.selectByApplyUserIdAndReceiveUserIdAndContactId(applyUserId,receiveUserId,contactId);
	}

	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId修改
	 */
	public Integer updateUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(UserContactApply bean,String applyUserId,String receiveUserId,String contactId){
		return this.userContactApplyMapper.updateByApplyUserIdAndReceiveUserIdAndContactId(bean,applyUserId,receiveUserId,contactId);
	}

	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId删除
	 */
	public Integer deleteUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId,String receiveUserId,String contactId){
		return this.userContactApplyMapper.deleteByApplyUserIdAndReceiveUserIdAndContactId(applyUserId,receiveUserId,contactId);
	}
	@Transactional
	@Override
	public void dealWithApply(String userId, Integer applyId, Integer status) throws BusinessException {
		UserContactApplyStatusEnum statusEnum=UserContactApplyStatusEnum.getByStatus(status);
		if(null==statusEnum||UserContactApplyStatusEnum.INIT==statusEnum){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		UserContactApply applyInfo=this.userContactApplyMapper.selectByApplyId(applyId);
		if(applyInfo==null||!userId.equals(applyInfo.getReceiveUserId())){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		UserContactApply updateInfo=new UserContactApply();
		updateInfo.setStatus(statusEnum.getStatus());
		updateInfo.setLastApplyTime(System.currentTimeMillis());
		UserContactApplyQuery userContactApplyQuery=new UserContactApplyQuery();
		userContactApplyQuery.setApplyId(applyId);
		userContactApplyQuery.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
		Integer count =userContactApplyMapper.updateByParam(updateInfo,userContactApplyQuery);
		if(count==0){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		//同意
        if(UserContactApplyStatusEnum.PASS.getStatus().equals(status)){

			addContact(applyInfo.getApplyUserId(), applyInfo.getReceiveUserId(), applyInfo.getContactId(),applyInfo.getContactType(),applyInfo.getApplyInfo());
			return ;
		}
		//拉黑
		if(UserContactApplyStatusEnum.BLACKLIST.getStatus().equals(status)){
             Date curDate=new Date();
			 UserContact userContact=new UserContact();
			 userContact.setUserId(applyInfo.getContactId());
			 userContact.setContactType(applyInfo.getContactType());
			 userContact.setContactId(applyInfo.getContactId());
			 userContact.setCreateTime(curDate);
			 userContact.setStatus(UserContactStatusEnum.BLACKLIST_BE_FIRST.getStatus());
			 userContact.setLastUpdateTime(curDate);
			 userContactMapper.insertOrUpdate(userContact);
		}
		//拒绝：无视
	}

	@Override
	public void addContact(String applyUserId, String receiveUserId, String contactId, Integer contactType, String applyInfo) throws BusinessException {
         //群聊人数
		if(UserContactTypeEnum.GROUP.equals(contactType)){
			UserContactQuery userContactQuery=new UserContactQuery();
			userContactQuery.setContactId(contactId);
			userContactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus());
			Integer count=userContactMapper.selectCount(userContactQuery);
			SysSettingDto sysSettingDto=redisComponent.getSysSetting();
			if(count>=sysSettingDto.getMaxGroupCount()){
				throw new BusinessException("成员已满，无法加入！");
			}
		}
		Date curDate=new Date();
		//同意双方添加好友
		List<UserContact> contactList=new ArrayList<>();
		//申请人添加对方
		UserContact userContact=new UserContact();
		userContact.setUserId(applyUserId);
		userContact.setContactId(contactId);
		userContact.setContactType(contactType);
		userContact.setCreateTime(curDate);
		userContact.setLastUpdateTime(curDate);
		userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
		contactList.add(userContact);
		//如果是申请好友，接收人添加申请人，群组不用添加对方为好友
		if(UserContactTypeEnum.USER.getType().equals(contactType)){
			userContact=new UserContact();
			userContact.setUserId(receiveUserId);
			userContact.setContactId(applyUserId);
			userContact.setContactType(contactType);
			userContact.setCreateTime(curDate);
			userContact.setLastUpdateTime(curDate);
			userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
			contactList.add(userContact);
		}
		//批量插入
		userContactMapper.insertOrUpdateBatch(contactList);
		if(UserContactTypeEnum.USER.getType().equals(contactType)){
			redisComponent.addUserContact(receiveUserId,applyUserId);
		}
		redisComponent.addUserContact(applyUserId,receiveUserId);


		// 创建会话
		String sessionId=null;
		if(UserContactTypeEnum.USER.getType().equals(contactType)){
		   sessionId= StringTools.getChatSessionId4User(new String[]{applyUserId,receiveUserId});
		}else{
			sessionId= StringTools.getChatSessionId4Group(contactId);
		}
		List<ChatSessionUser> chatSessionUserList=new ArrayList<>();
		if(UserContactTypeEnum.USER.getType().equals(contactType)){
			//创建会话
			ChatSession chatSession=new ChatSession();
			chatSession.setSessionId(sessionId);
			chatSession.setLastMessage(applyInfo);
			chatSession.setLastReceiveTime(curDate.getTime());
			this.chatSessionMapper.insertOrUpdate(chatSession);
            //申请人session
			ChatSessionUser applySessionUser=new ChatSessionUser();
			applySessionUser.setUserId(applyUserId);
			applySessionUser.setContactId(receiveUserId);
			applySessionUser.setSessionId(sessionId);
			UserInfo contactUser= this.userInfoMapper.selectByUserId(contactId);
			applySessionUser.setContactName(contactUser.getNickName());
			chatSessionUserList.add(applySessionUser);
			//接收人
			ChatSessionUser contactSessionUser=new ChatSessionUser();
			contactSessionUser.setUserId(contactId);
			contactSessionUser.setContactId(applyUserId);
			contactSessionUser.setSessionId(sessionId);
			UserInfo applyUser= this.userInfoMapper.selectByUserId(applyUserId);
			contactSessionUser.setContactName(applyUser.getNickName());
			chatSessionUserList.add(contactSessionUser);
			this.chatSessionUserMapper.insertOrUpdateBatch(chatSessionUserList);
			//记录消息表
			ChatMessage chatMessage=new ChatMessage();
			chatMessage.setSessionId(sessionId);
			chatMessage.setMessageType(MessageTypeEnum.ADD_FRIEND.getType());
			chatMessage.setMessageContent(applyInfo);
			chatMessage.setSendUserId(applyUserId);
			chatMessage.setSendUserNickName(applyUser.getNickName());
			chatMessage.setContactId(contactId);
			chatMessage.setContactType(UserContactTypeEnum.USER.getType());
			chatMessageMapper.insert(chatMessage);
			MessageSendDto messageSendDto= CopyTools.copy(chatMessage,MessageSendDto.class);
			//发送给接受好友申请的人
			messageHandler.sendMessage(messageSendDto);
			//发送给申请人，发送人就是接受人，联系人就是申请人
			messageSendDto.setMessageType(MessageTypeEnum.ADD_FRIEND_SELF.getType());
			messageSendDto.setContactId(applyUserId);
			messageSendDto.setExtendData(contactUser);

		}else{
            //加入群组
			ChatSessionUser chatSessionUser=new ChatSessionUser();
			chatSessionUser.setUserId(applyUserId);
			chatSessionUser.setContactId(contactId);
			GroupInfo groupInfo=groupInfoMapper.selectByGroupId(contactId);
			chatSessionUser.setContactName(groupInfo.getGroupName());
			chatSessionUser.setSessionId(sessionId);
			chatSessionUserMapper.insertOrUpdate(chatSessionUser);
			 UserInfo applyUserInfo=this.userInfoMapper.selectByUserId(applyUserId);
			String sendMessage=String.format(MessageTypeEnum.ADD_GROUP.getInitMessage(),applyUserInfo.getNickName());
			ChatSession chatSession=new ChatSession();
			chatSession.setSessionId(sessionId);
			chatSession.setLastReceiveTime(curDate.getTime());
			chatSession.setLastMessage(sendMessage);
			this.chatSessionMapper.insertOrUpdate(chatSession);
			ChatMessage chatMessage=new ChatMessage();
			chatMessage.setSessionId(sessionId);
			chatMessage.setMessageType(MessageTypeEnum.ADD_GROUP.getType());
            chatMessage.setSendTime(curDate.getTime());
			chatMessage.setContactType(UserContactTypeEnum.GROUP.getType());
			chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus());
			chatMessage.setContactId(contactId);
			this.chatMessageMapper.insert(chatMessage);



			redisComponent.addUserContact(applyUserId,groupInfo.getGroupId());
			channelContextUitls.addUser2Group(applyUserId, groupInfo.getGroupId());
            MessageSendDto messageSendDto=CopyTools.copy(chatMessage, MessageSendDto.class);
			messageSendDto.setContactId(contactId);
			UserContactQuery userContactQuery=new UserContactQuery();
			userContactQuery.setContactId(contactId);
			userContactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus());
			Integer memberCount=this.userContactMapper.selectCount(userContactQuery);
			messageSendDto.setMemberCount(memberCount);
			messageSendDto.setContactName(groupInfo.getGroupName());
			messageHandler.sendMessage(messageSendDto);

		}
	}

}
