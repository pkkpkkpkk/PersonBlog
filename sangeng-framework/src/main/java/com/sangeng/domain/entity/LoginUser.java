package com.sangeng.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser implements UserDetails {
//    该类 存储用户信息 + 权限信息
    private User user; //user封装成 LoginUser的成员变量，存储用户信息

    //（后台管理员）用户权限
    private List<String> permission;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //返回一个用户的 权限集合   springsecurity是通过这个方法 获取权限 （可以将permission转换成 该方法类型并返回）
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
