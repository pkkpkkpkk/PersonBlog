package com.sangeng.service.impl;

import com.sangeng.utils.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service("ps")  //对应自定义权限中定义的方法
// @PreAuthorize("@ps.hasPermission('content:category:export')") @GetMapping("/export")
public class PermissionService {
//    定义注解hasPermission
    /**
     * 判断当前用户是否具有permission  （权限）
     * @param permission 要判断的权限
     * @return 是 否  具有权限
     */
    public boolean hasPermission(String permission){
        //如果是超级管理员，直接返回true
        if (SecurityUtils.isAdmin()){
            return true;
        }
        //否则 获取当前登录用户所具有的权限列表，然后 判断是否具有permission
        List<String> perms = SecurityUtils.getLoginUser().getPermission();
        return perms.contains(permission);
    }
}
