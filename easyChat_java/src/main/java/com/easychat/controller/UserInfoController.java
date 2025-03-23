package com.easychat.controller;

import com.alibaba.druid.util.FnvHash;
import com.easychat.annotation.GlobalInterceptor;
import com.easychat.dto.TokenUserInfoDto;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.entity.vo.UserInfoVO;
import com.easychat.service.UserInfoService;
import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.IOException;

@RestController
@RequestMapping("/userInfo")
public class UserInfoController extends ABaseController{
    @Resource
    private UserInfoService userInfoService;
    @RequestMapping("/getUserInfo")
    @GlobalInterceptor
    public ResponseVO getUserInfo(HttpServletRequest request){
        TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
        UserInfo userInfo=userInfoService.getUserInfoByUserId(tokenUserInfoDto.getUserId());
        UserInfoVO userInfoVO= CopyTools.copy(userInfo,UserInfoVO.class);
        return getSuccessResponseVO(userInfoVO);
    }

    @RequestMapping("/saveUserInfo")
    @GlobalInterceptor
    public ResponseVO saveUserInfo(HttpServletRequest request, UserInfo userInfo, MultipartFile avatarFile,MultipartFile avatarCover) throws IOException {
        TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
        userInfo.setUserId(tokenUserInfoDto.getUserId());
        userInfo.setPassword(null);
        userInfo.setStatus(null);
        userInfo.setLastLoginTime(null);
        userInfo.setCreateTime(null);
        this.userInfoService.updateUserInfo(userInfo,avatarFile,avatarCover);
        return getUserInfo(request);
    }

    @RequestMapping("/updatePassword")
    @GlobalInterceptor
    public ResponseVO updatePassword(HttpServletRequest request, @NotEmpty @Pattern(regexp = Constants.REGEX_PASSWORD)String password) throws IOException {
        TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
        UserInfo userInfo=new UserInfo();
        userInfo.setPassword(StringTools.encodeMd5(password));
        this.userInfoService.updateUserInfoByUserId(userInfo,tokenUserInfoDto.getUserId());
        //TODO 重新登陆
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/logout")
    @GlobalInterceptor
    public ResponseVO logout(HttpServletRequest request) throws IOException {
        TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(request);
        //TODO 退出登录，关闭ws连接
        return getSuccessResponseVO(null);
    }


}
