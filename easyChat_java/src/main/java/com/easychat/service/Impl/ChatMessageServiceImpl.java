package com.easychat.service.Impl;

import com.easychat.config.AppConfig;
import com.easychat.dto.MessageSendDto;
import com.easychat.dto.SysSettingDto;
import com.easychat.dto.TokenUserInfoDto;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.po.ChatSession;
import com.easychat.entity.po.ChatSessionUser;
import com.easychat.entity.po.UserContact;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.mapper.ChatSessionMapper;
import com.easychat.mapper.ChatSessionUserMapper;
import com.easychat.mapper.UserContactMapper;
import com.easychat.query.*;
import com.easychat.redis.RedisComponent;
import com.easychat.utils.CopyTools;
import com.easychat.utils.DateUtils;
import com.easychat.utils.StringTools;
import com.easychat.websocket.MessageHandler;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import com.easychat.entity.po.ChatMessage;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.ChatMessageService;
import com.easychat.mapper.ChatMessageMapper;
import javax.annotation.Resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
  * @Description:聊天消息表 Service
  * @Author:刘耿豪
  * @Date:2025/03/25
  */
@Service
public class ChatMessageServiceImpl implements ChatMessageService{
     private static final Logger logger= LoggerFactory.getLogger(ChatMessageServiceImpl.class);
	@Resource
	private ChatMessageMapper<ChatMessage,ChatMessageQuery> chatMessageMapper;
    @Autowired
    private RedisComponent redisComponent;
	@Resource
	private ChatSessionMapper<ChatSession, ChatSessionQuery> chatSessionMapper;
    @Resource
	private MessageHandler messageHandler;
    @Autowired
    private AppConfig appConfig;
	@Resource
	private UserContactMapper<UserContact,UserContactQuery> userContactMapper;


	/**
	 * 根据条件查询列表
	 */
	public List<ChatMessage> findListByParam(ChatMessageQuery query){
		return this.chatMessageMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(ChatMessageQuery query){
		return this.chatMessageMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<ChatMessage> findListByPage(ChatMessageQuery query){
	Integer count=this.findCountByParam(query);
	Integer pageSize =query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
	SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
	query.setSimplePage(page);
	List<ChatMessage> list=this.findListByParam(query);
		return new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),list);
	}

	/**
	 * 新增
	 */
	public Integer add(ChatMessage bean){
		return this.chatMessageMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<ChatMessage> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.chatMessageMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	public Integer addOrUpdateBatch(List<ChatMessage> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.chatMessageMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据MessageId查询对象
	 */
	public ChatMessage getChatMessageByMessageId(Long messageId){
		return this.chatMessageMapper.selectByMessageId(messageId);
	}

	/**
	 * 根据MessageId修改
	 */
	public Integer updateChatMessageByMessageId(ChatMessage bean,Long messageId){
		return this.chatMessageMapper.updateByMessageId(bean,messageId);
	}

	/**
	 * 根据MessageId删除
	 */
	public Integer deleteChatMessageByMessageId(Long messageId){
		return this.chatMessageMapper.deleteByMessageId(messageId);
	}

	@Override
	public MessageSendDto saveMessage(ChatMessage chatMessage, TokenUserInfoDto tokenUserInfoDto) throws BusinessException{
		//不是机器人回复，判断好友状态
		if(!Constants.ROBOT_UID.equals(tokenUserInfoDto.getUserId())){
			List<String> contactList=redisComponent.getContactList(tokenUserInfoDto.getUserId());
			if(!contactList.contains(chatMessage.getContactId())){
				UserContactTypeEnum userContactTypeEnum=UserContactTypeEnum.getByPrefix(chatMessage.getContactId());
				if(UserContactTypeEnum.USER==userContactTypeEnum){
					throw new BusinessException(ResponseCodeEnum.CODE_902);
				}else{
					throw new BusinessException(ResponseCodeEnum.CODE_903);
				}
			}
		}
		String sessionId=null;

		String sendUserId=tokenUserInfoDto.getUserId();
		String contactId=chatMessage.getContactId();
        Long curTime=System.currentTimeMillis();
		chatMessage.setSendTime(curTime);
		UserContactTypeEnum contactTypeEnum=UserContactTypeEnum.getByPrefix(contactId);
		if(UserContactTypeEnum.USER==contactTypeEnum){
			sessionId= StringTools.getChatSessionId4User(new String[]{sendUserId,contactId});
		}else{
			sessionId=StringTools.getChatSessionId4Group(contactId);
		}
		chatMessage.setSessionId(sessionId);
		MessageTypeEnum messageTypeEnum=MessageTypeEnum.getByType(chatMessage.getMessageType());
		if(null==messageTypeEnum|| !ArrayUtils.contains(new Integer[]{MessageTypeEnum.CHAT.getType(),MessageTypeEnum.MEDIA_CHAT.getType()},chatMessage.getMessageType())){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
        Integer status=MessageTypeEnum.MEDIA_CHAT==messageTypeEnum? MessageStatusEnum.SENDING.getStatus() : MessageStatusEnum.SENDED.getStatus();
		chatMessage.setStatus(status);
		String messageContent=StringTools.cleanHtmlTag(chatMessage.getMessageContent());
		ChatSession chatSession=new ChatSession();
		chatSession.setLastMessage(messageContent);
		if(UserContactTypeEnum.GROUP==contactTypeEnum){
			chatSession.setLastMessage(tokenUserInfoDto.getNickName()+":"+messageContent);
		}
		chatSession.setLastReceiveTime(curTime);
		chatSessionMapper.updateBySessionId(chatSession,sessionId);
		chatMessage.setSendUserId(sendUserId);
		chatMessage.setSendUserNickName(tokenUserInfoDto.getNickName());
		chatMessage.setContactType(contactTypeEnum.getType());
		chatMessageMapper.insert(chatMessage);
		MessageSendDto messageSendDto= CopyTools.copy(chatMessage,MessageSendDto.class);
       if(Constants.ROBOT_UID.equals(contactId)){
		   SysSettingDto sysSettingDto=new SysSettingDto();
		   TokenUserInfoDto robot=new TokenUserInfoDto();
		   robot.setUserId(sysSettingDto.getRobotUid());
		   robot.setNickName(sysSettingDto.getRobotNickName());
		   ChatMessage robotChatMessage=new ChatMessage();
		   robotChatMessage.setContactId(sendUserId);
		   //TODO对接ai实现聊天
		   robotChatMessage.setMessageContent("我只是一个机器人，无法识别你的消息");
		   robotChatMessage.setMessageType(MessageTypeEnum.CHAT.getType());
		   saveMessage(robotChatMessage,robot);

	   }else{
		   messageHandler.sendMessage(messageSendDto);

	   }

		return messageSendDto;
	}

	@Override
	public void saveMessageFile(String userId, Long messageId, MultipartFile file, MultipartFile cover) throws BusinessException {
        ChatMessage chatMessage=chatMessageMapper.selectByMessageId(messageId);
		if(chatMessage==null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if(!chatMessage.getSendUserId().equals(userId)){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		SysSettingDto sysSettingDto=redisComponent.getSysSetting();
		String fieldSuffix=StringTools.getFileSuffix(file.getOriginalFilename());
		if(!StringTools.isEmpty(fieldSuffix)&&ArrayUtils.contains(Constants.IMAGE_SUFFIX_LIST,fieldSuffix.toLowerCase())&&file.getSize()>sysSettingDto.getMaxImageSize()*Constants.FILE_SIZE_MB){

			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}else if(!StringTools.isEmpty(fieldSuffix)&&ArrayUtils.contains(Constants.VIDEO_SUFFIX_LIST,fieldSuffix.toLowerCase())&&file.getSize()>sysSettingDto.getMaxVideoSize()*Constants.FILE_SIZE_MB){

			throw new BusinessException(ResponseCodeEnum.CODE_600);

		}
		else {
			if (!StringTools.isEmpty(fieldSuffix) && !ArrayUtils.contains(Constants.IMAGE_SUFFIX_LIST, fieldSuffix.toLowerCase()) && !ArrayUtils.contains(Constants.VIDEO_SUFFIX_LIST, fieldSuffix.toLowerCase()) &&file.getSize()>sysSettingDto.getMaxFileSize()*Constants.FILE_SIZE_MB) {


				throw new BusinessException(ResponseCodeEnum.CODE_600);

			}
		}
		String fileName=file.getOriginalFilename();
		String fileExtName=StringTools.getFileSuffix(fileName);
		String fileRealName=messageId+fileExtName;
		String month= DateUtils.format(new Date(chatMessage.getSendTime()),DateTimePatternEnum.YYYYMM.getPattern());
		File folder=new File(appConfig.getProjectFolder()+Constants.FILE_FOLDER_FILE+month);
		if(!folder.exists()){
			folder.mkdirs();
		}
		File uploadFile=new File(folder.getPath()+"/"+fileRealName);
		try{
			file.transferTo(uploadFile);
			if(cover!=null){
				cover.transferTo(new File(uploadFile.getPath()+Constants.COVER_IMAGE_SUFFIX));

			}
		} catch (IOException e) {
            logger.error("上传文件失败",e);
			throw new BusinessException("文件上传失败");
        }
		ChatMessage uploadInfo=new ChatMessage();
		uploadInfo.setStatus(MessageStatusEnum.SENDED.getStatus());
        ChatMessageQuery messageQuery=new ChatMessageQuery();
		messageQuery.setMessageId(messageId);
		messageQuery.setStatus(MessageStatusEnum.SENDING.getStatus());
		chatMessageMapper.updateByParam(uploadInfo,messageQuery);
        MessageSendDto messageSendDto=new MessageSendDto();
		messageSendDto.setStatus(MessageStatusEnum.SENDED.getStatus());
		messageSendDto.setMessageId(messageId);
		messageSendDto.setMessageType(MessageTypeEnum.FILE_UPLOAD.getType());
		messageSendDto.setContactId(chatMessage.getContactId());
		messageSendDto.setMessageContent("上传成功");
		messageHandler.sendMessage(messageSendDto);


    }

	@Override
	public File downloadFile(TokenUserInfoDto userInfoDto, Long messageId, String showCover) throws BusinessException {
		ChatMessage message=chatMessageMapper.selectByMessageId(messageId);
		String contactId=message.getContactId();
		UserContactTypeEnum contactTypeEnum=UserContactTypeEnum.getByPrefix(contactId);
		if(UserContactTypeEnum.USER==contactTypeEnum&&!userInfoDto.getUserId().equals(message.getContactId())){

			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if(UserContactTypeEnum.GROUP==contactTypeEnum){
			UserContactQuery userContactQuery=new UserContactQuery();
			userContactQuery.setUserId(userInfoDto.getUserId());
			userContactQuery.setContactType(UserContactTypeEnum.GROUP.getType());
			userContactQuery.setContactId(contactId);
			userContactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus());
			Integer contactCount=userContactMapper.selectCount(userContactQuery);
			if(contactCount==0){

				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
		}
		String month=DateUtils.format(new Date(message.getSendTime()),DateTimePatternEnum.YYYYMM.getPattern());
		File folder=new File(appConfig.getProjectFolder()+Constants.FILE_FOLDER_FILE+month);
		if(!folder.exists()){
			folder.mkdirs();
		}
		String fileName=message.getFileName();
		String fileExtName=StringTools.getFileSuffix(fileName);
		String fileRealName=messageId+fileExtName;
		if(showCover!=null&&!showCover.equals("false")){
			fileExtName=fileRealName+Constants.COVER_IMAGE_SUFFIX;
		}
		else{
			fileExtName=fileRealName;
		}
		File file=new File(folder.getPath()+"/"+fileExtName);
		if(!file.exists()){
			logger.info("文件不存在",messageId);
			throw new BusinessException(ResponseCodeEnum.CODE_602);
		}
		return file;
	}
}
