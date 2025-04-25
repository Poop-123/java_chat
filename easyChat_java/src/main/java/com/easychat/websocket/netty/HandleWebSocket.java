package com.easychat.websocket.netty;

import com.easychat.dto.TokenUserInfoDto;
import com.easychat.entity.constants.Constants;
import com.easychat.redis.RedisComponent;
import com.easychat.redis.RedisUtils;
import com.easychat.utils.StringTools;
import com.easychat.websocket.ChannelContextUitls;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
@Component
@ChannelHandler.Sharable
public class HandleWebSocket extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger logger= LoggerFactory.getLogger(HandleWebSocket.class);
    @Resource
    RedisUtils redisUtils;
    @Resource
    ChannelContextUitls channelContextUitls;
    @Resource
    private RedisComponent redisComponent;
    @Override
    //通道就绪调用 一般用来做初始化
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("有新的连接加入。。。");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("有连接断开。。。");
        channelContextUitls.removeContext(ctx.channel());
    }
    @Override
    //接收消息
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        Channel channel=ctx.channel();
        Attribute<String> attribute=channel.attr(AttributeKey.valueOf(channel.id().toString()));
        String userId = attribute.get();
        redisComponent.saveUserHeartBeat(userId);
        //logger.info("收到userId：{}的消息:{}",userId,textWebSocketFrame.text());
        //channelContextUitls.send2Group(textWebSocketFrame.text());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
            WebSocketServerProtocolHandler.HandshakeComplete complete=(WebSocketServerProtocolHandler.HandshakeComplete)evt;
            String url=complete.requestUri();
            String token = getToken(url);
            if(token==null){
                ctx.channel().close();
                return;
            }
            TokenUserInfoDto tokenUserInfoDto=(TokenUserInfoDto)redisUtils.get(Constants.REDIS_KEY_WS_TOKEN+token);
            if(tokenUserInfoDto==null){
                ctx.channel().close();
                return ;
            }
            channelContextUitls.addContext(tokenUserInfoDto.getUserId(),ctx.channel());
        }
    }
    private String getToken(String url){
        if(StringTools.isEmpty(url)|| !url.contains("?")){
            return null;
        }
        String[] queryParams=url.split("\\?");
        if(queryParams.length!=2){
            return null;
        }
        String[] params=queryParams[1].split("=");
        if(params.length!=2){
            return null;
        }
        return params[1];
    }
}
