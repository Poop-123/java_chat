package com.easychat.query;

import java.util.Date;

/**
  * @Description:app发布
  * @Author:刘耿豪
  * @Date:2025/03/23
  */
public class AppUpdateQuery extends BaseQuery{
	/**
	 * 自增加
	 */
	 private Integer id;

	/**
	 * 版本号
	 */
	 private String version;

	 private String versionFuzzy;

	/**
	 * 更新描述
	 */
	 private String updateDate;

	 private String updateDateFuzzy;

	/**
	 * 创建时间
	 */
	 private Date createTime;

	 private String createTimeStart;

	 private String createTimeEnd;

	/**
	 * 负责运行日志或发布 1:全局发布
	 */
	 private Integer status;

	/**
	 * 完整uid
	 */
	 private String greyscaleId;

	 private String greyscaleIdFuzzy;

	/**
	 * 文件类型0:本地文件 1:外延
	 */
	 private Integer fileType;

	/**
	 * 外延地址
	 */
	 private String outerLink;

	 private String outerLinkFuzzy;

	public void setId(Integer id){
		this.id=id;
	}
	public Integer getId(){
		return this.id;
	}
	public void setVersion(String version){
		this.version=version;
	}
	public String getVersion(){
		return this.version;
	}
	public void setUpdateDate(String updateDate){
		this.updateDate=updateDate;
	}
	public String getUpdateDate(){
		return this.updateDate;
	}
	public void setCreateTime(Date createTime){
		this.createTime=createTime;
	}
	public Date getCreateTime(){
		return this.createTime;
	}
	public void setStatus(Integer status){
		this.status=status;
	}
	public Integer getStatus(){
		return this.status;
	}
	public void setGreyscaleId(String greyscaleId){
		this.greyscaleId=greyscaleId;
	}
	public String getGreyscaleId(){
		return this.greyscaleId;
	}
	public void setFileType(Integer fileType){
		this.fileType=fileType;
	}
	public Integer getFileType(){
		return this.fileType;
	}
	public void setCursorLink(String outerLink){
		this.outerLink=outerLink;
	}
	public String getOuterLink(){
		return this.outerLink;
	}
	public void setVersionFuzzy(String versionFuzzy){
		this.versionFuzzy=versionFuzzy;
	}
	public String getVersionFuzzy(){
		return this.versionFuzzy;
	}
	public void setUpdateDateFuzzy(String updateDateFuzzy){
		this.updateDateFuzzy=updateDateFuzzy;
	}
	public String getUpdateDateFuzzy(){
		return this.updateDateFuzzy;
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
	public void setGreyscaleIdFuzzy(String greyscaleIdFuzzy){
		this.greyscaleIdFuzzy=greyscaleIdFuzzy;
	}
	public String getGreyscaleIdFuzzy(){
		return this.greyscaleIdFuzzy;
	}
	public void setOuterLinkFuzzy(String outerLinkFuzzy){
		this.outerLinkFuzzy=outerLinkFuzzy;
	}
	public String getOuterLinkFuzzy(){
		return this.outerLinkFuzzy;
	}

}