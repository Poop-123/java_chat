package com.easychat.service.Impl;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.easychat.enums.DateTimePatternEnum;
import com.easychat.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.easychat.entity.po.UserContact;
import com.easychat.query.UserContactQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.UserContactService;
import com.easychat.mapper.UserContactMapper;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.easychat.enums.PageSize;
import com.easychat.query.SimplePage;
/**
  * @Description:联系人 Service
  * @Author:刘耿豪
  * @Date:2025/03/19
  */
@Service
public class UserContactServiceImpl implements UserContactService{

	@Resource
	private UserContactMapper<UserContact,UserContactQuery> userContactMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<UserContact> findListByParam(UserContactQuery query){
		return this.userContactMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserContactQuery query){
		return this.userContactMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserContact> findListByPage(UserContactQuery query){
	Integer count=this.findCountByParam(query);
	Integer pageSize =query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
	SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
	query.setSimplePage(page);
	List<UserContact> list=this.findListByParam(query);
		return new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),list);
	}

	/**
	 * 新增
	 */
	public Integer add(UserContact bean){
		return this.userContactMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserContact> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.userContactMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	public Integer addOrUpdateBatch(List<UserContact> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.userContactMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据UserIdAndContactId查询对象
	 */
	public UserContact getUserContactByUserIdAndContactId(String userId,String contactId){
		return this.userContactMapper.selectByUserIdAndContactId(userId,contactId);
	}

	/**
	 * 根据UserIdAndContactId修改
	 */
	public Integer updateUserContactByUserIdAndContactId(UserContact bean,String userId,String contactId){
		return this.userContactMapper.updateByUserIdAndContactId(bean,userId,contactId);
	}

	/**
	 * 根据UserIdAndContactId删除
	 */
	public Integer deleteUserContactByUserIdAndContactId(String userId,String contactId){
		return this.userContactMapper.deleteByUserIdAndContactId(userId,contactId);
	}

}
