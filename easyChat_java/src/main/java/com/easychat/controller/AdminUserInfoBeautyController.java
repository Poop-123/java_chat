package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.po.UserInfoBeauty;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.enums.BeautyAccountStatusEnum;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.exception.BusinessException;
import com.easychat.query.UserInfoBeautyQuery;
import com.easychat.query.UserInfoQuery;
import com.easychat.service.UserInfoBeautyService;
import com.easychat.service.UserInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController("adminUserInfoBeautyController")
@RequestMapping("/admin")
public class AdminUserInfoBeautyController extends ABaseController{
    @Resource
    private UserInfoBeautyService userInfoBeautyService;
    @RequestMapping("/loadBeautyAccountList")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO loadBeautyAccountList(UserInfoBeautyQuery userInfoBeautyQuery){
        userInfoBeautyQuery.setOrderBy("id desc");
        PaginationResultVO resultVO=userInfoBeautyService.findListByPage(userInfoBeautyQuery);
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/saveBeautyAccount")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO saveBeautyAccount(UserInfoBeauty userInfoBeauty) throws BusinessException {
         userInfoBeautyService.saveAccount(userInfoBeauty);
        return getSuccessResponseVO(null);
    }
    @RequestMapping("/delBeautyAccount")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO delBeautyAccount(@NotNull Integer id){
        userInfoBeautyService.deleteUserInfoBeautyById(id);
        return getSuccessResponseVO(null);
    }




//    @RequestMapping("/updateUserStatus")
//    @GlobalInterceptor(checkAdmin = true)
//    public ResponseVO updateUserStatus(@NotNull Integer status,@NotEmpty String userId) throws BusinessException {
//          userInfoBeautyService.updateUserStatus(status,userId);
//        return getSuccessResponseVO(null);
//    }
//
//    @RequestMapping("/forceOffLine")
//    @GlobalInterceptor(checkAdmin = true)
//    public ResponseVO forceOffLine(@NotEmpty String userId) throws BusinessException {
//        userInfoBeautyService.forceOffLine(userId);
//        return getSuccessResponseVO(null);
//    }




}
