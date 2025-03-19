package com.easychat.enums;

public enum BeautyAccountStatusEnum {
    NO_USER(0,"未使用"),
    USERD(1,"已使用");
    private Integer status;
    private String desc;

    BeautyAccountStatusEnum(Integer status,String desc ) {
        this.desc = desc;
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static BeautyAccountStatusEnum getByStatus(Integer status){
        for(BeautyAccountStatusEnum item:BeautyAccountStatusEnum.values()){
            if(item.getStatus().equals(status)){
                return item;
            }
        }
        return null;
    }
}
