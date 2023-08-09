package com.sangeng.service.impl;


import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.LoginUser;
import com.sangeng.domain.entity.User;
import com.sangeng.service.LoginService;
import com.sangeng.utils.JwtUtil;
import com.sangeng.utils.RedisCache;
import com.sangeng.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class SystemLoginServiceImpl implements LoginService {
    @Autowired
    private AuthenticationManager authenticationManager;//SecurityConfig配置AuthenticationManager已经注入到容器当中，可以使用
    //AuthenticationManager.ProviderManager的方法进行认证
    @Autowired
    private RedisCache redisCache; //存入redis，需要使用rediscache
    @Override
    public ResponseResult login(User user) {
//        调用ProviderManager的方法进行认证 如果认证通过生成jwt
//      UsernamePasswordAuthenticationToken -> AbstractAuthenticationToken -> Authentication
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());//传用户名 密码
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);//调用认证方法 参数是Authentication，使用它的实现类UsernamePasswordAuthenticationToken
//        authenticationManager 实际上 会默认调用UserDetailsService接口去认证，所以要重写UserDetailsService接口

        //security会使用UserDetailsService实现类中的loadUserByUsername方法进行校验，所以要重写该方法
//        return new LoginUser(user) 返回给 Authentication authenticate,包含了 UserDetails对象(权限信息 + 用户信息)
//        authenticationManager.authenticate() 认证的时候，自动进行密码比对
        //判断是否通过
        if (Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        //获取userid，生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();//获取认证主体 强转成LoginUser
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId); //jwt：把userid加密后的密文 即token

        //把用户信息存入redis   格式(bloglogin:id,loginUser)  (包含权限信息)
        redisCache.setCacheObject("login:"+userId,loginUser);

        //把token封装 返回
        Map<String,String> map = new HashMap<>();
        map.put("token",jwt);
        return ResponseResult.okResult(map);
    }

    @Override
    public ResponseResult logout() {
        //获取当前登录的用户id
        Long userId = SecurityUtils.getUserId();
        //删除redis中对应的值
        redisCache.deleteObject("login:"+userId);

        return ResponseResult.okResult();
    }

}
