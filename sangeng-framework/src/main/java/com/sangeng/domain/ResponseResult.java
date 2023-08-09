package com.sangeng.domain;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sangeng.enums.AppHttpCodeEnum;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> implements Serializable {
//    ResponseResult<T> 作用： <T>指定ResponseResult类中 字段data的数据类型 为泛型
    private Integer code;  //编码
    private String msg;    //message
    private T data;       // 泛型， 传入data什么类型，data就是什么类型

    // 返回 { 200; "操作成功"; }    AppHttpCodeEnum枚举类中 SUCCESS(200,"操作成功"),
    public ResponseResult() { //如果什么参数都不传，则返回 { 200; "操作成功"; }
        this.code = AppHttpCodeEnum.SUCCESS.getCode();
        this.msg = AppHttpCodeEnum.SUCCESS.getMsg();
    }
    // 返回 { code; data; }
    public ResponseResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }
    // 返回 { code; msg; data;}
    public ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    // 返回 { code; msg;}
    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ResponseResult errorResult(int code, String msg) {//errorResult 出错方法
        ResponseResult result = new ResponseResult();
        return result.error(code, msg);
    }
    public static ResponseResult okResult() { //errorResult 正常方法
        ResponseResult result = new ResponseResult();
        return result;
    }
    public static ResponseResult okResult(int code, String msg) {//errorResult 正常方法
        ResponseResult result = new ResponseResult();
        return result.ok(code, null, msg);
    }
    //errorResult 正常方法
    public static ResponseResult okResult(Object data) {//传入什么类型，则泛型T就为什么类型
        ResponseResult result = setAppHttpCodeEnum(AppHttpCodeEnum.SUCCESS, AppHttpCodeEnum.SUCCESS.getMsg());
        if(data!=null) {
            result.setData(data);
        }
        return result;
    }
    public static ResponseResult errorResult(AppHttpCodeEnum enums){
        return setAppHttpCodeEnum(enums,enums.getMsg());
    }
    public static ResponseResult errorResult(AppHttpCodeEnum enums, String msg){
        return setAppHttpCodeEnum(enums,msg);
    }
    public static ResponseResult setAppHttpCodeEnum(AppHttpCodeEnum enums){
        return okResult(enums.getCode(),enums.getMsg());
    }
    private static ResponseResult setAppHttpCodeEnum(AppHttpCodeEnum enums, String msg){
        return okResult(enums.getCode(),msg);
    }
    public ResponseResult<?> error(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }
    public ResponseResult<?> ok(Integer code, T data) {
        this.code = code;
        this.data = data;
        return this;
    }
    public ResponseResult<?> ok(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        return this;
    }

    public ResponseResult<?> ok(T data) {
        this.data = data;
        return this;
    }
    //  getter和  setter
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}