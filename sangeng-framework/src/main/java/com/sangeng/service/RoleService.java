package com.sangeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Role;
import com.sangeng.domain.vo.RoleStatusVo;
import com.sangeng.domain.vo.RoleVo2;
import com.sangeng.domain.vo.RoleVo3;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 角色信息表(Role)表服务接口
 *
 * @author makejava
 * @since 2023-07-10 10:24:44
 */
public interface RoleService extends IService<Role> {

    List<String> selectRoleKeyByUserId(Long id);

    ResponseResult rolelist(Integer pageNum, Integer pageSize, String roleName, String status);

    ResponseResult changeStatus(RoleStatusVo roleStatusVo);

    ResponseResult addRole(RoleVo2 roleVo2);

    ResponseResult roleDetail(Long id);

    ResponseResult updateRole(RoleVo3 roleVo3);

    ResponseResult listAllRole();
}
