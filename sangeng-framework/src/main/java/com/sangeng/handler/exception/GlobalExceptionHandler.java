package com.sangeng.handler.exception;
import com.sangeng.domain.ResponseResult;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
//@ControllerAdvice  //controller中出现了异常，会在这里统一处理
//@ResponseBody    //将处理方法的返回值 放到 响应体中
@Slf4j  //日志
@RestControllerAdvice //@RestControllerAdvice = @ControllerAdvice + @ResponseBody
//处理方法的返回值 都会转换成json放到响应体中
public class GlobalExceptionHandler {

    @ExceptionHandler(SystemException.class) //处理SystemException异常
    public ResponseResult systemExceptionHandler(SystemException e){
        //打印异常信息
        log.error("出现了异常！{}",e);
        //从异常对象中获取提示信息封装返回
        return ResponseResult.errorResult(e.getCode(), e.getMsg());
        //前端返回json对象 { "code":504, "msg":"必需填写用户名" }
    }

    @ExceptionHandler(Exception.class) //处理Exception异常
    public ResponseResult exceptionHandler(Exception e){
        //打印异常信息
        log.error("出现了异常！{}",e);
        //从异常对象中获取提示信息封装返回
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(),e.getMessage());
    }
}
