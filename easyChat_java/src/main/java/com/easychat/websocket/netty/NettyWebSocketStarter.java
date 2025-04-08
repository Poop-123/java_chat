package com.easychat.websocket.netty;

import com.easychat.config.AppConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
@ChannelHandler.Sharable
@Component
public class NettyWebSocketStarter implements Runnable{
    private static final Logger logger= LoggerFactory.getLogger(NettyWebSocketStarter.class);
    private static EventLoopGroup bossGroup =new NioEventLoopGroup();
    private static EventLoopGroup workGroup =new NioEventLoopGroup();
    @Resource
    private HandleWebSocket handleWebSocket;
    @Resource
    private AppConfig appConfig;
    @PreDestroy
    public void close(){
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
   @Override
   public void run(){
       try{
           ServerBootstrap serverBootstrap=new ServerBootstrap();
           serverBootstrap.group(bossGroup,workGroup);
           serverBootstrap.channel(NioServerSocketChannel.class)
                   .handler(new LoggingHandler(LogLevel.DEBUG))
                   .childHandler(new ChannelInitializer<Channel>() {
                       @Override
                       protected void initChannel(Channel channel) throws Exception {
                           ChannelPipeline pipeline= channel.pipeline();
                           //设置几个重要的处理
                           //对http协议的支持，使用http的编码器，解码器
                           pipeline.addLast(new HttpServerCodec());
                           //聚合解码 httpRequest/httpContent/lastHttpContent到fullHttpRequest
                           //保证接收的http请求的完整性
                           pipeline.addLast(new HttpObjectAggregator(64*1024));
                           //心跳  long readerIdleTime,long writerIdleTime,long allIdleTime,TimeUnit unit
                           //readerIdleTime 读超时时间  即测试阶段一定时间内未接收到被测试端信息
                           //writerIdleTime 写超时时间 即测试端一定时间内向被测试端发送消息
                           //allIdleTime 所有类型的超时时间
                           //设置心跳规则
                           pipeline.addLast(new IdleStateHandler(60,0,0,TimeUnit.SECONDS));
                           //设置心跳处理
                           pipeline.addLast(new HandlerHeartBeat());
                           //将http协议升级为ws协议，对websocket支持
                           pipeline.addLast(new WebSocketServerProtocolHandler("/ws",null,true,64*1024,true,true,10000L));
                           //自定义处理器处理各种事务
                           pipeline.addLast( handleWebSocket);
                       }
                   });
           ChannelFuture channelFuture= serverBootstrap.bind(appConfig.getWePort()).sync();
           logger.info("netty启动成功,端口：{}",appConfig.getWePort());
           channelFuture.channel().closeFuture().sync();
       }catch (Exception e){
           logger.error("启动netty失败");
           e.printStackTrace();
       }finally {
           bossGroup.shutdownGracefully();
           workGroup.shutdownGracefully();
       }

   }





}
