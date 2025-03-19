package com.easychat.service.Impl;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.easyJava.enums.DateTimePatternEnum;
import com.easyJava.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.easychat.entity.po.GroupInfo;
import com.easychat.query.GroupInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.GroupInfoService;
import com.easychat.mapper.GroupInfoMapper;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.easychat.enums.PageSize;
import com.easychat.query.SimplePage;
/**
  * @Description: Service
  * @Author:刘耿豪
  * @Date:2025/03/19
  */
@Service
public class GroupInfoServiceImpl implements GroupInfoService{

	@Resource
	private GroupInfoMapper<GroupInfo,GroupInfoQuery> groupInfoMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<GroupInfo> findListByParam(GroupInfoQuery query){
		return this.groupInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(GroupInfoQuery query){
		return this.groupInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<GroupInfo> findListByPage(GroupInfoQuery query){
	Integer count=this.findCountByParam(query);
	Integer pageSize =query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
	SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
	query.setSimplePage(page);
	List<GroupInfo> list=this.findListByParam(query);
		return new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),list);
	}

	/**
	 * 新增
	 */
	public Integer add(GroupInfo bean){
		return this.groupInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<GroupInfo> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.groupInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	public Integer addOrUpdateBatch(List<GroupInfo> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.groupInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据GroupId查询对象
	 */
	public GroupInfo getGroupInfoByGroupId(String groupId){
		return this.groupInfoMapper.selectByGroupId(groupId);
	}

	/**
	 * 根据GroupId修改
	 */
	public Integer updateGroupInfoByGroupId(GroupInfo bean,String groupId){
		return this.groupInfoMapper.updateByGroupId(bean,groupId);
	}

	/**
	 * 根据GroupId删除
	 */
	public Integer deleteGroupInfoByGroupId(String groupId){
		return this.groupInfoMapper.deleteByGroupId(groupId);
	}

}
