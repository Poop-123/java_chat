package com.easychat.service.Impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.easychat.config.AppConfig;
import com.easychat.dto.MessageSendDto;
import com.easychat.dto.TokenUserInfoDto;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.po.UserInfoBeauty;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.mapper.UserContactMapper;
import com.easychat.mapper.UserInfoBeautyMapper;
import com.easychat.query.UserContactQuery;
import com.easychat.query.UserInfoBeautyQuery;
import com.easychat.query.UserInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.redis.RedisComponent;
import com.easychat.service.ChatSessionUserService;
import com.easychat.service.UserContactService;
import com.easychat.service.UserInfoService;
import com.easychat.mapper.UserInfoMapper;
import javax.annotation.Resource;

import com.easychat.utils.StringTools;
import com.easychat.websocket.MessageHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.easychat.query.SimplePage;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
  * @Description: Service
  * @Author:刘耿豪
  * @Date:2025/03/18
  */
@Service
public class UserInfoServiceImpl implements UserInfoService{
    @Resource
	private AppConfig appConfig;
	@Resource
	private UserInfoMapper<UserInfo,UserInfoQuery> userInfoMapper;
	@Resource
	private UserInfoBeautyMapper <UserInfoBeauty,UserInfoQuery> userInfoBeautyMapper;
	@Resource
	private RedisComponent redisComponent;
    @Resource
    private UserContactMapper userContactMapper;
	@Resource
	private UserContactService userContactService;
    @Resource
	private ChatSessionUserService chatSessionUserService;
	@Resource
	private MessageHandler messageHandler;
	/**
	 * 根据条件查询列表
	 */
	public List<UserInfo> findListByParam(UserInfoQuery query){
		return this.userInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserInfoQuery query){
		return this.userInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery query){
	Integer count=this.findCountByParam(query);
	Integer pageSize =query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
	SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
	query.setSimplePage(page);
	List<UserInfo> list=this.findListByParam(query);
		return new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),list);
	}

	/**
	 * 新增
	 */
	public Integer add(UserInfo bean){
		return this.userInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserInfo> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	public Integer addOrUpdateBatch(List<UserInfo> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据UserId查询对象
	 */
	public UserInfo getUserInfoByUserId(String userId){
		return this.userInfoMapper.selectByUserId(userId);
	}

	/**
	 * 根据UserId修改
	 */
	public Integer updateUserInfoByUserId(UserInfo bean,String userId){
		return this.userInfoMapper.updateByUserId(bean,userId);
	}

	/**
	 * 根据UserId删除
	 */
	public Integer deleteUserInfoByUserId(String userId){
		return this.userInfoMapper.deleteByUserId(userId);
	}

	/**
	 * 根据Email查询对象
	 */
	public UserInfo getUserInfoByEmail(String email){
		return this.userInfoMapper.selectByEmail(email);
	}

	/**
	 * 根据Email修改
	 */
	public Integer updateUserInfoByEmail(UserInfo bean,String email){
		return this.userInfoMapper.updateByEmail(bean,email);
	}

	/**
	 * 根据Email删除
	 */
	public Integer deleteUserInfoByEmail(String email){
		return this.userInfoMapper.deleteByEmail(email);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void register(String email, String nickName, String password) throws BusinessException {

          UserInfo userInfo=this.userInfoMapper.selectByEmail(email);
		  if(null!=userInfo){
			  throw new BusinessException("账号已存在");
		  }

			  String userId= null;
			  UserInfoBeauty beautyAccount=this.userInfoBeautyMapper.selectByEmail(email);
			  //存在未使用的靓号
			  Boolean useBeautyAccount=null!=beautyAccount&& BeautyAccountStatusEnum.NO_USER.getStatus().equals(beautyAccount.getStatus());
			  if(useBeautyAccount){
				  userId= UserContactTypeEnum.USER.getPrefix()+beautyAccount.getUserId();
			  }
			  else {
				  userId=StringTools.getUserId();
			  }
			  Date curDate=new Date();
			  userInfo=new UserInfo();
			  userInfo.setUserId(userId);
			  userInfo.setEmail(email);
			  userInfo.setNickName(nickName);
			  userInfo.setPassword(StringTools.encodeMd5(password));
			  userInfo.setCreateTime(curDate);
			  userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
			  userInfo.setLastOffTime(curDate.getTime());
			  userInfo.setJoinType(JoinTypeEnum.APPLY.getType());
			  this.userInfoMapper.insert(userInfo);
			  if(useBeautyAccount){
				  UserInfoBeauty updateBeauty=new UserInfoBeauty();
				  updateBeauty.setStatus(BeautyAccountStatusEnum.USERD.getStatus());
				  this.userInfoBeautyMapper.updateByUserId(updateBeauty,beautyAccount.getUserId());
			  }
			  // 创建机器人好友
		     userContactService.addContact4Robot(userId);
	}

	@Override
	public TokenUserInfoDto login(String email, String password) throws BusinessException {

		UserInfo userInfo=userInfoMapper.selectByEmail(email);
		if(null==userInfo||!userInfo.getPassword().equals(StringTools.encodeMd5(password))){
			throw new BusinessException("账号或密码错误！");
		}
		if(UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())){
			throw new BusinessException("账号已禁用！");
		}

		// 查询我的联系人
		UserContactQuery contactQuery=new UserContactQuery();
		contactQuery.setUserId(userInfo.getUserId());
		contactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus());
		List<UserContact> contactList=userContactMapper.selectList(contactQuery);
		List<String> contactIdList=contactList.stream().map(item->item.getContactId()).collect(Collectors.toList());

		redisComponent.cleanUserContact(userInfo.getUserId());
		if(!contactIdList.isEmpty()){
			redisComponent.addUserContactBath(userInfo.getUserId(), contactIdList);
		}
		TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto(userInfo);
        //Long lastHeartBeat= redisComponent.getUserHeartBeat(userInfo.getUserId());
		//if(null!=lastHeartBeat){
		//	throw new BusinessException("此账号已在别处登录！请退出后再登录！");
		//}
		//保存登录信息到redis中
		String token=StringTools.encodeMd5(tokenUserInfoDto.getUserId()+StringTools.getRandomString(Constants.LENGTH_20));
		tokenUserInfoDto.setToken(token);
        redisComponent.saveTokenUserInfoDto(tokenUserInfoDto);
		return tokenUserInfoDto;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateUserInfo(UserInfo userInfo, MultipartFile avatarFile, MultipartFile avtarCover) throws IOException {
           if(avatarFile!=null){
			   String baseFolder=appConfig.getProjectFolder()+Constants.FILE_FOLDER_FILE;
			   File targetFileFolder=new File(baseFolder+Constants.FILE_FOLDER_AVATAR_NAME);
			   if(!targetFileFolder.exists()){
				   targetFileFolder.mkdirs();
			   }
			   String filePath=targetFileFolder.getPath()+"/"+userInfo.getUserId()+Constants.IMAGE_SUFFIX;
		       avatarFile.transferTo(new File(filePath));
		   }
		   UserInfo dbInfo=this.userInfoMapper.selectByUserId(userInfo.getUserId());
		   this.userInfoMapper.updateByUserId(userInfo,userInfo.getUserId());
		   String contactNameUpdate=null;
		   if(!dbInfo.getNickName().equals(userInfo.getNickName())){
			   contactNameUpdate=userInfo.getNickName();
		   }
		   if(contactNameUpdate==null){
			   return ;
		   }
		TokenUserInfoDto tokenUserInfoDto = redisComponent.getTokenUserInfoDtoByUserId(userInfo.getUserId());
		tokenUserInfoDto.setNickName(contactNameUpdate);
		   redisComponent.saveTokenUserInfoDto(tokenUserInfoDto);
		   chatSessionUserService.updateRedundancyInfo(contactNameUpdate,userInfo.getUserId());

	}

	@Override
	public void updateUserStatus(Integer status, String userId) throws BusinessException {
		UserStatusEnum userStatusEnum=UserStatusEnum.getByStatus(status);
		if(userStatusEnum==null){
			throw new BusinessException("状态异常！");
		}
		UserInfo userInfo=new UserInfo();
		userInfo.setStatus(userStatusEnum.getStatus());
		this.userInfoMapper.updateByUserId(userInfo,userId);
	}

	@Override
	public void forceOffLine(String userId) {
		MessageSendDto sendDto=new MessageSendDto();
		sendDto.setContactType(UserContactTypeEnum.USER.getType());
		sendDto.setMessageType(MessageTypeEnum.FORCE_OFF_LINE.getType());
		sendDto.setContactId(userId);
		messageHandler.sendMessage(sendDto);

	}

	private TokenUserInfoDto getTokenUserInfoDto(UserInfo userInfo){
		TokenUserInfoDto tokenUserInfoDto=new TokenUserInfoDto();
		tokenUserInfoDto.setUserId(userInfo.getUserId());
		tokenUserInfoDto.setNickName(userInfo.getNickName());
		String adminEmails=appConfig.getAdminEmails();
		if(!StringTools.isEmpty(adminEmails)&& ArrayUtils.contains(adminEmails.split(","),userInfo.getEmail())){
			tokenUserInfoDto.setAdmin(true);
		}else {
			tokenUserInfoDto.setAdmin(false);
		}
		return tokenUserInfoDto;
	}

}
