package com.easychat.websocket;

import com.easychat.dto.MessageSendDto;
import com.easychat.utils.JsonUtils;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component("messageHandler")
public class MessageHandler {
    private static final Logger logger=LoggerFactory.getLogger(MessageHandler.class);

    private static final String MESSAGE_TOPIC="message.topic";
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private ChannelContextUitls channelContextUitls;
    @PostConstruct
    public void lisMessage(){
        RTopic rTopic=redissonClient.getTopic(MESSAGE_TOPIC);
        rTopic.addListener(MessageSendDto.class,(MessageSendDto,sendDto)->{
            logger.info("收到广播信息：{}", JsonUtils.convertObj2Json(sendDto));
            channelContextUitls.sendMessage(sendDto);
        });
    }
    public void sendMessage(MessageSendDto sendDto){
        RTopic rTopic=redissonClient.getTopic(MESSAGE_TOPIC);
        rTopic.publish(sendDto);
    }
}
