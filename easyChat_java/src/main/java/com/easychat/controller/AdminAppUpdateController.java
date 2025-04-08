package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.exception.BusinessException;
import java.io.IOException;
import com.easychat.entity.po.AppUpdate;
import com.easychat.query.AppUpdateQuery;
import org.springframework.web.bind.annotation.RestController;
import com.easychat.service.AppUpdateService;
import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import com.easychat.entity.vo.ResponseVO;
import org.springframework.web.multipart.MultipartFile;

/**
  * @Description:app发布 Service
  * @Author:刘耿豪
  * @Date:2025/03/23
  */
    @RestController("adminAppUpdateController")
	@RequestMapping("/admin")
    public class AdminAppUpdateController extends ABaseController{

	@Resource
	private AppUpdateService appUpdateService;
	//显示更新列表
	@RequestMapping("/loadUpdateList")
	@GlobalInterceptor(checkAdmin = true)
	public ResponseVO loadUpdateList(AppUpdateQuery query){
		query.setOrderBy("id desc");
		return getSuccessResponseVO(appUpdateService.findListByPage(query));
	}
	//保存更新列表
	@RequestMapping("/saveUpdateList")
	@GlobalInterceptor(checkAdmin = true)
	public ResponseVO saveUpdateList(Integer id,
									 @NotEmpty String version,
									 @NotEmpty String updateDesc,
									 @NotNull Integer fileType,
									 String outerLink,
									 MultipartFile file) throws BusinessException, IOException {
		AppUpdate appUpdate=new AppUpdate();
		appUpdate.setId(id);
		appUpdate.setVersion(version);
		appUpdate.setUpdateDate(updateDesc);
		appUpdate.setFileType(fileType);
		appUpdate.setOuterLink(outerLink);
        appUpdateService.saveUpdate(appUpdate,file);
		return getSuccessResponseVO(null);
	}
	//删除更新
	@RequestMapping("/delUpdate")
	@GlobalInterceptor(checkAdmin = true)
	public ResponseVO delUpdate(@NotEmpty Integer id) throws BusinessException {
		appUpdateService.deleteAppUpdateById(id);
		return getSuccessResponseVO(null);
	}
	//提交更新
	@RequestMapping("/postUpdate")
	@GlobalInterceptor(checkAdmin = true)
	public ResponseVO postUpdate(@NotEmpty Integer id,
								 @NotEmpty Integer status,
								 String grayscaleUid) throws BusinessException {
		appUpdateService.postUpdate(id,status,grayscaleUid);
		return getSuccessResponseVO(null);
	}
}
