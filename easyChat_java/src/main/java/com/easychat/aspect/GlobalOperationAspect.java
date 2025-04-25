package com.easychat.aspect;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.dto.TokenUserInfoDto;
import com.easychat.entity.constants.Constants;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.exception.BusinessException;
import com.easychat.redis.RedisUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component("globalOperationAspect")
public class GlobalOperationAspect {
    @Resource
    private RedisUtils redisUtils;
    private static final Logger logger= LoggerFactory.getLogger(GlobalOperationAspect.class);
    @Before("@annotation(com.easychat.annotation.GlobalInterceptor)")//切点
    public void interceptorDo(JoinPoint joinPoint) throws BusinessException {
        try{
            Method method=((MethodSignature)joinPoint.getSignature()).getMethod();
            GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);
            if(interceptor==null){
                return;
            }
            if(interceptor.checkAdmin()||interceptor.checkLogin()){
                checkLogin(interceptor.checkAdmin());
            }
        }catch (BusinessException e){
            logger.error("全局拦截异常",e);
            throw e;
        }catch (Throwable e){
            logger.error("全局拦截异常",e);
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }


    }
    private void checkLogin(Boolean checkAdmin) throws BusinessException {
        HttpServletRequest request= ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String token=request.getHeader("token");
        if(null==token){
            logger.error("没有token消息");
            throw new BusinessException(ResponseCodeEnum.CODE_901);

        }
        TokenUserInfoDto tokenUserInfoDto=(TokenUserInfoDto) redisUtils.get(Constants.REDIS_KEY_WS_TOKEN+token);
        if(tokenUserInfoDto==null){
            logger.error("redis中没有token消息");

            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        if(checkAdmin&&!tokenUserInfoDto.getAdmin()){

            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
    }
}
