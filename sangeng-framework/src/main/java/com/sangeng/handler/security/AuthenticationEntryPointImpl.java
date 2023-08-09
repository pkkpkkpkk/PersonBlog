package com.sangeng.handler.security;

import com.alibaba.fastjson.JSON;
import com.sangeng.domain.ResponseResult;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.utils.WebUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

//    AuthenticationEntryPoint 认证失败处理器

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        authException.printStackTrace(); //打印异常信息

//        BadCredentialsException
//        InsufficientAuthenticationException

        //认证失败 会 抛出BadCredentialsException异常
        ResponseResult result = null;
        if (authException instanceof BadCredentialsException) {
            result = ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_ERROR.getCode(), authException.getMessage());
        } else if (authException instanceof InsufficientAuthenticationException) {
            result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }else {
            result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR,"认证或授权失败");//给出错误的提示信息
        }

        //转化为json响应给前端 （认证失败后，响应给前端 自定义的 json格式字符串）
        WebUtils.renderString(response, JSON.toJSONString(result));

    }
}
