package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.exception.BusinessException;
import com.easychat.query.UserInfoQuery;
import com.easychat.service.UserInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

@RestController("adminUserInfoController")
@RequestMapping("/admin")
public class AdminUserInfoController extends ABaseController{
    @Resource
    private UserInfoService userInfoService;
    //加载用户
    @RequestMapping("/loadUser")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO getUserInfo(UserInfoQuery userInfoQuery){
        userInfoQuery.setOrderBy("create_time desc");
        PaginationResultVO resultVO=userInfoService.findListByPage(userInfoQuery);
        return getSuccessResponseVO(resultVO);
    }
    //更新用户状态
    @RequestMapping("/updateUserStatus")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO updateUserStatus(@NotEmpty Integer status,
                                       @NotEmpty String userId) throws BusinessException {
          userInfoService.updateUserStatus(status,userId);
        return getSuccessResponseVO(null);
    }
    //强制下线
    @RequestMapping("/forceOffLine")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO forceOffLine(@NotEmpty String userId) throws BusinessException {
        userInfoService.forceOffLine(userId);
        return getSuccessResponseVO(null);
    }
}
