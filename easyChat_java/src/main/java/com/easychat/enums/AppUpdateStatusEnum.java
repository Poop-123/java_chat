package com.easychat.enums;

public enum AppUpdateStatusEnum {
    INIT(0,""),GRAYSCALE(1,""),ALL(2,"");
    private Integer status;
    private String description;
    AppUpdateStatusEnum(int status,String description) {
        this.status = status;
        this.description = description;
    }
    public Integer getStatus(){return status;};
    public String getDescription(){return description;}
    public static AppUpdateStatusEnum getByStatus(Integer status){
        for(AppUpdateStatusEnum item:AppUpdateStatusEnum.values()){
            if(item.status.equals(status)){
                return item;
            }
        }
        return null;
    }
}
