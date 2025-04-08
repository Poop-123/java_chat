package com.easychat.entity.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseVO<T> {
    private String status;
    private Integer code;
    private String info;
    private T data;
}

