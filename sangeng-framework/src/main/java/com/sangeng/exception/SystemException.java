package com.sangeng.exception;
import com.sangeng.enums.AppHttpCodeEnum;
public class SystemException extends RuntimeException{
    private int code;
    private String msg;
    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
    public SystemException(AppHttpCodeEnum httpCodeEnum) {
        //通过枚举类 得到 其中的的常量  填充code,msg
        super(httpCodeEnum.getMsg()); //调用父类的super方法，必须写
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg();
    }
    // throw new SystemException(507,"必须填写用户名")  抛出异常是传入参数
    public SystemException(String message, int code, String msg) {
        super(message); //调用父类的super方法，必须写
        this.code = code;
        this.msg = msg;
    }
}
