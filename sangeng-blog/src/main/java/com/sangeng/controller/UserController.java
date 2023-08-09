package com.sangeng.controller;

import com.sangeng.annotation.SystemLog;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.User;
import com.sangeng.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @GetMapping("/userInfo")
    public ResponseResult userInfo(){ //get方式如有参数，参数在请求头head中
        return userService.userInfo();
    }
//虽然请求地址相同，但是请求方式不一样，可以

    @PutMapping("/userInfo")
    @SystemLog(businessName = "更新用户信息") //希望这个接口能打印日志 加上自定义注解即可。
    // 还要求打印这个业务的名字-通过这个注解的属性来指定业务名字
    public ResponseResult updateUserInfo(@RequestBody User user){ //@RequestBody获取请求体body中的参数
        return userService.updateUserInfo(user);
    }

    @PostMapping("/register")
    public ResponseResult register(@RequestBody User user){//用User类接收
        return userService.register(user);
    }
}
