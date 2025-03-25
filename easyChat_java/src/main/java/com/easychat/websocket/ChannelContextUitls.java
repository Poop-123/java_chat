package com.easychat.websocket;

import com.easychat.dto.WsInitData;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.po.ChatSessionUser;
import com.easychat.entity.po.UserInfo;
import com.easychat.enums.UserContactTypeEnum;
import com.easychat.mapper.UserInfoMapper;
import com.easychat.query.ChatSessionQuery;
import com.easychat.query.ChatSessionUserQuery;
import com.easychat.query.UserInfoQuery;
import com.easychat.redis.RedisComponent;
import com.easychat.service.ChatSessionUserService;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
   private ChatSessionUserService chatSessionUserService;

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
        wsInitData.setChatSessionUserList(chatSessionUserList);
        /**
         * 2.查询聊天信息
         */
        /**
         * 3查询聊天申请
         */






    }
    private static void sendMSG(){

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
}
