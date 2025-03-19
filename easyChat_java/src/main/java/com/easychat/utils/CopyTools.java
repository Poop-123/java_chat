package com.easychat.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class CopyTools {
    public static <T,S> List<T> copyList(List<S> sList,Class<T> classz){
        List<T> list=new ArrayList<T>();
        for(S s:sList){
            T t=null;
            try{
                t= classz.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            BeanUtils.copyProperties(s,t);
            list.add(t);
        }
        return list;

    }
    public static <T,S> T copy(S s,Class<T> classz){
        T t=null;
        try{
            t= classz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        BeanUtils.copyProperties(s,t);
        return t;
    }
}
