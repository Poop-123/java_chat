package com.easychat.query;

import java.util.Date;

/**
  * @Description:
  * @Author:刘耿豪
  * @Date:2025/03/18
  */
public class UserInfoQuery extends BaseQuery{
	/**
	 * 用户id
	 */
	 private String userId;

	 private String userIdFuzzy;

	/**
	 * 邮箱号
	 */
	 private String email;

	 private String emailFuzzy;

	/**
	 * 
	 */
	 private String nickName;

	 private String nickNameFuzzy;

	/**
	 * 0，直接加，1同意后加好友
	 */
	 private Integer joinType;

	/**
	 * 性别 0女1男
	 */
	 private Integer sex;

	/**
	 * 密码
	 */
	 private String password;

	 private String passwordFuzzy;

	/**
	 * 个性签名
	 */
	 private String personalSignature;

	 private String personalSignatureFuzzy;

	/**
	 * 状态
	 */
	 private Integer status;

	/**
	 * 创建时间
	 */
	 private Date createTime;

	 private String createTimeStart;

	 private String createTimeEnd;

	/**
	 * 上次登录时间
	 */
	 private Date lastLoginTime;

	 private String lastLoginTimeStart;

	 private String lastLoginTimeEnd;

	/**
	 * 地区
	 */
	 private String areaName;

	 private String areaNameFuzzy;

	/**
	 * 地区编码
	 */
	 private String areaCode;

	 private String areaCodeFuzzy;

	/**
	 * 离线时间
	 */
	 private Long lastOffTime;

	public void setUserId(String userId){
		this.userId=userId;
	}
	public String getUserId(){
		return this.userId;
	}
	public void setEmail(String email){
		this.email=email;
	}
	public String getEmail(){
		return this.email;
	}
	public void setNickName(String nickName){
		this.nickName=nickName;
	}
	public String getNickName(){
		return this.nickName;
	}
	public void setJoinType(Integer joinType){
		this.joinType=joinType;
	}
	public Integer getJoinType(){
		return this.joinType;
	}
	public void setSex(Integer sex){
		this.sex=sex;
	}
	public Integer getSex(){
		return this.sex;
	}
	public void setPassword(String password){
		this.password=password;
	}
	public String getPassword(){
		return this.password;
	}
	public void setPersonalSignature(String personalSignature){
		this.personalSignature=personalSignature;
	}
	public String getPersonalSignature(){
		return this.personalSignature;
	}
	public void setStatus(Integer status){
		this.status=status;
	}
	public Integer getStatus(){
		return this.status;
	}
	public void setCreateTime(Date createTime){
		this.createTime=createTime;
	}
	public Date getCreateTime(){
		return this.createTime;
	}
	public void setLastLoginTime(Date lastLoginTime){
		this.lastLoginTime=lastLoginTime;
	}
	public Date getLastLoginTime(){
		return this.lastLoginTime;
	}
	public void setAreaName(String areaName){
		this.areaName=areaName;
	}
	public String getAreaName(){
		return this.areaName;
	}
	public void setAreaCode(String areaCode){
		this.areaCode=areaCode;
	}
	public String getAreaCode(){
		return this.areaCode;
	}
	public void setLastOffTime(Long lastOffTime){
		this.lastOffTime=lastOffTime;
	}
	public Long getLastOffTime(){
		return this.lastOffTime;
	}
	public void setUserIdFuzzy(String userIdFuzzy){
		this.userIdFuzzy=userIdFuzzy;
	}
	public String getUserIdFuzzy(){
		return this.userIdFuzzy;
	}
	public void setEmailFuzzy(String emailFuzzy){
		this.emailFuzzy=emailFuzzy;
	}
	public String getEmailFuzzy(){
		return this.emailFuzzy;
	}
	public void setNickNameFuzzy(String nickNameFuzzy){
		this.nickNameFuzzy=nickNameFuzzy;
	}
	public String getNickNameFuzzy(){
		return this.nickNameFuzzy;
	}
	public void setPasswordFuzzy(String passwordFuzzy){
		this.passwordFuzzy=passwordFuzzy;
	}
	public String getPasswordFuzzy(){
		return this.passwordFuzzy;
	}
	public void setPersonalSignatureFuzzy(String personalSignatureFuzzy){
		this.personalSignatureFuzzy=personalSignatureFuzzy;
	}
	public String getPersonalSignatureFuzzy(){
		return this.personalSignatureFuzzy;
	}
	public void setCreateTimeStart(String createTimeStart){
		this.createTimeStart=createTimeStart;
	}
	public String getCreateTimeStart(){
		return this.createTimeStart;
	}
	public void setCreateTimeEnd(String createTimeEnd){
		this.createTimeEnd=createTimeEnd;
	}
	public String getCreateTimeEnd(){
		return this.createTimeEnd;
	}
	public void setLastLoginTimeStart(String lastLoginTimeStart){
		this.lastLoginTimeStart=lastLoginTimeStart;
	}
	public String getLastLoginTimeStart(){
		return this.lastLoginTimeStart;
	}
	public void setLastLoginTimeEnd(String lastLoginTimeEnd){
		this.lastLoginTimeEnd=lastLoginTimeEnd;
	}
	public String getLastLoginTimeEnd(){
		return this.lastLoginTimeEnd;
	}
	public void setAreaNameFuzzy(String areaNameFuzzy){
		this.areaNameFuzzy=areaNameFuzzy;
	}
	public String getAreaNameFuzzy(){
		return this.areaNameFuzzy;
	}
	public void setAreaCodeFuzzy(String areaCodeFuzzy){
		this.areaCodeFuzzy=areaCodeFuzzy;
	}
	public String getAreaCodeFuzzy(){
		return this.areaCodeFuzzy;
	}

}