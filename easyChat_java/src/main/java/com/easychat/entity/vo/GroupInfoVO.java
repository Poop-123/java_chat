package com.easychat.entity.vo;

import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.po.UserContact;

import java.util.List;

public class GroupInfoVO {
    private GroupInfo groupInfo;
    private List<UserContact> userContactList;
    private List<UserContact> getUserContactList(){return userContactList;}

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public void setUserContactList(List<UserContact> userContactList) {
        this.userContactList = userContactList;
    }
}
