package com.easychat.websocket;

import com.easychat.dto.MessageSendDto;
import com.easychat.dto.WsInitData;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.po.*;
import com.easychat.enums.MessageTypeEnum;
import com.easychat.enums.UserContactApplyStatusEnum;
import com.easychat.enums.UserContactTypeEnum;
import com.easychat.mapper.ChatMessageMapper;
import com.easychat.mapper.UserContactApplyMapper;
import com.easychat.mapper.UserInfoMapper;
import com.easychat.query.*;
import com.easychat.redis.RedisComponent;
import com.easychat.service.ChatSessionUserService;
import com.easychat.utils.JsonUtils;
import com.easychat.utils.StringTools;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ChannelContextUitls {
    private static final ConcurrentHashMap<String,Channel> USER_CONTEXT_MAP=new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String,ChannelGroup> GROUP_CONTEXT_MAP=new ConcurrentHashMap<>();

    private static final Logger logger= LoggerFactory.getLogger(ChannelContextUitls.class);

    @Resource
    private  UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Resource
    private  RedisComponent redisComponent;
    @Resource
    private ChatMessageMapper<ChatMessage,ChatMessageQuery> chatMessageMapper;
   @Resource
   private ChatSessionUserService chatSessionUserService;
   @Resource
   private UserContactApplyMapper<UserContactApply,UserContactApplyQuery> userContactApplyMapper;

    public void addContext(String userId, Channel channel){
          String channelId=channel.id().toString();
        AttributeKey attributeKey=null;
        if(!AttributeKey.exists(channelId)){
            attributeKey=AttributeKey.newInstance(channelId);
        }else{
            attributeKey=AttributeKey.valueOf(channelId);
        }
        channel.attr(attributeKey).set(userId);
        List<String> contactList=redisComponent.getContactList(userId);
        for(String groupId:contactList){
            if(groupId.startsWith(UserContactTypeEnum.GROUP.getPrefix())){
                add2Group(groupId,channel);
            }
        }
        USER_CONTEXT_MAP.put(userId,channel);
        redisComponent.saveUserHeartBeat(userId);
        //更新用户最后链接时间
        UserInfo userInfo=new UserInfo();
        userInfo.setLastLoginTime(new Date());
        userInfoMapper.updateByUserId(userInfo,userId);
        //给用户发送消息
        UserInfo userInfo1=userInfoMapper.selectByUserId(userId);
        Long sourceLastOffTime=userInfo1.getLastOffTime();
        Long lastOffTime=sourceLastOffTime;
        //超过三天
        if(sourceLastOffTime!=null&&System.currentTimeMillis()- Constants.MILLIS_SECONDS_3DAYS_AGO>sourceLastOffTime){
            lastOffTime=System.currentTimeMillis()- Constants.MILLIS_SECONDS_3DAYS_AGO;

        }
        /**
         * 1.查询会话信息，查询用户所有的会话信息 保证换了设备会同步
         */
        ChatSessionUserQuery chatSessionUserQuery=new ChatSessionUserQuery();
        chatSessionUserQuery.setUserId(userId);
        chatSessionUserQuery.setOrderBy("last_receive_time desc");
        List<ChatSessionUser> chatSessionUserList=chatSessionUserService.findListByParam(chatSessionUserQuery);

        WsInitData wsInitData=new WsInitData();
        wsInitData.setChatSessionList(chatSessionUserList);
        /**
         * 2.查询聊天信息
         */
        List<String> groupIdList=contactList.stream().filter(item->item.startsWith(UserContactTypeEnum.GROUP.getPrefix())).collect(Collectors.toList());
        groupIdList.add(userId);
        ChatMessageQuery chatMessageQuery=new ChatMessageQuery();
        chatMessageQuery.setLastReceiveTime(lastOffTime);
        chatMessageQuery.setContactIdList(contactList);
        List<ChatMessage> chatMessageList=chatMessageMapper.selectList(chatMessageQuery);
        wsInitData.setChatMessagesList(new ArrayList<>());

        /**
         * 3查询聊天申请
         */
        UserContactApplyQuery applyQuery=new UserContactApplyQuery();
        applyQuery.setReceiveUserId(userId);
        applyQuery.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
        applyQuery.setLastApplyTimestamp(lastOffTime);
        Integer applyCount=userContactApplyMapper.selectCount(applyQuery);
        wsInitData.setApplyCount(0);
        //发送消息
        MessageSendDto messageSendDto=new MessageSendDto();
        messageSendDto.setMessageType(MessageTypeEnum.INIT.getType());
        messageSendDto.setContactId(userId);
        messageSendDto.setExtendData(wsInitData);
        sendMsg(messageSendDto,userId);





    }

    private static void sendMSG(){

    }
    public void  addUser2Group(String userId,String groupId){
        Channel channelUser= USER_CONTEXT_MAP.get(userId);
        ChannelGroup channelGroup=GROUP_CONTEXT_MAP.get(groupId);
        channelGroup.add(channelUser);
    }
    private void add2Group(String groupId,Channel channel){
        if(channel==null){
            return;
        }
        ChannelGroup group=GROUP_CONTEXT_MAP.get(groupId);
        if(group==null){
            group=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            GROUP_CONTEXT_MAP.put(groupId,group);
        }
        group.add(channel);

    }
    public void removeContext(Channel channel){
        Attribute<String> attribute=channel.attr(AttributeKey.valueOf(channel.id().toString()));
        String userId = attribute.get();
        if(StringTools.isEmpty(userId)){
            return ;
        }
        USER_CONTEXT_MAP.remove(userId);
        redisComponent.removeUserHeartBeat(userId);
        //更新用户最后离线时间
        UserInfo userInfo=new UserInfo();
        userInfo.setLastOffTime(System.currentTimeMillis());
        userInfoMapper.updateByUserId(userInfo,userId);
    }
    public void send2Group(String msg){
        ChannelGroup group=GROUP_CONTEXT_MAP.get("1000");
        group.writeAndFlush(new TextWebSocketFrame(msg));
    }
    public void sendMessage(MessageSendDto messageSendDto){
        UserContactTypeEnum contactTypeEnum=UserContactTypeEnum.getByPrefix(messageSendDto.getContactId());
        switch (contactTypeEnum){
            case USER:
                send2User(messageSendDto);
                break;
            case GROUP:
                send2Group(messageSendDto);
                break;
        }

    }
    //发送给用户
    private void send2User(MessageSendDto messageSendDto){
          String contactId=messageSendDto.getContactId();
        if(StringTools.isEmpty(contactId)){
            return ;
        }
          sendMsg(messageSendDto,contactId);
        //强制下线
        if(MessageTypeEnum.FORCE_OFF_LINE.getType().equals(messageSendDto.getMessageType())){
            closeContext(messageSendDto.getSendUserId());
        }
    }
    public void closeContext(String userId){
        if(StringTools.isEmpty(userId)){
            return;
        }
        redisComponent.cleanUserTokenByUserId(userId);
        Channel channel=USER_CONTEXT_MAP.get(userId);
        if(channel==null){
            return ;
        }
        channel.close();


    }
    //发送给群组
    private void send2Group(MessageSendDto messageSendDto){
       if(StringTools.isEmpty(messageSendDto.getContactId())){
           return ;
       }
       ChannelGroup channelGroup=GROUP_CONTEXT_MAP.get(messageSendDto.getContactId());
       if(channelGroup==null){
           return ;
       }
       channelGroup.writeAndFlush(new TextWebSocketFrame(JsonUtils.convertObj2Json(messageSendDto.getMessageContent())));
    }
    //发送消息
    public static void sendMsg(MessageSendDto messageSendDto,String receiveId){

        Channel userChannel=USER_CONTEXT_MAP.get(receiveId);
        if(userChannel==null){
            return ;
        }
        //相对于客户端而言，联系人就是发送人，转换再发送
        if(MessageTypeEnum.ADD_FRIEND_SELF.getType().equals(messageSendDto.getMessageType())){
            UserInfo userInfo=(UserInfo) messageSendDto.getExtendData();
            messageSendDto.setMessageType(MessageTypeEnum.ADD_FRIEND.getType());
            messageSendDto.setContactId(userInfo.getUserId());
            messageSendDto.setContactName(userInfo.getNickName());
            messageSendDto.setExtendData(null);
        }else{
            messageSendDto.setContactId(messageSendDto.getSendUserId());
            messageSendDto.setContactName(messageSendDto.getSendUserNickName());

        }
       userChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.convertObj2Json(messageSendDto)));

    }
}
