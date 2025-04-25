package com.easychat.query;


/**
  * @Description:
  * @Author:刘耿豪
  * @Date:2025/03/23
  */
public class UserInfoBeautyQuery extends BaseQuery{
	/**
	 * 用户
	 */
	 private String userId;

	/**
	 * 邮箱号
	 */
	 private String email;

	 private String emailFuzzy;

	private String userIdFuzzy;

	/**
	 * 状态
	 */
	 private Integer status;

	/**
	 * 
	 */
	 private Integer id;

	public String getUserIdFuzzy() {
		return userIdFuzzy;
	}

	public void setUserIdFuzzy(String userIdFuzzy) {
		this.userIdFuzzy = userIdFuzzy;
	}

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
	public void setStatus(Integer status){
		this.status=status;
	}
	public Integer getStatus(){
		return this.status;
	}
	public void setId(Integer id){
		this.id=id;
	}
	public Integer getId(){
		return this.id;
	}
	public void setEmailFuzzy(String emailFuzzy){
		this.emailFuzzy=emailFuzzy;
	}
	public String getEmailFuzzy(){
		return this.emailFuzzy;
	}

}