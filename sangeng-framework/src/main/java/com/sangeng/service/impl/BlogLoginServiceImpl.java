package com.sangeng.service.impl;


import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.LoginUser;
import com.sangeng.domain.entity.User;
import com.sangeng.domain.vo.BlogUserLoginVo;
import com.sangeng.domain.vo.UserInfoVo;
import com.sangeng.service.BlogLoginService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.JwtUtil;
import com.sangeng.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BlogLoginServiceImpl implements BlogLoginService {

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
        redisCache.setCacheObject("bloglogin:"+userId,loginUser);

        //把token和userinfo封装 返回
        //把User转换成UserInfoVO
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVo.class);
        BlogUserLoginVo vo = new BlogUserLoginVo(jwt,userInfoVo);   //{ token ,userInfo}
        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult logout() {
//        登录时，要和redis中的信息校验，通过则登录成功。    所以退出登录，删除redis中用户信息即可

        //获取token 解析token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        //获取userid
        Long userId = loginUser.getUser().getId();
        // 删除redis中的用户信息
        redisCache.deleteObject("bloglogin:"+userId);

        return ResponseResult.okResult();
    }
}
