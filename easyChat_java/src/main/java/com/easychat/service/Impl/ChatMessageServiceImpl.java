package com.easychat.service.Impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.easychat.entity.po.ChatMessage;
import com.easychat.query.ChatMessageQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.ChatMessageService;
import com.easychat.mapper.ChatMessageMapper;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.easychat.enums.PageSize;
import com.easychat.query.SimplePage;
/**
  * @Description:聊天消息表 Service
  * @Author:刘耿豪
  * @Date:2025/03/25
  */
@Service
public class ChatMessageServiceImpl implements ChatMessageService{

	@Resource
	private ChatMessageMapper<ChatMessage,ChatMessageQuery> chatMessageMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<ChatMessage> findListByParam(ChatMessageQuery query){
		return this.chatMessageMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(ChatMessageQuery query){
		return this.chatMessageMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<ChatMessage> findListByPage(ChatMessageQuery query){
	Integer count=this.findCountByParam(query);
	Integer pageSize =query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
	SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
	query.setSimplePage(page);
	List<ChatMessage> list=this.findListByParam(query);
		return new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),list);
	}

	/**
	 * 新增
	 */
	public Integer add(ChatMessage bean){
		return this.chatMessageMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<ChatMessage> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.chatMessageMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	public Integer addOrUpdateBatch(List<ChatMessage> listBean){
		if(listBean!=null||listBean.isEmpty()){
			return 0;
		}
		return this.chatMessageMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据MessageId查询对象
	 */
	public ChatMessage getChatMessageByMessageId(Long messageId){
		return this.chatMessageMapper.selectByMessageId(messageId);
	}

	/**
	 * 根据MessageId修改
	 */
	public Integer updateChatMessageByMessageId(ChatMessage bean,Long messageId){
		return this.chatMessageMapper.updateByMessageId(bean,messageId);
	}

	/**
	 * 根据MessageId删除
	 */
	public Integer deleteChatMessageByMessageId(Long messageId){
		return this.chatMessageMapper.deleteByMessageId(messageId);
	}

}
