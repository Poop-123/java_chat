package com.easychat.entity.vo;

import java.io.Serializable;

public class UserInfoVO implements Serializable {
    private static final long serialVersionUID=-7202325263463L;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 性别
     */
    private Integer sex;

    private Integer joinType;
    /**
     * 个签
     */
    private String personalSignature;
    private String areaCode;
    private String areaName;
    private String token;
    private Boolean admin;
    private String contactStatus;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getJoinType() {
        return joinType;
    }

    public void setJoinType(Integer joinType) {
        this.joinType = joinType;
    }

    public String getPersonalSignature() {
        return personalSignature;
    }

    public void setPersonalSignature(String personalSignature) {
        this.personalSignature = personalSignature;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public String getContactStatus() {
        return contactStatus;
    }

    public void setContactStatus(String contactStatus) {
        this.contactStatus = contactStatus;
    }


}
