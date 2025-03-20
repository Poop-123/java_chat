package com.easychat.entity.constants;

import com.easychat.enums.UserContactTypeEnum;

public class Constants {
    public static final Integer  LENGTH_11=11;
    public static final Integer  LENGTH_20=20;
    public static final String REDIS_KEY_CHECK_CODE="easychat:checkCode:";
    public static final Integer REDIS_TIME_ONE_MINUTE=60;
    public static final String REDIS_KEY_WS_USER_HEART_BEAT="easychat:ws:user:heartbeat:";
    public static final String REDIS_KEY_WS_TOKEN="easychat:ws:token:";
    public static final String REDIS_KEY_WS_TOKEN_USERID="easychat:ws:token:userid:";
    public static final Integer REDIS_KEY_EXPRESS_DAY=REDIS_TIME_ONE_MINUTE*60*24;
    public static final String REDIS_KEY_SYS_SETTING="easychat:syssetting:";


    public static final String ROBOT_UID= UserContactTypeEnum.USER.getPrefix()+"robot";


    public static final String FILE_FOLDER_FILE="/file/";


}
