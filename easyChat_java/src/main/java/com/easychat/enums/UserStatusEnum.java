package com.easychat.enums;

public enum UserStatusEnum {
    DISABLE(0,"禁用"),
    ENABLE(1,"启用");

    private Integer status;
    private String desc;
    UserStatusEnum(Integer status,String desc) {
        this.desc=desc;
        this.status=status;
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

    public static UserStatusEnum getByStatus(Integer status){
        for(UserStatusEnum item:UserStatusEnum.values()){
            if(item.getStatus().equals(status)){
                return item;
            }
        }
        return null;
    }
}
