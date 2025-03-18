package com.easychat.service.Impl;

import java.util.List;
import com.easychat.entity.po.UserInfo;
import com.easychat.exception.BusinessException;
import com.easychat.query.UserInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.UserInfoService;
import com.easychat.mapper.UserInfoMapper;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.easychat.enums.PageSize;
import com.easychat.query.SimplePage;
/**
  * @Description: Service
  * @Author:刘耿豪
  * @Date:2025/03/18
  */
@Service
public class UserInfoServiceImpl implements UserInfoService{

	@Resource
	private UserInfoMapper<UserInfo,UserInfoQuery> userInfoMapper;
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
	public void register(String email, String nickName, String password) throws BusinessException {
          UserInfo userInfo=this.userInfoMapper.selectByEmail(email);
		  if(null!=userInfo){
			  throw new BusinessException("邮箱账号已存在");
		  }
		  userInfo=new UserInfo();
		  userInfo.setEmail(email);
		  userInfo.setNickName(nickName);
		  userInfo.setPassword(password);
		  this.userInfoMapper.insert(userInfo);

	}

}
