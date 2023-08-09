package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.User;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.exception.SystemException;
import com.sangeng.service.BlogLoginService;
import com.sangeng.service.impl.BlogLoginServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlogLoginController {

    @Autowired
    private BlogLoginService blogLoginService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user){//@RequestBody获取请求体body中的参数
        //对请求参数做一些合法性的判断
        if(!StringUtils.hasText(user.getUserName())){ //前端传来的username为空
            // 提示 必须传用户
//            throw new RuntimeException();
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return blogLoginService.login(user);
    }


    @PostMapping("/logout")
    public ResponseResult logout(){
            return blogLoginService.logout();
    }

}
