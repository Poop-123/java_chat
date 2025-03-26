package com.easychat.redis;

import com.easychat.dto.SysSettingDto;
import com.easychat.dto.TokenUserInfoDto;
import com.easychat.entity.constants.Constants;
import com.easychat.utils.StringTools;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.OnMessage;
import java.util.List;

@Component("redisComponent")
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;
    //获取心跳
    public long getUserHeartBeat(String userId){
        return (long) redisUtils.get(Constants.REDIS_KEY_WS_USER_HEART_BEAT+userId);
    }
    //存储心跳
    public void saveUserHeartBeat(String userId){
        redisUtils.setex(Constants.REDIS_KEY_WS_USER_HEART_BEAT+userId,System.currentTimeMillis(),Constants.REDIS_KEY_EXPIRES_HEART_BEAT);

    }
    //删除心跳
    public void removeUserHeartBeat(String userId){
        redisUtils.delete(Constants.REDIS_KEY_WS_USER_HEART_BEAT+userId);

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
    public void saveSysSetting(SysSettingDto sysSettingDto){
        redisUtils.set(Constants.REDIS_KEY_SYS_SETTING,sysSettingDto);

    }
    //清空联系人
    public void cleanUserContact(String userId){
        redisUtils.delete(Constants.REDIS_KEY_USER_CONTACT+userId);
    }
    //批量添加联系人
    public void addUserContactBath(String userId, List<String> contactIdList){
        redisUtils.lpushAll(Constants.REDIS_KEY_USER_CONTACT+userId,contactIdList,Constants.REDIS_KEY_TOKEN_EXPIRES);
    }
    //获取联系人
    public List<String> getContactList(String userId){
        return (List<String>)redisUtils.getQueueList(Constants.REDIS_KEY_USER_CONTACT+userId);
    }
    //清空token信息
    public void cleanUserTokenByUserId(String userId){
        String token=(String)redisUtils.get(Constants.REDIS_KEY_WS_TOKEN_USERID+userId);
        if(StringTools.isEmpty(token)){
            return ;
        }
        redisUtils.delete(token);
    }
}
