package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.exception.BusinessException;
import com.easychat.query.GroupInfoQuery;
import com.easychat.service.GroupInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

@RestController("adminGroupController")
@RequestMapping("/admin")
public class AdminGroupController extends ABaseController{
    @Resource
    private GroupInfoService groupInfoService;
    //加载群组
    @RequestMapping("/loadGroup")
    @GlobalInterceptor(checkAdmin=true)
    public ResponseVO loadGroup(GroupInfoQuery query){
        query.setOrderBy("create_time desc");
        query.setQueryMemberCount(true);
        query.setQueryGroupOwnerName(true);
        PaginationResultVO resultVO=this.groupInfoService.findListByPage(query);
        return getSuccessResponseVO(null);
    }
    //解散群组
    @RequestMapping("/dissolutionGroup")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO dissolutionGroup(@NotEmpty String groupId) throws BusinessException {
          GroupInfo groupInfo= groupInfoService.getGroupInfoByGroupId(groupId);
          if(null==groupInfo) {
              throw new BusinessException(ResponseCodeEnum.CODE_600);
          }
              groupInfoService.dissolutionGroup(groupInfo.getGroupOwnerId(),groupId);
              return getSuccessResponseVO(null);


    }
}
