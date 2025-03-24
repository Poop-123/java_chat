package com.easychat.websocket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyWebSocketStarter {
    private static EventLoopGroup bossGroup =new NioEventLoopGroup();
    private static EventLoopGroup workGroup =new NioEventLoopGroup();
    public static void main(String[] args){
        ServerBootstrap serverBootstrap=new ServerBootstrap();
        serverBootstrap.group(bossGroup,workGroup);
        serverBootstrap.channel(NioSctpServerChannel.class).handler(new LoggingHandler(LogLevel.DEBUG));
    }
}
