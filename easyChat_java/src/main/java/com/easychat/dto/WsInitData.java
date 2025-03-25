package com.easychat.dto;

import com.easychat.entity.po.ChatMessage;
import com.easychat.entity.po.ChatSessionUser;

import java.util.List;

public class WsInitData {
    private List<ChatSessionUser> chatSessionUserList;
    private List<ChatMessage> chatMessagesList;
    private Integer applyCount;

    public List<ChatSessionUser> getChatSessionUserList() {
        return chatSessionUserList;
    }

    public void setChatSessionUserList(List<ChatSessionUser> chatSessionUsers) {
        this.chatSessionUserList = chatSessionUsers;
    }

    public List<ChatMessage> getChatMessagesList() {
        return chatMessagesList;
    }

    public void setChatMessagesList(List<ChatMessage> chatMessagesList) {
        this.chatMessagesList = chatMessagesList;
    }

    public Integer getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(Integer applyCount) {
        this.applyCount = applyCount;
    }
}
