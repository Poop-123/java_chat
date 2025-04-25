package com.easychat.service.Impl;

import java.util.List;
import com.easychat.entity.po.ChatSession;
import com.easychat.query.ChatSessionQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.ChatSessionService;
import com.easychat.mapper.ChatSessionMapper;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.easychat.enums.PageSize;
import com.easychat.query.SimplePage;
/**
  * @Description:会话信息 Service
  * @Author:刘耿豪
  * @Date:2025/03/25
  */
@Service
public class ChatSessionServiceImpl implements ChatSessionService{

	@Resource
	private ChatSessionMapper<ChatSession,ChatSessionQuery> chatSessionMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<ChatSession> findListByParam(ChatSessionQuery query){
		return this.chatSessionMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(ChatSessionQuery query){
		return this.chatSessionMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<ChatSession> findListByPage(ChatSessionQuery query){
	Integer count=this.findCountByParam(query);
	Integer pageSize =query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
	SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
	query.setSimplePage(page);
	List<ChatSession> list=this.findListByParam(query);
		return new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),list);
	}

	/**
	 * 新增
	 */
	public Integer add(ChatSession bean){
		return this.chatSessionMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<ChatSession> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.chatSessionMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	public Integer addOrUpdateBatch(List<ChatSession> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.chatSessionMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据SessionId查询对象
	 */
	public ChatSession getChatSessionBySessionId(String sessionId){
		return this.chatSessionMapper.selectBySessionId(sessionId);
	}

	/**
	 * 根据SessionId修改
	 */
	public Integer updateChatSessionBySessionId(ChatSession bean,String sessionId){
		return this.chatSessionMapper.updateBySessionId(bean,sessionId);
	}

	/**
	 * 根据SessionId删除
	 */
	public Integer deleteChatSessionBySessionId(String sessionId){
		return this.chatSessionMapper.deleteBySessionId(sessionId);
	}

}
