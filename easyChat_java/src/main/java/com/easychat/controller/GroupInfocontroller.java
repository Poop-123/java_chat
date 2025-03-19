package com.easychat.controller;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.easychat.enums.DateTimePatternEnum;
import com.easychat.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.easychat.entity.po.GroupInfo;
import com.easychat.query.GroupInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import org.springframework.web.bind.annotation.RestController;
import com.easychat.service.GroupInfoService;
import javax.annotation.Resource;
import com.easychat.enums.PageSize;
import com.easychat.query.SimplePage;
import org.springframework.web.bind.annotation.RequestMapping;
import com.easychat.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestBody;

/**
  * @Description: Service
  * @Author:刘耿豪
  * @Date:2025/03/19
  */
@RestController("groupInfoController")
	@RequestMapping("/groupInfo")
public class GroupInfocontroller extends ABaseController{
	@Resource
	private GroupInfoService groupInfoService;

}
