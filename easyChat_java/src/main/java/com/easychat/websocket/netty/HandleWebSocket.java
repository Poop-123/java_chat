package com.easychat.websocket.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandleWebSocket extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger logger= LoggerFactory.getLogger(HandleWebSocket.class);

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

}
