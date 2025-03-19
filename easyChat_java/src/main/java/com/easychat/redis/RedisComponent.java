package com.easychat.redis;

import com.easychat.dto.SysSettingDto;
import com.easychat.dto.TokenUserInfoDto;
import com.easychat.entity.constants.Constants;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.OnMessage;

@Component("redisComponent")
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;
    //获取心跳
    public long getUserHeartBeat(String userId){
        return (long) redisUtils.get(Constants.REDIS_KEY_WS_USER_HEART_BEAT+userId);
    }
    //存储token和userid->通过userid得到token->得到对象
    public void saveTokenUserInfoDto(TokenUserInfoDto tokenUserInfoDto){
        redisUtils.setex(Constants.REDIS_KEY_WS_TOKEN+tokenUserInfoDto.getToken(),tokenUserInfoDto, Constants.REDIS_KEY_EXPRESS_DAY*2);
        redisUtils.setex(Constants.REDIS_KEY_WS_TOKEN_USERID+tokenUserInfoDto.getUserId(),tokenUserInfoDto.getToken(), Constants.REDIS_KEY_EXPRESS_DAY*2);
    }
    public SysSettingDto getSysSetting(){
        SysSettingDto sysSettingDto=(SysSettingDto) redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        return sysSettingDto==null?new SysSettingDto():sysSettingDto;
    }
}
