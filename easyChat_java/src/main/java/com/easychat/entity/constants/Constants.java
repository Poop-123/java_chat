package com.easychat.entity.constants;

import com.easychat.enums.UserContactTypeEnum;
import com.sun.org.apache.bcel.internal.generic.PUSH;

public class Constants {
    public static final Integer  LENGTH_11=11;
    public static final Integer  LENGTH_20=20;
    public static final String REDIS_KEY_CHECK_CODE="easychat:checkCode:";
    public static final Integer REDIS_TIME_ONE_MINUTE=60;
    public static final String REDIS_KEY_WS_USER_HEART_BEAT="easychat:ws:user:heartbeat:";
    public static final String REDIS_KEY_WS_TOKEN="easychat:ws:token:";
    public static final String REDIS_KEY_WS_TOKEN_USERID="easychat:ws:token:userid:";
    public static final Integer REDIS_KEY_EXPRESS_DAY=REDIS_TIME_ONE_MINUTE*60*24;
    public static final Integer REDIS_KEY_TOKEN_EXPIRES=REDIS_KEY_EXPRESS_DAY*2;
    public static final String REDIS_KEY_SYS_SETTING="easychat:syssetting:";
    public static final Integer REDIS_KEY_EXPIRES_HEART_BEAT=6;


    public static final String ROBOT_UID= UserContactTypeEnum.USER.getPrefix()+"robot";


    public static final String FILE_FOLDER_FILE="file/";
    public static final String FILE_FOLDER_AVATAR_NAME="avatar/";
    public static final String IMAGE_SUFFIX=".png";
    public static final String COVER_IMAGE_SUFFIX="_cover.png";
    public static final String APPLY_INFO_TEMPLATE="我是%s";

    public static final String REGEX_PASSWORD="^(?=.*\\d)(?=.*[a-zA-Z])[\\da-zA-Z~!@#$%^&*_]{8,18}$";

    public static final String APP_UPDATE_FOLDER="/app/";
    public static final String APP_EXE_SUFFIX=".exe";
    public static final String APP_NAME="EasyChatSetup";
    public static final String REDIS_KEY_USER_CONTACT="easychat:ws:user:contact:";
    public static final Long MILLIS_SECONDS_3DAYS_AGO=3*24*60*60*1000L;
}
