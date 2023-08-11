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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BlogLoginServiceImpl implements BlogLoginService {

    @Autowired
    private AuthenticationManager authenticationManager;
    //SecurityConfig类中配置AuthenticationManager注入到容器中，所有这里可以使用AuthenticationManager
    //接口AuthenticationManager  实现类ProviderManager  调用Authentication()进行认证

    @Autowired
    private RedisCache redisCache; //封装好的工具类，使用redis， 本质：public RedisTemplate redisTemplate;

    @Override
    public ResponseResult login(User user) {
//      接口authenticationManager.authenticate()进行认证 如果认证通过生成jwt    (ProviderManager是接口authenticationManager的实现类)
//      UsernamePasswordAuthenticationToken (父)-> AbstractAuthenticationToken -> Authentication(子)
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());//传用户名 密码
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);//调用认证方法 参数是Authentication(实现类UsernamePasswordAuthenticationToken)
//        authenticationManager 实际上 会默认调用UserDetailsService接口去认证，所以要重写UserDetailsService接口

        //security会使用UserDetailsService实现类中的loadUserByUsername方法进行校验，所以要重写该方法
//        return new LoginUser(user) 返回给 Authentication authenticate,包含了 UserDetails对象(权限信息 + 用户信息)

//        authenticationManager.authenticate() 认证的时候，自动进行密码比对
        //判断是否通过
        if (Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        //获取userid，生成token           Authentication对象：(username,password) ->认证(数据库查找用户名密码+用户信息+权限信息) UserDetailsService.loadUserByUsername()实现
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();//获取认证主体 强转成LoginUser
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId); //jwt：把userid加密后的密文 即token    可以解析token拿到userid

        //把用户信息(用户+权限)存入redis   格式(bloglogin:id,loginUser)  (包含用户信息+权限信息)
        redisCache.setCacheObject("bloglogin:"+userId,loginUser);
        /**  redisCache.setCacheObject()方法 是工具类封装好的方法，等同于如下代码

        @Autowired
        public RedisTemplate redisTemplate;

        public <T> void setCacheObject(final String key, final T value){
            redisTemplate.opsForValue().set(key, value);
        }
         */

        //把token和userinfo封装 返回
        //把User转换成UserInfoVO
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVo.class);
        BlogUserLoginVo vo = new BlogUserLoginVo(jwt,userInfoVo);   //{ token ,userInfo}
        // 响应：登录成功后，返回给浏览器一个token，下次再请求的时候，需要带上这个token即可，即可识别出哪个具体的用户
//        为了让用户下回请求时能通过jwt识别出具体的是哪个用户，我们需要把用户信息存入redis，可以把userId作为key，即 （userId,loginUser）
        /** SpringSecurity安全认证    登陆 退出
         * 第一次登陆时，根据userId生成jwt(能反解析出userId)，返回给 浏览器并存储
         *  用户下次再请求时，带上token, 登录校验过滤器会从token中解析出userId,
         *  并通过这个key(userId) 在redis查询 key对应的 value(loginUser).
         *  如果找到，说明已经登录(将认证信息即用户信息 存入SecurityContextHolder)。 如果查不到，说明没有登录，需要重新登录
         *  退出时，从SecurityContextHolder中拿到认证信息loginUser,进而得到userId。 通过这个key(userId) 从redis中删除
         */

        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult logout() {

        //获取token 解析token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();//从SecurityContextHolder中 拿到Authentication对象
        //SecurityContextHolder默认使用ThreadLocal 策略来存储 认证信息. 与线程绑定的策略
        // Spring Security在用户登录时自动绑定认证信息到当前线程，在用户退出时，自动清除当前线程的认证信息。
        //当用户再次发送请求时，携带token，则自动把token存入到 SecurityContextHolder
        LoginUser loginUser = (LoginUser) authentication.getPrincipal(); //获取认证主体
        //获取userid
        Long userId = loginUser.getUser().getId();
        // 删除redis中的用户信息
        redisCache.deleteObject("bloglogin:"+userId);

//        redisTemplate.delete(key);
        return ResponseResult.okResult();
    }
}
