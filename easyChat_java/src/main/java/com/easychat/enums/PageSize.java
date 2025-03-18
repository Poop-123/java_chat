package com.easychat.enums;


public enum PageSize {
    SIZE15(15),SIZE20(20),SIZE30(30),SIZE40(40),SIZE(50);
    int size;
    private PageSize(int size){this.size=size;}
    public int getSize(){return this.size;}
}

