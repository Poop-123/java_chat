package com.easychat.service.Impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.easychat.entity.po.UserContactApply;
import com.easychat.query.UserContactApplyQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.UserContactApplyService;
import com.easychat.mapper.UserContactApplyMapper;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.easychat.enums.PageSize;
import com.easychat.query.SimplePage;
/**
  * @Description:用户联系人表 Service
  * @Author:刘耿豪
  * @Date:2025/03/19
  */
@Service
public class UserContactApplyServiceImpl implements UserContactApplyService{

	@Resource
	private UserContactApplyMapper<UserContactApply,UserContactApplyQuery> userContactApplyMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<UserContactApply> findListByParam(UserContactApplyQuery query){
		return this.userContactApplyMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserContactApplyQuery query){
		return this.userContactApplyMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserContactApply> findListByPage(UserContactApplyQuery query){
	Integer count=this.findCountByParam(query);
	Integer pageSize =query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
	SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
	query.setSimplePage(page);
	List<UserContactApply> list=this.findListByParam(query);
		return new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),list);
	}

	/**
	 * 新增
	 */
	public Integer add(UserContactApply bean){
		return this.userContactApplyMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserContactApply> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.userContactApplyMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	public Integer addOrUpdateBatch(List<UserContactApply> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.userContactApplyMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据ApplyId查询对象
	 */
	public UserContactApply getUserContactApplyByApplyId(Integer applyId){
		return this.userContactApplyMapper.selectByApplyId(applyId);
	}

	/**
	 * 根据ApplyId修改
	 */
	public Integer updateUserContactApplyByApplyId(UserContactApply bean,Integer applyId){
		return this.userContactApplyMapper.updateByApplyId(bean,applyId);
	}

	/**
	 * 根据ApplyId删除
	 */
	public Integer deleteUserContactApplyByApplyId(Integer applyId){
		return this.userContactApplyMapper.deleteByApplyId(applyId);
	}

}
