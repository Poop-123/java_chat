package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.dto.TokenUserInfoDto;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.entity.vo.UserInfoVO;
import com.easychat.exception.BusinessException;
import com.easychat.redis.RedisComponent;
import com.easychat.redis.RedisUtils;
import com.easychat.service.UserInfoService;
import com.easychat.utils.CopyTools;
import com.wf.captcha.ArithmeticCaptcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController("accountController")
@RequestMapping("/account")
@Validated
public class AccountController extends ABaseController{
    private static final Logger logger= LoggerFactory.getLogger(AccountController.class);
    @Resource
    private  RedisUtils<String> redisUtils;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private RedisComponent redisComponent;

    //发送验证码
    @RequestMapping("/checkCode")
    public ResponseVO  checkCode(){
        ArithmeticCaptcha captcha=new ArithmeticCaptcha(100,42);

        String code=captcha.text();
        String checkCodeKey= UUID.randomUUID().toString();
        String checkCodeBase64=captcha.toBase64();

        redisUtils.setex(Constants.REDIS_KEY_CHECK_CODE+checkCodeKey,code,Constants.REDIS_TIME_ONE_MINUTE);

        Map<String,String> result=new HashMap<>();
        result.put("checkCode",checkCodeBase64);
        result.put("checkCodeKey",checkCodeKey);

        return getSuccessResponseVO(result);
    }

    //注册
    @RequestMapping("/register")
    ResponseVO register(@NotEmpty String checkCodeKey,
                        @NotEmpty @Email  String email,
                        @NotEmpty @Pattern(regexp = Constants.REGEX_PASSWORD)String password,
                        @NotEmpty String nickName,
                        @NotEmpty String checkCode){
        try{
            //验证码错误
           if(!checkCode.equals(redisUtils.get(Constants.REDIS_KEY_CHECK_CODE+checkCodeKey))){
               return getBusinessErrorResponseVO(new BusinessException("图片验证码不正确！"),null);
           }
           userInfoService.register(email,nickName,password);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE+checkCodeKey);
        }
        return getSuccessResponseVO(null);
    }
    //登录
    @RequestMapping("/login")
    ResponseVO login(@NotEmpty String checkCodeKey,
                     @NotEmpty String email,
                     @NotEmpty String password,
                     @NotEmpty String checkCode){
        try{
            if(!checkCode.equals(redisUtils.get(Constants.REDIS_KEY_CHECK_CODE+checkCodeKey))){
                throw new BusinessException("图片验证码不正确！");
            }
            TokenUserInfoDto tokenUserInfoDto=userInfoService.login(email,password);

            UserInfo userInfo=userInfoService.getUserInfoByUserId(tokenUserInfoDto.getUserId());
            UserInfoVO userInfoVO= CopyTools.copy(userInfo, UserInfoVO.class);
            userInfoVO.setToken(tokenUserInfoDto.getToken());
            userInfoVO.setAdmin(tokenUserInfoDto.getAdmin());

            return getSuccessResponseVO(userInfoVO);

        } catch (Exception e) {
            e.printStackTrace();
            return getBusinessErrorResponseVO((BusinessException) e,null);

        } finally {
            redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE+checkCodeKey);

        }

    }
    //系统配置页面
    @GlobalInterceptor
    @RequestMapping("/getSysSetting")
    ResponseVO getSysSetting(){
        return getSuccessResponseVO(redisComponent.getSysSetting());
    }
}
