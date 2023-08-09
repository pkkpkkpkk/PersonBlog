package com.sangeng.config;

import com.sangeng.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

//    为啥注入的不是实现类呢? 不传实现类是为了符合开闭原则
    @Autowired
    AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    AccessDeniedHandler accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder(){ //对比密码时 加密方式
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //关闭csrf
                .csrf().disable()
                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 对于登录接口 允许匿名访问
                .antMatchers("/login").anonymous()   //接口必须携带token
                .antMatchers("/logout").authenticated()//退出接口 必须认证，即必须携带token才能 发出退出请求
                .antMatchers("/user/userInfo").authenticated() //这个接口需要认证之后才能访问
//                .antMatchers("/upload").authenticated() //前端vue上传图片时，没有要求token，所以后端不需要认证，不需要传token即可
//                .antMatchers("/link/getAllLink").authenticated() //这个接口需要认证之后才能访问
                // 除上面外的所有请求全部不需要认证即可访问
                .anyRequest().permitAll();

        //配置异常处理器
        http.exceptionHandling()
                        .authenticationEntryPoint(authenticationEntryPoint) //认证失败处理器
                         .accessDeniedHandler(accessDeniedHandler); //授权失败处理器

        http.logout().disable(); //关闭默认 logout功能

        //将过滤器 配置到 UsernamePasswordAuthenticationFilter之前
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        //允许跨域
        http.cors();
    }

    @Override
    @Bean     //暴露ProvideManager方法注入到spring bean容器中
    public AuthenticationManager authenticationManagerBean() throws Exception {
//        这一段配置用于登录时认证，只有使用了这个配置才能自动注入AuthenticationManager,并使用它来进行用户认证
        return super.authenticationManagerBean();
    }
}
