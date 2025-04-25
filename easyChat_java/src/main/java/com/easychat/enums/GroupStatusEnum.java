package com.easychat.enums;

public enum GroupStatusEnum {
    NORMAL(1,"正常"),
    DISSOLUTION(0,"解散");
    private Integer status;
    private String desc;

    GroupStatusEnum( Integer status,String desc) {
        this.desc = desc;
        this.status = status;
    }
    public static GroupStatusEnum getByStatus(Integer status){
        for(GroupStatusEnum item:GroupStatusEnum.values()){
            if(item.getStatus()==status){
                return item;
            }
        }
        return null;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
