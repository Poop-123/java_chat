package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.config.AppConfig;
import com.easychat.dto.SysSettingDto;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.exception.BusinessException;
import com.easychat.redis.RedisComponent;
import com.easychat.service.GroupInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

@RestController("adminSettingController")
@RequestMapping("/admin")
public class AdminSettingController extends ABaseController{
    @Resource
    private GroupInfoService groupInfoService;
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private AppConfig appConfig;
    //获取系统配置
    @RequestMapping("/getSysSetting")
    @GlobalInterceptor(checkAdmin=true)
    public ResponseVO getSysSetting4Admin(){
        SysSettingDto sysSettingDto=redisComponent.getSysSetting();
      return getSuccessResponseVO(sysSettingDto);
    }
    //保存系统配置
    @RequestMapping("/saveSysSetting")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO saveSysSetting(SysSettingDto sysSettingDto,
                                     MultipartFile robotFile,
                                     MultipartFile robotCover) throws BusinessException, IOException {
        if(robotFile!=null){
            String baseFolder=appConfig.getProjectFolder()+ Constants.FILE_FOLDER_FILE;
            File targetFileFolder=new File(baseFolder+Constants.FILE_FOLDER_AVATAR_NAME);
            if(!targetFileFolder.exists()){
                targetFileFolder.mkdirs();
            }
            String filePath=targetFileFolder.getPath()+"/"+Constants.ROBOT_UID+Constants.IMAGE_SUFFIX;
            robotFile.transferTo(new File(filePath));
            robotCover.transferTo(new File(filePath+Constants.COVER_IMAGE_SUFFIX));
        }
        redisComponent.saveSysSetting(sysSettingDto);
        return getSuccessResponseVO(null);
    }
}
