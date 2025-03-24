package com.easychat.service.Impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.easychat.config.AppConfig;
import com.easychat.entity.constants.Constants;
import com.easychat.enums.AppUpdateFileTypeEnum;
import com.easychat.enums.AppUpdateStatusEnum;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.exception.BusinessException;
import com.easychat.utils.StringTools;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.easychat.enums.DateTimePatternEnum;
import com.easychat.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.easychat.entity.po.AppUpdate;
import com.easychat.query.AppUpdateQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.AppUpdateService;
import com.easychat.mapper.AppUpdateMapper;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.easychat.enums.PageSize;
import com.easychat.query.SimplePage;
import org.springframework.web.multipart.MultipartFile;

/**
  * @Description:app发布 Service
  * @Author:刘耿豪
  * @Date:2025/03/24
  */
@Service
public class AppUpdateServiceImpl implements AppUpdateService{

	@Resource
	private AppUpdateMapper<AppUpdate,AppUpdateQuery> appUpdateMapper;
	@Resource
	private AppConfig appConfig;
	/**
	 * 根据条件查询列表
	 */
	public List<AppUpdate> findListByParam(AppUpdateQuery query){
		return this.appUpdateMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(AppUpdateQuery query){
		return this.appUpdateMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<AppUpdate> findListByPage(AppUpdateQuery query){
	Integer count=this.findCountByParam(query);
	Integer pageSize =query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
	SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
	query.setSimplePage(page);
	List<AppUpdate> list=this.findListByParam(query);
		return new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),list);
	}

	/**
	 * 新增
	 */
	public Integer add(AppUpdate bean){
		return this.appUpdateMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<AppUpdate> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.appUpdateMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	public Integer addOrUpdateBatch(List<AppUpdate> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.appUpdateMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据Id查询对象
	 */
	public AppUpdate getAppUpdateById(Integer id){
		return this.appUpdateMapper.selectById(id);
	}

	/**
	 * 根据Id修改
	 */
	public Integer updateAppUpdateById(AppUpdate bean,Integer id){
		return this.appUpdateMapper.updateById(bean,id);
	}

	/**
	 * 根据Id删除
	 */
	public Integer deleteAppUpdateById(Integer id) throws BusinessException {
		AppUpdate dbInfo=this.getAppUpdateById(id);
		if(!AppUpdateStatusEnum.INIT.getStatus().equals(dbInfo.getStatus())){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		return this.appUpdateMapper.deleteById(id);
	}

	/**
	 * 根据Version查询对象
	 */
	public AppUpdate getAppUpdateByVersion(String version){
		return this.appUpdateMapper.selectByVersion(version);
	}

	/**
	 * 根据Version修改
	 */
	public Integer updateAppUpdateByVersion(AppUpdate bean,String version){
		return this.appUpdateMapper.updateByVersion(bean,version);
	}

	/**
	 * 根据Version删除
	 */
	public Integer deleteAppUpdateByVersion(String version){
		return this.appUpdateMapper.deleteByVersion(version);
	}
     @Override
	public void saveUpdate(AppUpdate appUpdate, MultipartFile file) throws BusinessException, IOException, IOException {
		AppUpdateFileTypeEnum fileTypeEnum=AppUpdateFileTypeEnum.getByType(appUpdate.getFileType());
		 AppUpdateStatusEnum statusEnum=AppUpdateStatusEnum.getByStatus(appUpdate.getStatus());

		 if(null==fileTypeEnum){
			throw new BusinessException(ResponseCodeEnum.CODE_600);

		}
		 if(appUpdate.getId()!=null){
			 AppUpdate dbInfo=this.getAppUpdateById(appUpdate.getId());
			 if(!AppUpdateStatusEnum.INIT.getStatus().equals(dbInfo.getStatus())){
				 throw new BusinessException(ResponseCodeEnum.CODE_600);
			 }
		 }

		 AppUpdateQuery updateQuery=new AppUpdateQuery();
		updateQuery.setOrderBy("id desc");
		updateQuery.setSimplePage(new SimplePage(0,1));
		List<AppUpdate>appUpdateList=appUpdateMapper.selectList(updateQuery);
		if(!appUpdateList.isEmpty()){
			AppUpdate lastest=appUpdateList.get(0);
			Long dbVersion=Long.parseLong(lastest.getVersion().replace(".",""));
			Long currentVersion=Long.parseLong(appUpdate.getVersion().replace(".",""));
			if(appUpdate.getId()==null&&currentVersion<=dbVersion){
				throw new BusinessException("当前版本必须大于历史版本！");
			}
			if(appUpdate.getId()!=null&&currentVersion>=dbVersion&&!appUpdate.getVersion().equals(lastest.getVersion())){
				throw new BusinessException("当前版本必须大于历史版本！");
			}
			AppUpdate versionDb=appUpdateMapper.selectByVersion(appUpdate.getVersion());
			if(appUpdate.getId()!=null&&versionDb!=null&&!versionDb.getId().equals(appUpdate.getId())){
				throw new BusinessException("版本号已存在！");
			}
			if(appUpdate.getId()==null){
				appUpdate.setCreateTime(new Date());
				appUpdate.setStatus(AppUpdateStatusEnum.INIT.getStatus());
				appUpdateMapper.insert(appUpdate);
			}else{
				appUpdateMapper.updateById(appUpdate,appUpdate.getId());
			}
			if(file!=null){
				File folder=new File(appConfig.getProjectFolder()+ Constants.APP_UPDATE_FOLDER);
				if(!folder.exists()){
					folder.mkdirs();
				}
				file.transferTo(new File(folder.getAbsolutePath()+"/"+appUpdate.getId()+Constants.APP_EXE_SUFFIX));
			}
		}

	}

	@Override
	public void postUpdate(Integer id, Integer status, String grayscaleUid) throws BusinessException {
		AppUpdateStatusEnum statusEnum=AppUpdateStatusEnum.getByStatus(status);
		if(null==statusEnum){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if(AppUpdateStatusEnum.GRAYSCALE==statusEnum&& StringTools.isEmpty(grayscaleUid)){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if(AppUpdateStatusEnum.GRAYSCALE!=statusEnum){
			grayscaleUid="";
		}
		AppUpdate appUpdate=new AppUpdate();
		appUpdate.setStatus(status);
		appUpdate.setGreyscaleId(grayscaleUid);
		appUpdateMapper.updateById(appUpdate,id);
	}

	@Override
	public AppUpdate getLastestUpdate(String appVersion, String uid) {
		return appUpdateMapper.selectLatestUpdate(appVersion,uid);
	}


}
