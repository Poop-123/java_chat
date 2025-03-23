package com.easychat.controller;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.exception.BusinessException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;


@RestControllerAdvice
public class AGlobalExceptionHandlerController extends ABaseController {
    private static final Logger logger= LoggerFactory.getLogger(AGlobalExceptionHandlerController.class);
    @ExceptionHandler(value = Exception.class)
    Object handleException(Exception e, HttpServletRequest request){
        e.printStackTrace();
        logger.error("请求错误，请求地址{}，错误信息"+e,request.getRequestURI());
        ResponseVO ajaxResponse =new ResponseVO();
        //404
        if(e instanceof NoHandlerFoundException){
            ajaxResponse.setCode(ResponseCodeEnum.CODE_404.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_404.getMsg());
            ajaxResponse.setStatus(STATIC_ERROR);
        }
        //业务错误
        else if(e instanceof BusinessException){
            BusinessException biz=(BusinessException) e;

            ajaxResponse.setCode(biz.getCode());
            ajaxResponse.setInfo(biz.getMessage());
            ajaxResponse.setStatus(STATIC_ERROR);
        }
        //参数类型
        else if(e instanceof BindException){
            ajaxResponse.setCode(ResponseCodeEnum.CODE_600.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_600.getMsg());
            ajaxResponse.setStatus(STATIC_ERROR);
        }
        //逐渐冲突
        else if(e instanceof DuplicateKeyException){
            ajaxResponse.setCode(ResponseCodeEnum.CODE_601.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_601.getMsg());
            ajaxResponse.setStatus(STATIC_ERROR);
        }
        else{
            ajaxResponse.setCode(ResponseCodeEnum.CODE_500.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_500.getMsg());
            ajaxResponse.setStatus(STATIC_ERROR);
        }
        return ajaxResponse;
    }


}

