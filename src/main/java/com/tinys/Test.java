package com.tinys;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by asus on 2017/7/20.
 */
public class Test {
    public static  void main(String[] args){
        //String json = "{\"data\":\"{\"name\":\"tinys\"}\"}";
        String json = "123";
        JSONObject  jsonObject = JSON.parseObject(json);
        System.out.println(jsonObject.getString("data"));
    }
}
