package com.easychat.service.Impl;

import java.util.List;
import com.easychat.entity.po.UserInfoBeauty;
import com.easychat.query.UserInfoBeautyQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.UserInfoBeautyService;
import com.easychat.mapper.UserInfoBeautyMapper;
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
public class UserInfoBeautyServiceImpl implements UserInfoBeautyService{

	@Resource
	private UserInfoBeautyMapper<UserInfoBeauty,UserInfoBeautyQuery> userInfoBeautyMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<UserInfoBeauty> findListByParam(UserInfoBeautyQuery query){
		return this.userInfoBeautyMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserInfoBeautyQuery query){
		return this.userInfoBeautyMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserInfoBeauty> findListByPage(UserInfoBeautyQuery query){
	Integer count=this.findCountByParam(query);
	Integer pageSize =query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
	SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
	query.setSimplePage(page);
	List<UserInfoBeauty> list=this.findListByParam(query);
		return new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),list);
	}

	/**
	 * 新增
	 */
	public Integer add(UserInfoBeauty bean){
		return this.userInfoBeautyMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserInfoBeauty> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.userInfoBeautyMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	public Integer addOrUpdateBatch(List<UserInfoBeauty> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.userInfoBeautyMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据UserId查询对象
	 */
	public UserInfoBeauty getUserInfoBeautyByUserId(Integer userId){
		return this.userInfoBeautyMapper.selectByUserId(userId);
	}

	/**
	 * 根据UserId修改
	 */
	public Integer updateUserInfoBeautyByUserId(UserInfoBeauty bean,Integer userId){
		return this.userInfoBeautyMapper.updateByUserId(bean,userId);
	}

	/**
	 * 根据UserId删除
	 */
	public Integer deleteUserInfoBeautyByUserId(Integer userId){
		return this.userInfoBeautyMapper.deleteByUserId(userId);
	}

	/**
	 * 根据Email查询对象
	 */
	public UserInfoBeauty getUserInfoBeautyByEmail(String email){
		return this.userInfoBeautyMapper.selectByEmail(email);
	}

	/**
	 * 根据Email修改
	 */
	public Integer updateUserInfoBeautyByEmail(UserInfoBeauty bean,String email){
		return this.userInfoBeautyMapper.updateByEmail(bean,email);
	}

	/**
	 * 根据Email删除
	 */
	public Integer deleteUserInfoBeautyByEmail(String email){
		return this.userInfoBeautyMapper.deleteByEmail(email);
	}

}
