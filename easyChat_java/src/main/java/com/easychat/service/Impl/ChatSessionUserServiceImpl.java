package com.easychat.service.Impl;

import java.util.List;
import com.easychat.entity.po.ChatSessionUser;
import com.easychat.query.ChatSessionUserQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.ChatSessionUserService;
import com.easychat.mapper.ChatSessionUserMapper;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.easychat.enums.PageSize;
import com.easychat.query.SimplePage;
/**
  * @Description:会话用户表 Service
  * @Author:刘耿豪
  * @Date:2025/03/25
  */
@Service
public class ChatSessionUserServiceImpl implements ChatSessionUserService{

	@Resource
	private ChatSessionUserMapper<ChatSessionUser,ChatSessionUserQuery> chatSessionUserMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<ChatSessionUser> findListByParam(ChatSessionUserQuery query){
		return this.chatSessionUserMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(ChatSessionUserQuery query){
		return this.chatSessionUserMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<ChatSessionUser> findListByPage(ChatSessionUserQuery query){
	Integer count=this.findCountByParam(query);
	Integer pageSize =query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
	SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
	query.setSimplePage(page);
	List<ChatSessionUser> list=this.findListByParam(query);
		return new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),list);
	}

	/**
	 * 新增
	 */
	public Integer add(ChatSessionUser bean){
		return this.chatSessionUserMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<ChatSessionUser> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.chatSessionUserMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	public Integer addOrUpdateBatch(List<ChatSessionUser> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.chatSessionUserMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据UserIdAndContactId查询对象
	 */
	public ChatSessionUser getChatSessionUserByUserIdAndContactId(String userId,String contactId){
		return this.chatSessionUserMapper.selectByUserIdAndContactId(userId,contactId);
	}

	/**
	 * 根据UserIdAndContactId修改
	 */
	public Integer updateChatSessionUserByUserIdAndContactId(ChatSessionUser bean,String userId,String contactId){
		return this.chatSessionUserMapper.updateByUserIdAndContactId(bean,userId,contactId);
	}

	/**
	 * 根据UserIdAndContactId删除
	 */
	public Integer deleteChatSessionUserByUserIdAndContactId(String userId,String contactId){
		return this.chatSessionUserMapper.deleteByUserIdAndContactId(userId,contactId);
	}

}
