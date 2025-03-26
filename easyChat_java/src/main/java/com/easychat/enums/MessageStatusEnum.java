package com.easychat.enums;

public enum MessageStatusEnum {
    SENDING(0,"发送中"),
    SENDED(1,"已发送");
    private Integer status;
    private String desc;

    MessageStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }
    public static MessageStatusEnum getByStatus(Integer status){
        for(MessageStatusEnum item:MessageStatusEnum.values()){
            if(item.getStatus().equals(status)){
                return item;
            }
        }
        return null;
    }
    public String getDesc() {
        return desc;
    }

    public Integer getStatus() {
        return status;
    }

}
