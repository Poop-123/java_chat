package com.easychat.controller;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.entity.vo.ResponseVO;


public class ABaseController {
    protected static final String STATIC_SUCCESS="success";
    protected static final String STATIC_ERROR="error";
    protected <T>ResponseVO getSuccessResponseVO(T t){
        ResponseVO<T> responseVO=new ResponseVO<>();
        responseVO.setStatus(STATIC_SUCCESS);
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setData(t);
        return responseVO;
    }
}

