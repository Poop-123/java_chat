package com.easychat.entity.po;

import java.io.Serializable;
import java.util.Date;

import com.easychat.utils.StringTools;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.easychat.enums.DateTimePatternEnum;
import com.easychat.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
  * @Description:app发布
  * @Author:刘耿豪
  * @Date:2025/03/23
  */
public class AppUpdate implements Serializable{
	/**
	 * 自增加
	 */
	 private Integer id;

	/**
	 * 版本号
	 */
	 private String version;

	/**
	 * 更新描述
	 */
	 private String updateDate;

	/**
	 * 创建时间
	 */
	 @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	 @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	 private Date createTime;

	/**
	 * 负责运行日志或发布 1:全局发布
	 */

	 private Integer status;

	/**
	 * 完整uid
	 */
	 private String greyscaleId;
     private String[] updateDescArray;
	/**
	 * 文件类型0:本地文件 1:外延
	 */
	 private Integer fileType;

	/**
	 * 外延地址
	 */
	 private String outerLink;

	public String[] getUpdateDescArray() {
		if(!StringTools.isEmpty(updateDate)){
			return updateDate.split("\\|");
		}
		return updateDescArray;
	}

	public void setUpdateDescArray(String[] updateDescArray) {
		this.updateDescArray = updateDescArray;
	}

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
	public void setOuterLink(String outerLink){
		this.outerLink=outerLink;
	}
	public String getOuterLink(){
		return this.outerLink;
	}
	@Override
	public String toString(){
		return "自增加:"+(id==null?"空":id)+","+"版本号:"+(version==null?"空":version)+","+"更新描述:"+(updateDate==null?"空":updateDate)+","+"创建时间:"+(createTime==null?"空":DateUtils.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+","+"负责运行日志或发布 1:全局发布:"+(status==null?"空":status)+","+"完整uid:"+(greyscaleId==null?"空":greyscaleId)+","+"文件类型0:本地文件 1:外延:"+(fileType==null?"空":fileType)+","+"外延地址:"+(outerLink==null?"空":outerLink);
	}
}