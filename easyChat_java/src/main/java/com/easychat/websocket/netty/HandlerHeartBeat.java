package com.easychat.websocket.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlerHeartBeat extends ChannelDuplexHandler {
    private static final Logger logger= LoggerFactory.getLogger(HandlerHeartBeat.class);
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            //可以拿到相应的参数
            IdleStateEvent e=(IdleStateEvent)evt;
            if(e.state()== IdleState.READER_IDLE){
                logger.info("用户{}心跳超时",ctx.channel().attr(AttributeKey.valueOf(ctx.channel().id().toString())).get());
                ctx.close();
            }else if(e.state()==IdleState.WRITER_IDLE){
                ctx.writeAndFlush("heart");
            }
        }

    }
}
