package com.sangeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Menu;

import java.util.List;


/**
 * 菜单权限表(Menu)表服务接口
 *
 * @author makejava
 * @since 2023-07-10 10:16:00
 */
public interface MenuService extends IService<Menu> {

    List<String> selectPermsByUserId(Long id);

    List<Menu> selectRouterMenuTreeByUserId(Long userId);

    ResponseResult menuList(String menuName,
                            String status);

    ResponseResult addMenu(Menu menu);

    ResponseResult menuById(Long id);

    ResponseResult updateMenu(Menu menu);

    ResponseResult treeSelect();

    ResponseResult roleMenuTreeselectById(Long id);
}
