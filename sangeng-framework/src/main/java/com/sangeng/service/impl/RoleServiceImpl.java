package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.injector.methods.UpdateById;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Role;
import com.sangeng.domain.entity.RoleMenu;
import com.sangeng.domain.vo.*;
import com.sangeng.mapper.RoleMapper;
import com.sangeng.service.RoleMenuService;
import com.sangeng.service.RoleService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2023-07-10 10:24:45
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMenuService roleMenuService;

    @Override
    public List<String> selectRoleKeyByUserId(Long id) {
        //判断是否为管理员，如果是 返回集合中 只有admin
        if (id == 1L){
            List<String> roleKeys = new ArrayList<>();
            roleKeys.add("admin");
            return roleKeys;
        }
        //否则查询用户所有具有的角色信息  (自己再定义一个mapper，实现下面这个方法selectRoleKeyByUserId(id))
        return getBaseMapper().selectRoleKeyByUserId(id);
    }

    @Override
    public ResponseResult rolelist(Integer pageNum, Integer pageSize, String roleName, String status) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Objects.nonNull(roleName),Role::getRoleName,roleName);
        queryWrapper.eq(Objects.nonNull(status),Role::getStatus,status);
        queryWrapper.orderByAsc(Role::getRoleSort);
        Page<Role> page = new Page<>(pageNum,pageSize);
        page(page,queryWrapper);

        PageVo pageVo = new PageVo();
        pageVo.setRows(page.getRecords());
        pageVo.setTotal(page.getTotal());

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult listAllRole() {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getStatus,0);
        List<Role> roleList = list(queryWrapper);
        return ResponseResult.okResult(roleList);
    }

    //    @Autowired
//    private RoleMapper roleMapper;
    @Override
    public ResponseResult changeStatus(RoleStatusVo roleStatusVo ) {
        Long roleId = roleStatusVo.getRoleId();
        String status = roleStatusVo.getStatus();
        LambdaUpdateWrapper<Role> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Role::getId,roleId);
        updateWrapper.set(Role::getStatus,status);
//        roleMapper.update(null,updateWrapper);
        update(updateWrapper);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult addRole(RoleVo2 roleVo2) {
//        1.插入数据
        Role role = BeanCopyUtils.copyBean(roleVo2, Role.class);
        save(role);  //插入数据成功后，这个role就有 主键id了

//        2.插入role_menu表
//        Long roleId = SecurityUtils.getUserId();
        Long roleId = role.getId(); // id 插入数据成功后，这个role就有 主键id了

        List<RoleMenu> roleMenus = roleVo2.getMenuIds().stream()
                .map(menuId -> new RoleMenu(roleId, Long.parseLong(menuId)))
                .collect(Collectors.toList());
        roleMenuService.saveBatch(roleMenus);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult roleDetail(Long id) {
        Role role = getById(id);
        RoleVo roleVo = BeanCopyUtils.copyBean(role, RoleVo.class);
        return ResponseResult.okResult(roleVo);
    }

    @Override
    @Transactional
    public ResponseResult updateRole(RoleVo3 roleVo3) {
//        1.更新role表
        Role role = BeanCopyUtils.copyBean(roleVo3, Role.class);
        updateById(role);
//        2.更新role_menu表
        //2.1 先删除role_id对应的 role_menu中所有记录
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMenu::getRoleId,role.getId());
        roleMenuService.remove(queryWrapper);
        //2.2 再插入新的role_menu
        List<RoleMenu> roleMenuList = roleVo3.getMenuIds().stream()
                .map(menuId -> new RoleMenu(roleVo3.getId(), Long.parseLong(menuId)))
                .collect(Collectors.toList());
        roleMenuService.saveBatch(roleMenuList); //批量保存

        return ResponseResult.okResult();
    }
}
