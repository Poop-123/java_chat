package com.easychat.websocket.netty;

import com.easychat.entity.constants.Constants;
import com.easychat.redis.RedisComponent;
import com.easychat.redis.RedisUtils;
import com.easychat.utils.StringTools;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
@Component
public class HandleWebSocket extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger logger= LoggerFactory.getLogger(HandleWebSocket.class);
    @Resource
    RedisUtils redisUtils;
    @Override
    //通道就绪调用 一般用来做初始化
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("有新的连接加入。。。");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("有连接断开。。。");
    }
    @Override
    //接收消息
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        Channel channel=ctx.channel();
        logger.info("收到消息{}",textWebSocketFrame.text());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
            WebSocketServerProtocolHandler.HandshakeComplete complete=(WebSocketServerProtocolHandler.HandshakeComplete)evt;
            String url=complete.requestUri();
            String token = getToken(url);
            if(token==null){
                ctx.channel().close();
            }
            if(redisUtils.get(Constants.REDIS_KEY_WS_TOKEN+token)==null){
                logger.info(url);
                ctx.channel().close();
            }
            logger.info(url);
            logger.info(token);
        }
    }
    private String getToken(String url){
        if(StringTools.isEmpty(url)||url.indexOf("?")==-1){
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
