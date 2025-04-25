package com.easychat.entity.po;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
  * @Description:
  * @Author:刘耿豪
  * @Date:2025/03/23
  */
public class UserInfoBeauty implements Serializable{
	/**
	 * 用户
	 */
	 private String userId;

	/**
	 * 邮箱号
	 */
	 private String email;

	/**
	 * 状态
	 */
	 @JsonIgnore
	 private Integer status;

	/**
	 * 
	 */
	 private Integer id;

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
	@Override
	public String toString(){
		return "用户:"+(userId==null?"空":userId)+","+"邮箱号:"+(email==null?"空":email)+","+"状态:"+(status==null?"空":status)+","+":"+(id==null?"空":id);
	}
}