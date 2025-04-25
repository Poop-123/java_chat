package com.easychat.controller;
import com.easychat.dto.TokenUserInfoDto;
import com.easychat.entity.constants.Constants;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.exception.BusinessException;
import com.easychat.redis.RedisUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

//基础controller层 定义了返回对象
public class ABaseController {
    @Resource
    private RedisUtils redisUtils;
    protected static final String STATIC_SUCCESS="success";
    protected static final String STATIC_ERROR="error";
    //成功返回
    protected <T>ResponseVO getSuccessResponseVO(T t){
        ResponseVO<T> responseVO=new ResponseVO<>();
        responseVO.setStatus(STATIC_SUCCESS);
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setData(t);
        return responseVO;
    }
    //业务规则违反
    protected <T> ResponseVO getBusinessErrorResponseVO(BusinessException e,T t){
        ResponseVO vo=new ResponseVO();
        vo.setStatus(STATIC_ERROR);
        if(e.getCode()==null){
            vo.setCode(ResponseCodeEnum.CODE_600.getCode());
        }
        else{
            vo.setCode(e.getCode());
        }
        vo.setInfo(e.getMessage());
        vo.setData(t);
        return vo;
    }
    //服务器错误
    protected <T> ResponseVO getServerErrorResponseVO(T t){
        ResponseVO vo=new ResponseVO();
        vo.setStatus(STATIC_ERROR);
        vo.setCode(ResponseCodeEnum.CODE_500.getCode());
        vo.setInfo(ResponseCodeEnum.CODE_500.getMsg());
        vo.setData(t);
        return vo;
    }
    //获取token对象
    protected TokenUserInfoDto getTokenUserInfoDto(HttpServletRequest request){
        String token=request.getHeader("token");
        TokenUserInfoDto tokenUserInfoDto=(TokenUserInfoDto)redisUtils.get(Constants.REDIS_KEY_WS_TOKEN+token);
        return tokenUserInfoDto;
    }
}

