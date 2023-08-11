package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.entity.LoginUser;
import com.sangeng.domain.entity.User;
import com.sangeng.mapper.MenuMapper;
import com.sangeng.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MenuMapper menuMapper;

    /**
     * 通过用户名 查找用户
     * @param username
     * @return UserDetails 对象
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //security会使用UserDetailsService实现类中的loadUserByUsername方法进行校验
        //根据用户名查询用户信息
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,username);
        User user = userMapper.selectOne(queryWrapper);
        //判断是否查到用户  如果没查到抛出异常
        if (Objects.isNull(user)){ throw new RuntimeException("用户不存在"); }
        //返回用户信息    返回的是UserDatails对象，后面才做密码校验   密码SpringSecurity自动校验

        //TODO 查询权限信息封装 如果是后台用户才需要查询权限封装 （前台用户不需要查询权限）
        if (user.getType().equals(SystemConstants.ADMIN)){ //如果是管理员，返回 用户信息+权限信息
            List<String> perms = menuMapper.selectPermsByUserId(user.getId()); //权限列表
            return new LoginUser(user,perms);
        }
        //  定义一个loginUser 实现 UserDetails，即可返回
        return new LoginUser(user,null); //UserDetails对象(权限信息 + 用户信息)
    }
}
