package com.sangeng.controller;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.LoginUser;
import com.sangeng.domain.entity.Menu;
import com.sangeng.domain.entity.User;
import com.sangeng.domain.vo.AdminUserInfoVo;
import com.sangeng.domain.vo.RoutersVo;
import com.sangeng.domain.vo.UserInfoVo;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.exception.SystemException;
import com.sangeng.service.BlogLoginService;
import com.sangeng.service.LoginService;
import com.sangeng.service.MenuService;
import com.sangeng.service.RoleService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.RedisCache;
import com.sangeng.utils.SecurityUtils;
import org.ehcache.core.spi.service.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private RoleService roleService;

    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody User user){//@RequestBody获取请求体body中的参数
        //对请求参数做一些合法性的判断
        if(!StringUtils.hasText(user.getUserName())){ //前端传来的username为空
            // 提示 必须传用户
//            throw new RuntimeException();
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return loginService.login(user);
    }

    //登录之后 前端自动调用 /getInfo 请求，展示该用户的权限列表
    @GetMapping("/getInfo")
    public ResponseResult<AdminUserInfoVo> getInfo(){
        // 获取当前登录的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //获取用户id查询权限信息
        List<String> perms = menuService.selectPermsByUserId(loginUser.getUser().getId());
        //获取用户id查询角色信息
        List<String> roleKeyList = roleService.selectRoleKeyByUserId(loginUser.getUser().getId());
        //获取用户信息
        User user = loginUser.getUser();
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        //封装数据返回
        AdminUserInfoVo adminUserInfoVo = new AdminUserInfoVo(perms,roleKeyList,userInfoVo);
        return ResponseResult.okResult(adminUserInfoVo);

    }
    //登录之后 前端自动调用 /getInfo /getRouters 请求，动态获取前端路由，展示不同的 子菜单（前端实现动态路由）
    @GetMapping("/getRouters")
    public ResponseResult<RoutersVo> getRouters(){
        Long userId = SecurityUtils.getUserId();
        //查询menu ，结果是tree的形式
        List<Menu> menus = menuService.selectRouterMenuTreeByUserId(userId);
        //封装数据返回
        return ResponseResult.okResult(new RoutersVo(menus));
    }

    @PostMapping("/user/logout")
    public ResponseResult logout(){
        return loginService.logout();
    }
}
