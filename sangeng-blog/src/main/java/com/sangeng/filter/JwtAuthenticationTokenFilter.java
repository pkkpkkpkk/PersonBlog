package com.sangeng.filter;

import com.alibaba.fastjson.JSON;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.LoginUser;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.utils.JwtUtil;
import com.sangeng.utils.RedisCache;
import com.sangeng.utils.WebUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//      登录校验过滤器        定义Jwt认证过滤器  浏览器登录后，再次发送请求时，会携带token，让服务器验证

        //获取请求头中的token
        String token = request.getHeader("token");
        if(!StringUtils.hasText(token)){ //没有token就说明是第一次登录，直接放行
            //说明该接口 不需要登录，直接放行
            filterChain.doFilter(request,response);
            return;   //放行，程序到此结束
        }
        //解析token 获取userid
        Claims claims = null;
        try {
            claims = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            e.printStackTrace();
            //token超时，token非法
            //响应告诉前端重新登录 (响应一个json格式)
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);// 401 请重新登录
            WebUtils.renderString(response, JSON.toJSONString(result)); //转成json，并把json串写到响应体当中
            return;
        }
        String userId = claims.getSubject(); // 拿到userid
        //从redis获取用户信息(如果获取失败，则验证失败)
        LoginUser loginUser = redisCache.getCacheObject("bloglogin:" + userId);
        //如果获取不到
        if (Objects.isNull(loginUser)){
            //说明登录过期，重新登录
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);// 401 请重新登录
            WebUtils.renderString(response, JSON.toJSONString(result)); //转成json，并把json串写到响应体当中
            return;
        }

        //存入SecurityContextHolder
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser,null,null);//未认证，用两个参数，认证过，用三个参数
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

//        都执行完了要 放行，让下面的filter处理
        filterChain.doFilter(request,response);
    }
}
