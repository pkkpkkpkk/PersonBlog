package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.vo.RoleStatusVo;
import com.sangeng.domain.vo.RoleVo2;
import com.sangeng.domain.vo.RoleVo3;
import com.sangeng.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public ResponseResult roleList(Integer pageNum, Integer pageSize, String roleName, String status){
        return roleService.rolelist(pageNum,pageSize,roleName,status);
    }
    @GetMapping("/listAllRole")
    public ResponseResult listAllRole(){
        return roleService.listAllRole();
    }

    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody RoleStatusVo roleStatusVo){
//        前端都是以字符串传到后端的，请求体中的参数 要先封装成VO然后 通过@RequestBody拿到
        return roleService.changeStatus(roleStatusVo);
    }

    @PostMapping
    public ResponseResult addRole(@RequestBody RoleVo2 roleVo2){
        return roleService.addRole(roleVo2);
    }

    @GetMapping("/{id}")
    public ResponseResult roleDetail(@PathVariable Long id){
        return roleService.roleDetail(id);
    }

    @PutMapping
    public ResponseResult updateRole(@RequestBody RoleVo3 roleVo3){
        return roleService.updateRole(roleVo3);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteRoleById(@PathVariable Long id){
        roleService.removeById(id);
        return ResponseResult.okResult();
    }


}
