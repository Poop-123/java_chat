package com.easychat.controller;

import com.easychat.entity.constants.Constants;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.exception.BusinessException;
import com.easychat.redis.RedisUtils;
import com.easychat.service.UserInfoService;
import com.wf.captcha.ArithmeticCaptcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController("accountController")
@RequestMapping("/account")
@Validated
public class AccountController extends ABaseController{
    private static final Logger logger= LoggerFactory.getLogger(AccountController.class);
    @Autowired
    private  RedisUtils<String> redisUtils;
    @Autowired
    private UserInfoService userInfoService;
    //发送验证码
    @PostMapping("/checkCode")
    public ResponseVO  checkCode(){
        ArithmeticCaptcha captcha=new ArithmeticCaptcha(100,43);
        String code=captcha.text();
        logger.info("验证码："+code);
        String checkCodeKey= UUID.randomUUID().toString();
        String checkCodeBase64=captcha.toBase64();
        redisUtils.setex(Constants.REDIS_KEY_CHECK_CODE+checkCodeKey,code,Constants.REDIS_TIME_ONE_MINUTE);
        Map<String,String> result=new HashMap<>();
        result.put("checkCode",checkCodeBase64);
        result.put("checkCodeKey",checkCodeKey);
        return getSuccessResponseVO(result);
    }
    //注册
    @PostMapping("/register")
    ResponseVO register(@NotEmpty String checkCodeKey, @NotEmpty @Email  String email, @NotEmpty String password, @NotEmpty String nickName, @NotEmpty String checkCode){
        try{
           if(checkCode.equals(redisUtils.get(Constants.REDIS_KEY_CHECK_CODE+checkCodeKey))){
               throw new BusinessException("图片验证码不正确！");
           }
           userInfoService.register(email,nickName,password);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE+checkCodeKey);

        }
        return getSuccessResponseVO(null);
    }
    @PostMapping("/login")
    ResponseVO login(String email,String nickName,String password){
        return getSuccessResponseVO(null);
    }
    @PostMapping("/getSysSetting")
    ResponseVO getSysSetting(){
        return getSuccessResponseVO(null);
    }


}
