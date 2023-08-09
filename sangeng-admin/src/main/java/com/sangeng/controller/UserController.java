package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.vo.UserVo2;
import com.sangeng.domain.vo.UserVo4;
import com.sangeng.service.UserRoleService;
import com.sangeng.service.UserService;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;

    @GetMapping("/list")
    public ResponseResult userList(Integer pageNum,Integer pageSize,
                                   String userName,String phonenumber,String status){
        return userService.userList(pageNum,pageSize,userName,phonenumber,status);
    }

    @PostMapping
    public ResponseResult addUser(@RequestBody UserVo2 userVo2){
        return userService.addUser(userVo2);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseResult deleteById(@PathVariable Long id){
        userService.removeById(id);
        userRoleService.removeById(id);
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult userDetailById(@PathVariable Long id){
        return userService.userDetailById(id);
    }

    @PutMapping
    public ResponseResult updateUser(@RequestBody UserVo4 userVo4){
        return userService.updateUser(userVo4);
    }

}
